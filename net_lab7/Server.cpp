#include <iostream>
#include "exceptions/StartUpException.h"
#include "exceptions/ConnectionException.h"
#include "Utils.h"
#include "PortForwarder.h"
#include "exceptions/PortForwaderException.h"
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/select.h>
#include <netdb.h>
#include <cstring>
#include <csignal>
#include <cstdlib>
#include "Server.h"
#include <netinet/in.h>
#include <unistd.h>

Server::Server(int listen_port) :
		listen_sockfd(socket(AF_INET, SOCK_STREAM, 0)),
		fd_max(0),
		listen_port(listen_port){

}

void Server::run() {
	signal(SIGPIPE, SIG_IGN);
	listenFor(listen_sockfd, listen_port);
	fcntl(listen_sockfd, F_SETFL, fcntl(listen_sockfd, F_GETFL, 0) | O_NONBLOCK);
	int opt = 1;
	setsockopt(listen_sockfd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));


	int fdReady;

	while (true) {
		prepareSets();
		fdReady = forwardSelect();
		if (fdReady < 0) {
			throw PortForwarderException();
		}
		if (fdReady == 0) {
			continue;
		}
		processPendingConnections();
		processWaitingForHandshake();
		processWaitingForResolving();
		processConnections();
	}
}

void Server::listenFor(int listenfd, int listenPort) {
	Utils::checkSocket(listenfd, &fd_max);

	struct sockaddr_in serverAddr;
	memset(&serverAddr, 0, sizeof(serverAddr));
	serverAddr.sin_family = AF_INET;
	serverAddr.sin_addr.s_addr = htonl(0x7f000001);
	serverAddr.sin_port = htons(listenPort);

	if (bind(listenfd, (struct sockaddr*) &serverAddr, sizeof(serverAddr))) {
		throw ConnectionException("Error while connecting");
	}
	listen(listenfd, SOMAXCONN);

}

void Server::makeSelectMasks() {
	FD_ZERO(&readset);
	FD_ZERO(&writeset);
	FD_SET(listen_sockfd, &readset);

	for (auto connectionIter = connectionList.begin(); connectionIter != connectionList.end(); connectionIter++) {
		Connection *connection = *connectionIter;
		if ((connection->getDataCF() < 0 && connection->getDataFC() <= 0) ||
			(connection->getDataFC() < 0 && connection->getDataCF() <= 0)) {
			connectionIter--;
			disconnect(connection);
		}
		else {
			if (connection->getDataCF() == 0) {
				FD_SET(connection->getClientSocket(), &readset);
				std::cout << "setclr" << std::endl;
			}
			if(connection->getDataFC() == 0) {
				FD_SET(connection->getForwardingSocket(), &readset);
				std::cout << "setfr" << std::endl;
			}
			if(connection->getDataCF() > 0) {
				FD_SET(connection->getForwardingSocket(), &writeset);
				std::cout << "setfw" << std::endl;
			}
			if(connection->getDataFC() > 0) {
				FD_SET(connection->getClientSocket(), &writeset);
				std::cout << "setclw" << std::endl;
			}
		}
	}
}

void Server::disconnect(Connection *connection) {
	connectionList.remove(connection);
	delete(connection);
}

void Server::processMasks() {
	std::cout << connectionList.size() << std::endl;
	ssize_t dataCF;
	ssize_t dataFC;
	int clientSocket;
	int forwardingSocket;
	for (auto connection : connectionList) {
		dataCF = connection->getDataCF();
		dataFC = connection->getDataFC();
		clientSocket = connection->getClientSocket();
		forwardingSocket = connection->getForwardingSocket();
		if ((dataCF == 0) && FD_ISSET(clientSocket, &readset)) {
			connection->readFromClient();
			dataCF = connection->getDataCF();
		}
		if ((dataCF > 0) && FD_ISSET(forwardingSocket, &writeset)) {
			connection->writeToForward();
		}
		if ((dataFC == 0) && FD_ISSET(forwardingSocket, &readset)) {
			connection->readFromForward();
			dataFC = connection->getDataFC();
		}
		if ((dataFC > 0) && FD_ISSET(clientSocket, &writeset)) {
			connection->writeToClient();
		}
	}
}

fd_set Server::getReadset() {
	return readset;
}

int Server::forwardSelect() {
	return select(fd_max, &readset, &writeset, NULL, NULL);
}

bool Server::listenfdSet() {
	return FD_ISSET(listen_sockfd, &readset);
}

void Server::processPendingConnections() {
	if (listenfdSet()) {
		struct sockaddr_in clientAddr;
		int addressLength = sizeof(clientAddr);
		int client_connection = accept(listen_sockfd, (struct sockaddr *)&clientAddr, (socklen_t *)&addressLength);
		if (client_connection < 0) {
			throw PortForwarderException();
		}
		Utils::checkSocket(client_connection, &fd_max);
		waitingForHandshakeList.push_back(std::pair<int, SOCKS5Connecter*>(client_connection, new SOCKS5Connecter(client_connection)));
	}
}

void Server::processWaitingForHandshake() {
	for (auto i = waitingForHandshakeList.begin();i!=waitingForHandshakeList.end();) {
		int code = i->second->exchangeData(readset, writeset);
		std::cout << code << std::endl;
		if (code == 1){
			if(i->second->isDomain()){
				std::pair<int,int> fd_port = std::pair<int,int>(i->first,i->second->getPort());
				waitingForResolvingList.push_back(std::pair<std::pair<int, int>, DNSResolver*>(fd_port,new DNSResolver((unsigned char*)i->second->getHost().c_str())));
			} else{
				sockaddr_in addr;
				memset(&addr, 0, sizeof(addr));
				addr.sin_addr.s_addr = inet_addr(i->second->getHost().c_str());
				addr.sin_port = htons(i->second->getPort());
				connectionList.emplace_back(new Connection(i->first,&addr));
			}
			delete(i->second);
			waitingForHandshakeList.erase(i++);
		} else {
			i++;
		}
	}
}

void Server::processWaitingForResolving() {
	for (auto i = waitingForResolvingList.begin();i!=waitingForResolvingList.end();) {
		if (i->second->exchangeData(readset, writeset)) {
			sockaddr_in addr;
			memset(&addr, 0, sizeof(addr));
			addr.sin_addr.s_addr = i->second->getaddr();
			addr.sin_port = htons(i->first.second);
			addr.sin_family = AF_INET;
			connectionList.push_back(new Connection(i->first.first, &addr));
			delete(i->second);
			waitingForResolvingList.erase(i++);
		} else {
			Utils::checkSocket(i->second->getfd(), &fd_max);
			i++;
		}
	}
}

void Server::prepareSets() {
	FD_ZERO(&readset);
	FD_ZERO(&writeset);
	FD_SET(listen_sockfd, &readset);

	for (auto i = connectionList.begin(); i != connectionList.end();) {
		if (!(*i)->toDisconn()) {
			(*i)->fillSets(readset, writeset);
		} else {
			delete(*i);
			connectionList.erase(i++);
			continue;
		}
		++i;
	}
	for (auto i : waitingForHandshakeList){
		i.second->fillFdSet(readset, writeset);
	}
	for(auto i : waitingForResolvingList){
		i.second->fillFdSet(readset, writeset);
		Utils::checkSocket(i.second->getfd(), &fd_max);
	}
}


void Server::processConnections() {
	for (auto &i : connectionList) {
		i->exchangeData(readset, writeset);
	}
}