project(zad1)

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_EXPORT_COMPILE_COMMANDS 1)

add_subdirectory("archimedes")

add_executable(server server.cpp)
target_link_libraries(server archimedes)

add_executable(client client.cpp)
target_link_libraries(client archimedes)

