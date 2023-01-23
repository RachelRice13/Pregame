package com.example.pregame.Model;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class Team {
    private String teamName, teamCode;
    private ArrayList<DocumentReference> players;
    private ArrayList<DocumentReference> coaches;

    public Team(String teamName, String teamCode, ArrayList<DocumentReference> players, ArrayList<DocumentReference> coaches) {
        this.teamName = teamName;
        this.teamCode = teamCode;
        this.players = players;
        this.coaches = coaches;
    }
    public Team() {
        this.teamName = "";
        this.teamCode = "";
        this.players = null;
        this.coaches = null;
    }

    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamCode() {
        return teamCode;
    }
    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }

    public ArrayList<DocumentReference> getPlayers() {
        return players;
    }
    public void setPlayers(ArrayList<DocumentReference> players) {
        this.players = players;
    }

    public ArrayList<DocumentReference> getCoaches() {
        return coaches;
    }
    public void setCoaches(ArrayList<DocumentReference> coaches) {
        this.coaches = coaches;
    }

}
