package com.example.pregame.Model;

public class MatchStats extends MatchStatsId {
    private String myTeam, opponent, date;
    private int myTeamScore, opponentScore, offensiveRebounds, defensiveRebounds, assists, blocks, turnovers, steals, fouls;

    public MatchStats(String myTeam, String opponent, String date, int myTeamScore, int opponentScore, int offensiveRebounds, int defensiveRebounds, int assists, int blocks, int turnovers, int steals, int fouls) {
        this.myTeam = myTeam;
        this.opponent = opponent;
        this.date = date;
        this.myTeamScore = myTeamScore;
        this.opponentScore = opponentScore;
        this.offensiveRebounds = offensiveRebounds;
        this.defensiveRebounds = defensiveRebounds;
        this.assists = assists;
        this.blocks = blocks;
        this.turnovers = turnovers;
        this.steals = steals;
        this.fouls = fouls;
    }
    public MatchStats() {
        this.myTeam = "";
        this.opponent = "";
        this.date = "";
        this.myTeamScore = 0;
        this.opponentScore = 0;
        this.offensiveRebounds = 0;
        this.defensiveRebounds = 0;
        this.assists = 0;
        this.blocks = 0;
        this.turnovers = 0;
        this.steals = 0;
        this.fouls = 0;
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

    public int getOffensiveRebounds() {
        return offensiveRebounds;
    }
    public void setOffensiveRebounds(int offensiveRebounds) {
        this.offensiveRebounds = offensiveRebounds;
    }

    public int getDefensiveRebounds() {
        return defensiveRebounds;
    }
    public void setDefensiveRebounds(int defensiveRebounds) {
        this.defensiveRebounds = defensiveRebounds;
    }

    public int getAssists() {
        return assists;
    }
    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getBlocks() {
        return blocks;
    }
    public void setBlocks(int blocks) {
        this.blocks = blocks;
    }

    public int getTurnovers() {
        return turnovers;
    }
    public void setTurnovers(int turnovers) {
        this.turnovers = turnovers;
    }

    public int getSteals() {
        return steals;
    }
    public void setSteals(int steals) {
        this.steals = steals;
    }

    public int getFouls() {
        return fouls;
    }
    public void setFouls(int fouls) {
        this.fouls = fouls;
    }

}
