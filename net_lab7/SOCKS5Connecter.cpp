//
// Created by kirsanium on 15.01.19.
//

#include <string>
#include <sys/socket.h>
#include <iostream>
#include "SOCKS5Connecter.h"

SOCKS5Connecter::SOCKS5Connecter(int client_sock) : client_sock_(client_sock), inputbuf_size(0),
															  outputbuf_size(0), firstStepCompleted(
				false), port(0), server_port(server_port), drop(false), domain(false) {}

int SOCKS5Connecter::exchangeData(const fd_set &rdfds, const fd_set &wrfds) {
	if (drop) {
		return 0;
	}
	if (FD_ISSET(client_sock_, &rdfds) && outputbuf_size == 0) {
		inputbuf_size = recv(client_sock_, inputbuf, MAX_BUF_SIZE, 0);
		std::cout << "inputbufsize" << inputbuf_size << std::endl;
		if (inputbuf_size == 0) {
			drop = true;
			return true;
		}
		if (inputbuf_size == -1 && errno != EWOULDBLOCK) {
			drop = true;
			return 0;
		}
		if (!firstStepCompleted && inputbuf_size > 0) {
			outputbuf_size = 2;
			outputbuf[0] = 0x05;
			outputbuf[1] = 0xFF;
			for (int i = 0; i < inputbuf[1]; i++) { //auth methods
				if (inputbuf[i + 2] == 0x00) {
					outputbuf[1] = 0x00;
				}
			}
		} else {
			outputbuf_size = 10;
			outputbuf[0] = 0x05;
			outputbuf[1] = 0x00;
			outputbuf[2] = 0x00;
			outputbuf[3] = 0x01;
			//writing my ip
			outputbuf[4] = 127;
			outputbuf[5] = 0;
			outputbuf[6] = 0;
			outputbuf[7] = 1;
			//writing my port
			outputbuf[8] = ((unsigned char *) &port)[0];
			outputbuf[9] = ((unsigned char *) &port)[1];
			if (inputbuf[0] != 0x05) { //if SOCKS4
				outputbuf[1] = 0x07;
			}
			if (inputbuf[1] != 0x01) { //if operation is not supported
				outputbuf[1] = 0x07;
			}
			if (inputbuf[3] == 0x04) { //if ipv6
				outputbuf[1] = 0x08;
			}
			if (inputbuf[3] == 0x03) { //if domain
				int k = inputbuf[4];
				domain = true;
				for (int i = 0; i < k; i++) {
					addr.push_back(inputbuf[i + 5]);
				}
				((unsigned char *) &port)[1] = inputbuf[k + 5];
				((unsigned char *) &port)[0] = inputbuf[k + 6];
			}
			if (inputbuf[3] == 0x01) {//if ipv4
				domain = false;
				addr += std::to_string(inputbuf[4]) + '.' + std::to_string(inputbuf[5]) + '.' +
						std::to_string(inputbuf[6]) + '.' + std::to_string(inputbuf[7]);
				((unsigned char *) &port)[1] = inputbuf[8];
				((unsigned char *) &port)[0] = inputbuf[9];
			}
		}
	}
	if (FD_ISSET(client_sock_, &wrfds) && outputbuf_size != 0) {
		int err = send(client_sock_, outputbuf, outputbuf_size, 0);
		outputbuf_size = 0;
		if (err == 0 || (err == -1 && errno != EWOULDBLOCK)) {
			drop = true;
		}
		if (!firstStepCompleted) {
			firstStepCompleted = true;
		} else {
			return 1;
		}
	}
	return 0;
}

bool SOCKS5Connecter::isDomain() {
	return domain;
}

int SOCKS5Connecter::getPort() {
	return port;
}

std::string SOCKS5Connecter::getHost() {
	return addr;
}

void SOCKS5Connecter::fillFdSet(fd_set &rdfds, fd_set &wrfds) {
	if (!firstStepCompleted) {
		FD_SET(client_sock_, &rdfds);
		FD_SET(client_sock_, &wrfds);
	} else {
		FD_SET(client_sock_, &wrfds);
		if (outputbuf_size == 0) {
			FD_SET(client_sock_, &rdfds);
		}
	}
}

bool SOCKS5Connecter::needToDrop() {
	return drop;
}

