package com.example.pregame.Model;

public class Training extends TrainingId {
    private String trainingTitle, date, startTime, meetTime, location, details;

    public Training() {
        this.trainingTitle = "";
        this.date = "";
        this.startTime = "";
        this.meetTime = "";
        this.location = "";
        this.details = "";
    }
    public Training(String trainingTitle, String date, String startTime, String meetTime, String location, String details) {
        this.trainingTitle = trainingTitle;
        this.date = date;
        this.startTime = startTime;
        this.meetTime = meetTime;
        this.location = location;
        this.details = details;
    }

    public String getTrainingTitle() {
        return trainingTitle;
    }
    public void setTrainingTitle(String trainingTitle) {
        this.trainingTitle = trainingTitle;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getMeetTime() {
        return meetTime;
    }
    public void setMeetTime(String meetTime) {
        this.meetTime = meetTime;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }
}
