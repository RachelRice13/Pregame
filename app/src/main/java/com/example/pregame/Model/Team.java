package com.example.pregame.Model;

import java.util.ArrayList;

public class Team {
    private String teamName, teamCode;
    private ArrayList<Player> players;
    private ArrayList<Coach> coaches;

    public Team(String teamName, String teamCode, ArrayList<Player> players, ArrayList<Coach> coaches) {
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

    public ArrayList<Player> getPlayers() {
        return players;
    }
    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public ArrayList<Coach> getCoaches() {
        return coaches;
    }
    public void setCoaches(ArrayList<Coach> coaches) {
        this.coaches = coaches;
    }

}
