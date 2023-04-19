package com.example.pregame.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class MatchTraining extends MatchTrainingId implements Serializable {
    String title, date, startTime, location, status, type;
    int teamScore, opponentScore;
    ArrayList<Attendance> attendance;

    public MatchTraining() {}

    public MatchTraining(String title, String date, String startTime, String location, String status, String type, ArrayList<Attendance> attendance) {
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.location = location;
        this.status = status;
        this.type = type;
        this.attendance = attendance;
    }

    public MatchTraining(String title, String date, String startTime, String location, String status, String type, int teamScore, int opponentScore, ArrayList<Attendance> attendance) {
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.location = location;
        this.status = status;
        this.type = type;
        this.teamScore = teamScore;
        this.opponentScore = opponentScore;
        this.attendance = attendance;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
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

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public int getTeamScore() {
        return teamScore;
    }
    public void setTeamScore(int teamScore) {
        this.teamScore = teamScore;
    }

    public int getOpponentScore() {
        return opponentScore;
    }
    public void setOpponentScore(int opponentScore) {
        this.opponentScore = opponentScore;
    }

    public ArrayList<Attendance> getAttendance() {
        return attendance;
    }
    public void setAttendance(ArrayList<Attendance> attendance) {
        this.attendance = attendance;
    }
}
