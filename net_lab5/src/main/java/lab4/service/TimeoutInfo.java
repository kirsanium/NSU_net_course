package lab4.service;

public class TimeoutInfo {
    private int timeout;
    private String token;

    public TimeoutInfo(String token) {
        this.token = token;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getToken() {
        return token;
    }

    public void incrementTimeout() {
        timeout++;
    }

    public void resetTimeout() {
        timeout = 0;
    }
}
