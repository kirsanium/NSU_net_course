//
// Created by kirsanium on 03.12.18.
//

#ifndef NET_LAB6_UTILS_H
#define NET_LAB6_UTILS_H

#include <netinet/in.h>
#include "exceptions/ConnectionException.h"

class Utils {
public:
    static void checkSocket(int socket, int *nfds) {
        if (socket >= FD_SETSIZE) {
            throw ConnectionException("Socket number out of range");
        }
        if (socket + 1 > *nfds)
            *nfds = socket + 1;
    }
};
#endif //NET_LAB6_UTILS_H
