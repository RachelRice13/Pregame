package com.example.pregame.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class MatchStats extends Stats implements Serializable {
    private String myTeam, opponent, date, id, title;
    private int myTeamScore, opponentScore, opponentShotsTakenTwo, opponentShotsMissedTwo, opponentShotsMadeTwo, opponentShotsTakenThree, opponentShotsMissedThree, opponentShotsMadeThree, opponentShotsTakenFt, opponentShotsMissedFt, opponentShotsMadeFt;
    private ArrayList<IndividualStats> players;

    public MatchStats() {}

    public MatchStats(String myTeam, String opponent, String date, String id, String title, int myTeamScore, int opponentScore, int opponentShotsTakenTwo, int opponentShotsMissedTwo, int opponentShotsMadeTwo, int opponentShotsTakenThree, int opponentShotsMissedThree, int opponentShotsMadeThree, int opponentShotsTakenFt, int opponentShotsMissedFt, int opponentShotsMadeFt, ArrayList<IndividualStats> players, int shotsTakenTwo, int shotsMissedTwo, int shotsMadeTwo, int shotsTakenThree, int shotsMissedThree, int shotsMadeThree, int shotsTakenFt, int shotsMissedFt, int shotsMadeFt, int offensiveRebounds, int defensiveRebounds, int assists, int blocks, int turnovers, int steals, int fouls) {
        super(shotsTakenTwo, shotsMissedTwo, shotsMadeTwo, shotsTakenThree, shotsMissedThree, shotsMadeThree, shotsTakenFt, shotsMissedFt, shotsMadeFt, offensiveRebounds, defensiveRebounds, assists, blocks, turnovers, steals, fouls);
        this.myTeam = myTeam;
        this.opponent = opponent;
        this.date = date;
        this.id = id;
        this.title = title;
        this.myTeamScore = myTeamScore;
        this.opponentScore = opponentScore;
        this.opponentShotsTakenTwo = opponentShotsTakenTwo;
        this.opponentShotsMissedTwo = opponentShotsMissedTwo;
        this.opponentShotsMadeTwo = opponentShotsMadeTwo;
        this.opponentShotsTakenThree = opponentShotsTakenThree;
        this.opponentShotsMissedThree = opponentShotsMissedThree;
        this.opponentShotsMadeThree = opponentShotsMadeThree;
        this.opponentShotsTakenFt = opponentShotsTakenFt;
        this.opponentShotsMissedFt = opponentShotsMissedFt;
        this.opponentShotsMadeFt = opponentShotsMadeFt;
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

    public int getOpponentShotsTakenTwo() {
        return opponentShotsTakenTwo;
    }
    public void setOpponentShotsTakenTwo(int opponentShotsTakenTwo) {
        this.opponentShotsTakenTwo = opponentShotsTakenTwo;
    }

    public int getOpponentShotsMissedTwo() {
        return opponentShotsMissedTwo;
    }
    public void setOpponentShotsMissedTwo(int opponentShotsMissedTwo) {
        this.opponentShotsMissedTwo = opponentShotsMissedTwo;
    }

    public int getOpponentShotsMadeTwo() {
        return opponentShotsMadeTwo;
    }
    public void setOpponentShotsMadeTwo(int opponentShotsMadeTwo) {
        this.opponentShotsMadeTwo = opponentShotsMadeTwo;
    }

    public int getOpponentShotsTakenThree() {
        return opponentShotsTakenThree;
    }
    public void setOpponentShotsTakenThree(int opponentShotsTakenThree) {
        this.opponentShotsTakenThree = opponentShotsTakenThree;
    }

    public int getOpponentShotsMissedThree() {
        return opponentShotsMissedThree;
    }
    public void setOpponentShotsMissedThree(int opponentShotsMissedThree) {
        this.opponentShotsMissedThree = opponentShotsMissedThree;
    }

    public int getOpponentShotsMadeThree() {
        return opponentShotsMadeThree;
    }
    public void setOpponentShotsMadeThree(int opponentShotsMadeThree) {
        this.opponentShotsMadeThree = opponentShotsMadeThree;
    }

    public int getOpponentShotsTakenFt() {
        return opponentShotsTakenFt;
    }
    public void setOpponentShotsTakenFt(int opponentShotsTakenFt) {
        this.opponentShotsTakenFt = opponentShotsTakenFt;
    }

    public int getOpponentShotsMissedFt() {
        return opponentShotsMissedFt;
    }
    public void setOpponentShotsMissedFt(int opponentShotsMissedFt) {
        this.opponentShotsMissedFt = opponentShotsMissedFt;
    }

    public int getOpponentShotsMadeFt() {
        return opponentShotsMadeFt;
    }
    public void setOpponentShotsMadeFt(int opponentShotsMadeFt) {
        this.opponentShotsMadeFt = opponentShotsMadeFt;
    }

    public ArrayList<IndividualStats> getPlayers() {
        return players;
    }
    public void setPlayers(ArrayList<IndividualStats> players) {
        this.players = players;
    }
}
