#pragma once

#include <cstddef>
#include <cstdint>

namespace arch::math {

using Byte = std::byte;

// NOLINTBEGIN(*-identifier-naming)

using i8 = int8_t;
using i16 = int16_t;
using i32 = int32_t;
using i64 = int64_t;

using u8 = uint8_t;
using u16 = uint16_t;
using u32 = uint32_t;
using u64 = uint64_t;

using f32 = float;
using f64 = double;
using fld = long double;

// using float2 = glm::vec2;
// using float3 = glm::vec3;
// using float4 = glm::vec4;
//
// using int2 = glm::ivec2;
// using int3 = glm::ivec3;
// using int4 = glm::ivec4;
//
// using uint2 = glm::uvec2;
// using uint3 = glm::uvec3;
// using uint4 = glm::uvec4;
//
//// NOLINTEND(*-identifier-naming)
//
// using Mat2x2 = glm::mat2;
// using Mat3x3 = glm::mat3;
// using Mat4x4 = glm::mat4;
//
// using Color = glm::vec4;
//
// using Quat = glm::qua<f32>;

} // namespace arch::math
