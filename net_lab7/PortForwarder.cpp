//
// Created by kirsanium on 03.12.18.
//

#include <unistd.h>
#include <iostream>
#include "PortForwarder.h"
#include "Utils.h"


void PortForwarder::disconnect(Connection *connection) {
    connectionList.remove(connection);
    delete(connection);
}

void PortForwarder::makeSelectMasks() {
    FD_ZERO(&readset);
    FD_ZERO(&writeset);
    FD_SET(listenfd, &readset);

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

void PortForwarder::processMasks() {
    std::cout << connectionList.size() << std::endl;
    ssize_t dataCF;
    ssize_t dataFC;
    int clientSocket;
    int forwardingSocket;
    for (std::list<Connection*>::iterator connectionIter = connectionList.begin(); connectionIter != connectionList.end(); connectionIter++) {
        Connection *connection = *connectionIter;
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
