//
// Created by kirsanium on 04.12.18.
//

#ifndef NET_LAB6_PORTFORWADEREXCEPTION_H
#define NET_LAB6_PORTFORWADEREXCEPTION_H

#include <exception>
#include <string>

class PortForwarderException: public std::exception {
private:
    const char *msg = "Error while forwarding";
public:
    virtual const char* what() const throw() {
        return msg;
    }
};

#endif //NET_LAB6_PORTFORWADEREXCEPTION_H
