package com.example.pregame.Model;

import com.google.firebase.firestore.DocumentReference;

public class Attendance {
    String response, date, type;
    DocumentReference user;

    public Attendance() {}

    public Attendance(String response, String date, String type, DocumentReference user) {
        this.response = response;
        this.date = date;
        this.type = type;
        this.user = user;
    }

    public String getResponse() {
        return response;
    }
    public void setResponse(String response) {
        this.response = response;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public DocumentReference getUser() {
        return user;
    }
    public void setUser(DocumentReference user) {
        this.user = user;
    }
}
