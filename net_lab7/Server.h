//
// Created by kirsanium on 15.01.19.
//

#ifndef NET_LAB6_SERVER_H
#define NET_LAB6_SERVER_H

#include <sys/socket.h>
#include "SOCKS5Connecter.h"
#include "DNSResolver.h"


class Server {
private:
	int listen_sockfd;
	int listen_port;
	int fd_max;
	std::list<Connection*> connectionList;
	std::list<std::pair<int, SOCKS5Connecter*>> waitingForHandshakeList;
	std::list<std::pair<std::pair<int, int>, DNSResolver*>> waitingForResolvingList;
	fd_set readset, writeset;

	void listenFor(int listenfd, int listenPort);
	Connection *addConnection(int clientSocket, struct sockaddr_in *servaddr);
	void makeSelectMasks();
	void disconnect(Connection *connection);
	void processMasks();
	fd_set getReadset();
	int forwardSelect();
	bool listenfdSet();
	void prepareSets();
	void processPendingConnections();
	void processWaitingForHandshake();
	void processWaitingForResolving();
	void processConnections();

public:
	explicit Server(int listen_port);
	void run();
};


#endif //NET_LAB6_SERVER_H
