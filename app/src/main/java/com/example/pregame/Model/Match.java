package com.example.pregame.Model;

public class Match extends MatchId{
    private String matchTitle, date, startTime, meetTime, opponentName, location, status;
    private boolean homeOrAway, attendance;
    private int teamScore, opponentScore;

    public Match() {
        this.matchTitle = "";
        this.date = "";
        this.startTime = "";
        this.meetTime = "";
        this.opponentName = "";
        this.location = "";
        this.homeOrAway = false;
        this.status = "";
        this.attendance = false;
        this.teamScore = 0;
        this.opponentScore = 0;
    }
    public Match(String matchTitle, String date, String startTime, String meetTime, String opponentName, String location, boolean homeOrAway, String status, boolean attendance, int teamScore, int opponentScore) {
        this.matchTitle = matchTitle;
        this.date = date;
        this.startTime = startTime;
        this.meetTime = meetTime;
        this.opponentName = opponentName;
        this.location = location;
        this.homeOrAway = homeOrAway;
        this.status = status;
        this.attendance = attendance;
        this.teamScore = teamScore;
        this.opponentScore = opponentScore;
    }

    public String getMatchTitle() {
        return matchTitle;
    }
    public void setMatchTitle(String matchTitle) {
        this.matchTitle = matchTitle;
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

    public String getOpponentName() {
        return opponentName;
    }
    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isHomeOrAway() {
        return homeOrAway;
    }
    public void setHomeOrAway(boolean homeOrAway) {
        this.homeOrAway = homeOrAway;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAttendance() {
        return attendance;
    }
    public void setAttendance(boolean attendance) {
        this.attendance = attendance;
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

}
