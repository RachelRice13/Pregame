package com.example.pregame.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class MatchStats extends Stats implements Serializable {
    private String myTeam, opponent, date, id, title;
    private int myTeamScore, opponentScore;
    private ArrayList<IndividualStats> players;

    public MatchStats() {}

    public MatchStats(String myTeam, String opponent, String date, String id, String title, int myTeamScore, int opponentScore, ArrayList<IndividualStats> players, int shotsTakenTwo, int shotsMissedTwo, int shotsMadeTwo, int shotsTakenThree, int shotsMissedThree, int shotsMadeThree, int shotsTakenFt, int shotsMissedFt, int shotsMadeFt, int offensiveRebounds, int defensiveRebounds, int assists, int blocks, int turnovers, int steals, int fouls) {
        super(shotsTakenTwo, shotsMissedTwo, shotsMadeTwo, shotsTakenThree, shotsMissedThree, shotsMadeThree, shotsTakenFt, shotsMissedFt, shotsMadeFt, offensiveRebounds, defensiveRebounds, assists, blocks, turnovers, steals, fouls);
        this.myTeam = myTeam;
        this.opponent = opponent;
        this.date = date;
        this.id = id;
        this.title = title;
        this.myTeamScore = myTeamScore;
        this.opponentScore = opponentScore;
        this.players = players;
    }

    public String getMyTeam() {
        return myTeam;
    }
    public void setMyTeam(String myTeam) {
        this.myTeam = myTeam;
    }

    public String getOpponent() {
        return opponent;
    }
    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public int getMyTeamScore() {
        return myTeamScore;
    }
    public void setMyTeamScore(int myTeamScore) {
        this.myTeamScore = myTeamScore;
    }

    public int getOpponentScore() {
        return opponentScore;
    }
    public void setOpponentScore(int opponentScore) {
        this.opponentScore = opponentScore;
    }

    public ArrayList<IndividualStats> getPlayers() {
        return players;
    }
    public void setPlayers(ArrayList<IndividualStats> players) {
        this.players = players;
    }
}
