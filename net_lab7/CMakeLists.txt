cmake_minimum_required(VERSION 3.12)
project(net_lab6)

set(CMAKE_CXX_STANDARD 11)

add_executable(net_lab6 main.cpp PortForwarder.cpp PortForwarder.h Connection.cpp Connection.h exceptions/ConnectionException.h Utils.h exceptions/StartUpException.h exceptions/PortForwaderException.h Server.cpp Server.h SOCKS5Connecter.cpp SOCKS5Connecter.h DNSResolver.cpp DNSResolver.h)