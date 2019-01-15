package lab4;

public class User {

    private String username;
    private boolean isOnline;

    public User(String username) {
        this.username = username;
        this.isOnline = true;
    }

    public String getUsername() {
        return username;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }
}
