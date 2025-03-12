#pragma once

#include <Net.h>

struct NetInit {
	NetInit() noexcept {
		arch::net::Init::init();
	}
	~NetInit() noexcept {
		arch::net::Init::cleanup();
	}
};

static inline NetInit _init;