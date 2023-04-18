package com.example.pregame.Model;

public class Attendance {
    String fullName, response, date, type;

    public Attendance() {}

    public Attendance(String fullName, String response, String date, String type) {
        this.fullName = fullName;
        this.response = response;
        this.date = date;
        this.type = type;
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
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
}
