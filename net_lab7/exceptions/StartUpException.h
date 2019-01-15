//
// Created by kirsanium on 04.12.18.
//

#ifndef NET_LAB6_STARTUPEXCEPTION_H
#define NET_LAB6_STARTUPEXCEPTION_H

#include <exception>
#include <string>

class StartUpException: public std::exception {
private:
    const char *msg = "Usage: listenPort, forwardIP, forwardPort";
public:
    virtual const char* what() const throw() {
        return msg;
    }
};

#endif //NET_LAB6_STARTUPEXCEPTION_H
