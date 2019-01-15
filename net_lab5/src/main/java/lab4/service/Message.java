package lab4.service;

public class Message {

    private int messageID;
    private String message;
    private String author;

    public Message(int messageID, String message, String author) {
        this.messageID = messageID;
        this.message = message;
        this.author = author;
    }

    public int getMessageID() {
        return messageID;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }
}
