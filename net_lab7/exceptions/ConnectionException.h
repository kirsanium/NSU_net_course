//
// Created by kirsanium on 03.12.18.
//

#ifndef NET_LAB6_CONNECTIONEXCEPTION_H
#define NET_LAB6_CONNECTIONEXCEPTION_H

#include <exception>
#include <string>

class ConnectionException: public std::exception {
private:
    char* msg;
public:
    ConnectionException(char* msg): msg(msg){};
    virtual const char* what() const throw() {
        return msg;
    }
};

#endif //NET_LAB6_CONNECTIONEXCEPTION_H
