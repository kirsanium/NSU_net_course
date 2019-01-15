//
// Created by kirsanium on 15.01.19.
//

#ifndef NET_LAB6_SOCK5CONNECTIONMAKER_H
#define NET_LAB6_SOCK5CONNECTIONMAKER_H


#include <sys/types.h>
#include <string>

class SOCKS5Connecter {
private:
	bool drop;
	const static int MAX_BUF_SIZE = 1024;
	int client_sock_;
	unsigned char inputbuf[MAX_BUF_SIZE];
	unsigned char outputbuf[MAX_BUF_SIZE];
	int inputbuf_size;
	int outputbuf_size;
	bool firstStepCompleted;
	bool domain;
	int port;
	std::string addr;
	int server_port;
public:
	bool needToDrop();

	SOCKS5Connecter(int client_sock);

	void fillFdSet(fd_set &rdfds, fd_set &wrfds);

	std::string getHost();

	int getPort();

	bool isDomain();
	int exchangeData(const fd_set &rdfds, const fd_set &wrfds);

};


#endif //NET_LAB6_SOCK5CONNECTIONMAKER_H
