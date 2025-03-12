#include <Net.h>
#include <Mmath.h>
#include <print>
#include "Init.hpp"
#include "Commons.hpp"
#include <iostream>
#include <text/ConvertTo.h>

int main() {
	std::string ipInput;
	std::print("Server IP: ");
	std::cin >> ipInput;

	auto server = net::Host(net::IPv4(ipInput));

	net::TCPSocket tcp;
	try {
		tcp.connect(server, common::port);
	}
	catch (...) {
		return 1;
	}

	int id;
	tcp.recv((char*)&id, sizeof(id));
	std::println("connected as {}", id);

	net::UDPSocket udp;
	udp.bind(common::port + id + 1);

	std::string asciiArtMsg = std::format("{}: [{}]", id, text::convertTo<char>(std::u32string_view(common::asciiArt)));

	std::string varBuf;
	for (;;) {
		if (not tcp.connectedForce()) {
			break;
		}

		std::string input;
		std::getline(std::cin, input);

		if (input.length() == 1) {
			char action = input[0];
			if (action == 'Q') {
				break;
			}
			else if (action == 'U') {
				udp.sendTo(server, common::port, asciiArtMsg);
			}
		}
		else if (input.length() != 0) {
			tcp.send(input);
		}

		while (tcp.dataAvalible()) {
			char buf[128]{};
			int len;
			tcp.recv(buf, sizeof(buf), len);
			varBuf.append(buf, len);

			for (auto n = varBuf.find('\033'); n != std::string::npos; n = varBuf.find('\033')) {
				std::println("{}", std::string_view(varBuf.data(), varBuf.data() + n));
				varBuf.erase(0, n + 1);

			}
		}
		if (udp.dataAvalible()) {
			char buf[sizeof(common::asciiArt) + 128]{};
			udp.recv(buf, sizeof(buf));
			std::println("{}", buf);
		}
	}
}