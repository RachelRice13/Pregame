package com.example.pregame.Model;

import java.util.Date;

public class Message {
    String sender, message;
    Date date;

    public Message (String sender, String message, Date date) {
        this.sender = sender;
        this.message = message;
        this.date = date;
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

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
}
