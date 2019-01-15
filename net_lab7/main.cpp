#include <iostream>
#include "exceptions/StartUpException.h"
#include "exceptions/ConnectionException.h"
#include "Utils.h"
#include "PortForwarder.h"
#include "exceptions/PortForwaderException.h"
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <cstring>
#include <csignal>
#include <cstdlib>
#include "Server.h"

struct sockaddr_in *getForwardAddr(char* forwardName, int forwardPort) {
    struct addrinfo hints;
    struct addrinfo *result;

    memset(&hints, 0, sizeof(struct addrinfo));
    hints.ai_family = AF_INET;    /* Allow IPv4 or IPv6 */
    hints.ai_socktype = SOCK_DGRAM; /* Datagram socket */
    hints.ai_flags = AI_PASSIVE;    /* For wildcard IP address */
    hints.ai_protocol = 0;          /* Any protocol */
    hints.ai_canonname = NULL;
    hints.ai_addr = NULL;
    hints.ai_next = NULL;

    int errorCode = getaddrinfo(forwardName, NULL, &hints, &result);
    if (errorCode != 0) {
        throw ConnectionException("Error while connecting");
    }
    struct sockaddr_in *forwardAddr = (struct sockaddr_in *)(result->ai_addr);
    forwardAddr->sin_port = htons(forwardPort);
    forwardAddr->sin_family = AF_INET;
    return forwardAddr;
}

int main(int argc, char** argv) {

	if (argc != 2) {
		throw StartUpException();
	}

	try {
		int listenPort = atoi(argv[1]);
		Server server(listenPort);
		server.run();
	} catch (std::exception& e) {
		std::cerr << e.what() << std::endl;
	}

	//    try {
//        int nfds;
//        if (argc < 3) {
//            throw StartUpException();
//        }
//
//        int listenPort = atoi(argv[1]);
//        int forwardPort = atoi(argv[3]);
//
//        if (listenPort <= 0 || forwardPort <= 0) {
//            throw StartUpException();
//        }
//
//        struct sockaddr_in *forwardAddr = getForwardAddr(argv[2], forwardPort);
//
//        int listenfd = socket(AF_INET, SOCK_STREAM, 0);
//        listen(listenfd, listenPort, &nfds);
//
//        std::signal(SIGPIPE, SIG_IGN);
//
//        PortForwarder portForwarder(&nfds, listenfd);
//        int fdReady, addressLength;
//        struct sockaddr_in clientAddr;
//
//        while (true) {
//            portForwarder.makeSelectMasks();
//            fdReady = portForwarder.forwardSelect();
//            if (fdReady < 0) {
//                throw PortForwarderException();
//            }
//            if (fdReady == 0) {
//                continue;
//            }
//            portForwarder.processMasks();
//            if (portForwarder.listenfdSet()) {
//                int client_connection;
//                addressLength = sizeof(clientAddr);
//                client_connection = accept(listenfd, (struct sockaddr *)&clientAddr, (socklen_t *)&addressLength);
//                if (client_connection < 0) {
//                    throw PortForwarderException();
//                }
//                Utils::checkSocket(client_connection, &nfds);
//                try {
//                    portForwarder.addConnection(client_connection, forwardAddr);
//                }
//                catch (ConnectionException &e) {
//                    std::cerr << e.what() << std::endl;
//                }
//            }
//        }
//    }
//    catch (std::exception &e) {
//        std::cerr << e.what() << std::endl;
//    }
}