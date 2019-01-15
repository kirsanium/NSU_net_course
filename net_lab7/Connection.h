//
// Created by kirsanium on 03.12.18.
//

#ifndef NET_LAB6_CONNECTION_H
#define NET_LAB6_CONNECTION_H

#include <sys/socket.h>
#include <cstring>
#include <unistd.h>
#include <iostream>
#include <netinet/in.h>
#include <csignal>
#include "exceptions/ConnectionException.h"
#include <fcntl.h>


#define BUFSIZE 4096

class Connection {
private:
    int clientSocket;
    int forwardingSocket;
    int maxTimeout = 64;
    sockaddr_in* serveraddr;
    Connection *prev, *next;
    char bufferCF[BUFSIZE];
    char bufferFC[BUFSIZE];
    ssize_t dataCF;
    ssize_t dataFC;
    bool toDisconnect;
    bool connected;

    void connect() {
        int ret;
        for (int numsec = 1; numsec <= maxTimeout; numsec *= 2) {
            ret = ::connect(forwardingSocket, reinterpret_cast<sockaddr *> (serveraddr), sizeof(*serveraddr));
            connected = true;
            return;
        }
        throw std::runtime_error(std::string("connect: ") + strerror(errno));
    }
public:
    Connection(int clientSocket, sockaddr_in* serveraddr) :
        clientSocket(clientSocket),
        serveraddr(serveraddr),
        prev(NULL),
        next(NULL),
        forwardingSocket(socket(AF_INET, SOCK_STREAM, 0)),
        toDisconnect(false)
    {
        fcntl(forwardingSocket, F_SETFL, fcntl(forwardingSocket, F_GETFL, 0) | O_NONBLOCK);
        connect();
    }

    ~Connection() {
        close(clientSocket);
        close(forwardingSocket);
    }

    bool toDisconn() {
        return toDisconnect;
    }

    void setDisconnect(bool b) {
        toDisconnect = b;
    }

    Connection* getPrevConnection() {
        return prev;
    }

    Connection* getNextConnection() {
        return next;
    }

    void setPrevConnection(Connection* prev) {
        this->prev = prev;
    }

    void setNextConnection(Connection* next) {
        this->next = next;
    }

    int getClientSocket(){
        return clientSocket;
    }

    int getForwardingSocket() {
        return forwardingSocket;
    }

    ssize_t getDataCF() {
        return dataCF;
    }

    ssize_t getDataFC() {
        return dataFC;
    }

    void setDataCF(ssize_t x) {
        dataCF = x;
    }

    void setDataFC(ssize_t x) {
        dataFC = x;
    }

    void resetBuffers() {
        dataCF = 0;
        dataFC = 0;
        memset(bufferCF, 0, BUFSIZE * sizeof(char));
        memset(bufferFC, 0, BUFSIZE * sizeof(char));
    }

    ssize_t readFromClient() {
        ssize_t bytesRead = read(clientSocket, bufferCF, sizeof(bufferCF));
        dataCF = bytesRead;
        if (dataCF==0) dataCF = -1;
        std::cout << "readfc" << std::endl;
        return bytesRead;
    }

    ssize_t readFromForward() {
        ssize_t bytesRead = read(forwardingSocket, bufferFC, sizeof(bufferFC));
        dataFC = bytesRead;
        if (dataFC==0) dataFC = -1;
        std::cout << "readff" << std::endl;
        return bytesRead;
    }

    ssize_t writeToClient() {
        ssize_t bytesWritten = write(clientSocket, bufferFC, (size_t)dataFC);
        if (bytesWritten == -1) dataCF = -1;
        else dataFC = 0;
        std::cout << "writetc" << std::endl;
    }

    ssize_t writeToForward() {
        ssize_t bytesWritten = write(forwardingSocket, bufferCF, (size_t)dataCF);
        if (bytesWritten == -1) dataFC = -1;
        else dataCF = 0;
        std::cout << "writetf" << std::endl;
    }

    void fillSets(fd_set &readset, fd_set &writeset) {
        if (dataCF == 0) {
            FD_SET(clientSocket, &readset);
        }
        if (dataFC == 0) {
            FD_SET(forwardingSocket, &readset);
        }
        if (dataCF > 0) {
            FD_SET(forwardingSocket, &writeset);
        }
        if (dataFC > 0) {
            FD_SET(clientSocket, &writeset);
        }
    }

    void exchangeData(const fd_set &rdfds, const fd_set &wrfds) {
        signal(SIGPIPE, SIG_IGN);
        if (dataCF == 0 && FD_ISSET(clientSocket, &rdfds)) {
            dataCF = ::recv(clientSocket, &bufferCF[0], BUFSIZE, 0);
            if (dataCF == 0 || (dataCF == -1 && errno != EWOULDBLOCK)) {
                toDisconnect = true;
            }
            if (dataCF == -1 && errno == EWOULDBLOCK) {
                dataCF = 0;
            }
        }
        if (dataFC == 0 && FD_ISSET(forwardingSocket, &rdfds)) {
            dataFC = ::recv(forwardingSocket, &bufferFC[0], BUFSIZE, 0);
            if (dataFC == 0 || (dataFC == -1 && errno != EWOULDBLOCK)) {
                toDisconnect = true;
            }
            if (dataFC == -1 && errno == EWOULDBLOCK) {
                dataFC = 0;
            }
        }
        if (dataCF > 0 && FD_ISSET(forwardingSocket, &wrfds)) {
            ssize_t res = send(forwardingSocket, &bufferCF[0], dataCF, 0);
            if (res == -1) {
                if (errno != EWOULDBLOCK) {
                    toDisconnect = true;
                }
            } else dataCF = 0;
        }
        if (dataFC > 0 && FD_ISSET(clientSocket, &wrfds)) {
            ssize_t res = send(clientSocket, &bufferFC[0], dataFC, 0);
            if (res == -1) {
                if (errno != EWOULDBLOCK) {
                    toDisconnect = true;
                }
            } else dataFC = 0;
        }
    }
};


#endif //NET_LAB6_CONNECTION_H
