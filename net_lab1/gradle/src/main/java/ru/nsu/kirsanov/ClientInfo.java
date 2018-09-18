package ru.nsu.kirsanov;

import java.net.InetAddress;
import java.util.Objects;

public class ClientInfo {
    private InetAddress inetAddress;
    private int timeoutCounter = 0;

    public ClientInfo(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }


    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public int getTimeoutCounter() {
        return timeoutCounter;
    }

    public void incrementTimeoutCounter() {
        timeoutCounter++;
    }

    public void resetTimeoutCounter() {
        timeoutCounter = 0;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ClientInfo))
            return false;
        ClientInfo otherClientInfo = (ClientInfo)other;
        return this.inetAddress.getHostAddress().equals(otherClientInfo.inetAddress.getHostAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(inetAddress.getHostAddress());
    }
}
