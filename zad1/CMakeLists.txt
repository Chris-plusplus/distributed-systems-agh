cmake_minimum_required(VERSION 3.19)

project(zad1)

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_EXPORT_COMPILE_COMMANDS 1)

# check build type
if(
    "${CMAKE_CONFIGURATION_TYPES}" STREQUAL Debug
    OR "${CMAKE_BUILD_TYPE}" STREQUAL Debug
    OR "${CMAKE_CONFIGURATION_TYPES}" STREQUAL "RelWithDebInfo"
    OR "${CMAKE_BUILD_TYPE}" STREQUAL "RelWithDebInfo"
)
    set(DISTRIBUTED_DEBUG TRUE)
    set(DISTRIBUTED_RELEASE FALSE)

    set(DISTRIBUTED_BUILD_TYPE "Debug")
    
    add_compile_definitions(DISTRIBUTED_DEBUG=1)
    add_compile_definitions(DISTRIBUTED_RELEASE=0)
else()
    set(DISTRIBUTED_DEBUG FALSE)
    set(DISTRIBUTED_RELEASE TRUE)
    
    set(DISTRIBUTED_BUILD_TYPE "Release")
    
    add_compile_definitions(DISTRIBUTED_DEBUG=0)
    add_compile_definitions(DISTRIBUTED_RELEASE=1)
endif()

message(STATUS "Build mode: ${DISTRIBUTED_BUILD_TYPE}")

set(CMAKE_BUILD_TYPE ${DISTRIBUTED_BUILD_TYPE})

add_subdirectory("archimedes")
#include_directories(${CMAKE_INCLUDE_PATH})

include("${PROJECT_SOURCE_DIR}/archimedes/cmake/conan_files/${DISTRIBUTED_BUILD_TYPE}/conan_toolchain.cmake")
include("${PROJECT_SOURCE_DIR}/archimedes/cmake/conan_files/${DISTRIBUTED_BUILD_TYPE}/conandeps_legacy.cmake")

add_executable(server server.cpp)
target_link_libraries(server PRIVATE archimedes)

add_executable(client client.cpp)
target_link_libraries(client PRIVATE archimedes)

