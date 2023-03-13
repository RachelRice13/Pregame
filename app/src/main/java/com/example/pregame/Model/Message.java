package com.example.pregame.Model;

public class Message {
    private String sender, message;

    public Message (String sender, String message) {
        this.sender = sender;
        this.message = message;
    }
    public Message () {
        this.sender = "";
        this.message = "";
    }

    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
