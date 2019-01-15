//
// Created by kirsanium on 03.12.18.
//

#ifndef NET_LAB6_CONNECTIONLIST_H
#define NET_LAB6_CONNECTIONLIST_H

#include <netinet/in.h>
#include "Connection.h"
#include <list>

class PortForwarder {
private:
    std::list<Connection*> connectionList;
    fd_set readset, writeset;
    int *nfds;
    int listenfd;

public:
    PortForwarder(int *nfds, int listenfd): nfds(nfds), listenfd(listenfd){};

    ~PortForwarder() {
        close(listenfd);
		auto connectionIter = connectionList.begin();
        while (connectionIter != connectionList.end()) {
            Connection *connection = *connectionIter;
            ++connectionIter;
            delete(connection);
        }
    }

    Connection *addConnection(int clientSocket, struct sockaddr_in *servaddr);

    void makeSelectMasks();

    void disconnect(Connection *connection);

    void processMasks();

    fd_set getReadset() {
        return readset;
    }

    int forwardSelect() {
        return select(*nfds, &readset, &writeset, NULL, NULL);
    }

    bool listenfdSet() {
        return FD_ISSET(listenfd, &readset);
    }
};


#endif //NET_LAB6_CONNECTIONLIST_H
