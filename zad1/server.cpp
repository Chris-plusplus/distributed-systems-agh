#include <Net.h>
#include <Ecs.h>
#include <Scene.h>
#include <print>
#include "Init.hpp"
#include "Commons.hpp"
#include <queue>
#include <chrono>
#include <csignal>
#include <unordered_map>
#include <ranges>
#include <algorithm>
#include <execution>

using ecs::Domain;

static std::mutex domainMutex{};
static Domain domain{};

static std::mutex printMutex;
template<class... Args>
void lockedPrintln(std::format_string<Args...> fmt, Args&&... args) {
	auto lock = std::lock_guard(printMutex);
	std::println(fmt, std::forward<Args>(args)...);
}

int newID() {
	static std::mutex mutex;
	static int id = 0;
	auto lock = std::lock_guard(mutex);
	return id++;
}

struct SocketComponent {
	static constexpr bool inPlaceComponent = true;

	net::TCPSocket tcp;
};

struct ThreadSafeQueueComponent {
	static constexpr bool inPlaceComponent = true;

	std::unique_ptr<std::mutex> mutex = std::make_unique<std::mutex>();
	std::queue<std::string> queue{};
};

void addTCPMsgToClients(std::string msg, const ecs::Entity sender) noexcept {
	auto lock = std::lock_guard(domainMutex);
	auto id = domain.getComponent<int>(sender);
	for (auto&& [client, q] : domain.view<ThreadSafeQueueComponent>().all()) {
		if (client != sender) {
			auto lock = std::lock_guard(*q.mutex);
			q.queue.push(std::format("{}: '{}'\033", id, msg));
		}
	}
}

void sendUDPMsgToClients(net::UDPSocket& udp, std::string msg, int id) noexcept {
	auto lock = std::lock_guard(domainMutex);
	for (auto&& [client, tcp, clientID] : domain.view<SocketComponent, int>().all()) {
		if (clientID != id) {
			udp.sendTo(tcp.tcp.peer(), common::port + clientID + 1, msg);
		}
	}
}

void clientHandler(std::stop_token stopToken, const ecs::Entity client) noexcept {
	domainMutex.lock();
	auto&& q = domain.getComponent<ThreadSafeQueueComponent>(client);
	auto&& tcp = domain.getComponent<SocketComponent>(client).tcp;
	auto id = domain.getComponent<int>(client);
	domainMutex.unlock();

	tcp.send((const char*)&id, sizeof(id));

	for (; not stopToken.stop_requested(); std::this_thread::sleep_for(16ms)) {
		// CONNECTION STATUS CHECK
		if (not tcp.connectedForce()) {
			auto lock = std::lock_guard(domainMutex);
			lockedPrintln("{} disconnected", id);
			domain.kill(client);
			break;
		}

		// SENDING
		std::string toSend;
		{
			auto lock = std::lock_guard(*q.mutex);
			if (not q.queue.empty()) {
				toSend = std::move(q.queue.front());
				q.queue.pop();
			}
		}
		if (not toSend.empty()) {
			tcp.send(toSend);
		}

		// RECEIVING
		if (tcp.dataAvalible()) {
			char buf[128]{};
			tcp.recv(buf, sizeof(buf));
			lockedPrintln("received '{}' from {}", buf, id);

			addTCPMsgToClients(std::string(buf), client);
		}
	}
}

void UDPhandler(std::stop_token stopToken) {
	auto&& udp = domain.global<net::UDPSocket>();
	udp.bind(common::port);

	for (; not stopToken.stop_requested(); std::this_thread::sleep_for(16ms)) {
		if (udp.dataAvalible()) {
			char buf[sizeof(common::asciiArt) + 128]{};
			udp.recv(buf, sizeof(buf));
			auto idEnd = std::string_view(buf).find(':');

			int id = -1;
			std::from_chars(buf, buf + idEnd, id);
			lockedPrintln("received asciiArt from {}", id);

			sendUDPMsgToClients(udp, std::string(buf), id);
		}
	}
}

void exitHandler(int) {
	auto lock = std::lock_guard(domainMutex);
	auto&& threadSet = domain.global<std::unordered_map<std::jthread::id, std::jthread>>();
	std::for_each(std::execution::par_unseq, threadSet.begin(), threadSet.end(), [](std::pair<const std::jthread::id, std::jthread>& thread) {
		thread.second.request_stop();
		thread.second.join();
	});
	lockedPrintln("stopped all worker threads");

	std::exit(0);
}

int main() {
	auto&& threadSet = domain.global<std::unordered_map<std::jthread::id, std::jthread>>();

	auto listener = net::TCPSocket(common::port);
	listener.listen();

	auto localhost = net::Host::localhost(true);
	std::println("ready at:");
	for (auto&& ip : localhost.ips()) {
		std::println("\t{}", ip.str());
	}

	std::signal(SIGINT, exitHandler);
	std::signal(SIGTERM, exitHandler);

	{
		auto thread = std::jthread(UDPhandler);
		auto id = thread.get_id();
		threadSet.emplace(id, std::move(thread));
	}

	for (;; std::this_thread::sleep_for(16ms)) {
		net::TCPSocket newSock;
		listener.accept(newSock);
		{
			auto lock = std::lock_guard(domainMutex);
			auto newClient = domain.newEntity();
			auto id = newID();

			lockedPrintln("new client: {}", id);

			domain.addComponent<ThreadSafeQueueComponent>(newClient);
			domain.addComponent<SocketComponent>(newClient, std::move(newSock));
			domain.addComponent<int>(newClient, id);

			{
				auto thread = std::jthread(clientHandler, newClient);
				auto id = thread.get_id();
				threadSet.emplace(id, std::move(thread));
			}
		}
	}
}