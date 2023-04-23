package com.example.pregame.Model;

public class Stats extends StatsId {
    int shotsTakenTwo, shotsMissedTwo, shotsMadeTwo, shotsTakenThree, shotsMissedThree, shotsMadeThree, shotsTakenFt, shotsMissedFt, shotsMadeFt, offensiveRebounds, defensiveRebounds, assists, blocks, turnovers, steals, fouls;

    public Stats() {}

    public Stats(int shotsTakenTwo, int shotsMissedTwo, int shotsMadeTwo, int shotsTakenThree, int shotsMissedThree, int shotsMadeThree, int shotsTakenFt, int shotsMissedFt, int shotsMadeFt, int offensiveRebounds, int defensiveRebounds, int assists, int blocks, int turnovers, int steals, int fouls) {
        this.shotsTakenTwo = shotsTakenTwo;
        this.shotsMissedTwo = shotsMissedTwo;
        this.shotsMadeTwo = shotsMadeTwo;
        this.shotsTakenThree = shotsTakenThree;
        this.shotsMissedThree = shotsMissedThree;
        this.shotsMadeThree = shotsMadeThree;
        this.shotsTakenFt = shotsTakenFt;
        this.shotsMissedFt = shotsMissedFt;
        this.shotsMadeFt = shotsMadeFt;
        this.offensiveRebounds = offensiveRebounds;
        this.defensiveRebounds = defensiveRebounds;
        this.assists = assists;
        this.blocks = blocks;
        this.turnovers = turnovers;
        this.steals = steals;
        this.fouls = fouls;
    }

    public int getShotsTakenTwo() {
        return shotsTakenTwo;
    }
    public void setShotsTakenTwo(int shotsTakenTwo) {
        this.shotsTakenTwo = shotsTakenTwo;
    }

    public int getShotsMissedTwo() {
        return shotsMissedTwo;
    }
    public void setShotsMissedTwo(int shotsMissedTwo) {
        this.shotsMissedTwo = shotsMissedTwo;
    }

    public int getShotsMadeTwo() {
        return shotsMadeTwo;
    }
    public void setShotsMadeTwo(int shotsMadeTwo) {
        this.shotsMadeTwo = shotsMadeTwo;
    }

    public int getShotsTakenThree() {
        return shotsTakenThree;
    }
    public void setShotsTakenThree(int shotsTakenThree) {
        this.shotsTakenThree = shotsTakenThree;
    }

    public int getShotsMissedThree() {
        return shotsMissedThree;
    }
    public void setShotsMissedThree(int shotsMissedThree) {
        this.shotsMissedThree = shotsMissedThree;
    }

    public int getShotsMadeThree() {
        return shotsMadeThree;
    }
    public void setShotsMadeThree(int shotsMadeThree) {
        this.shotsMadeThree = shotsMadeThree;
    }

    public int getShotsTakenFt() {
        return shotsTakenFt;
    }
    public void setShotsTakenFt(int shotsTakenFt) {
        this.shotsTakenFt = shotsTakenFt;
    }

    public int getShotsMissedFt() {
        return shotsMissedFt;
    }
    public void setShotsMissedFt(int shotsMissedFt) {
        this.shotsMissedFt = shotsMissedFt;
    }

    public int getShotsMadeFt() {
        return shotsMadeFt;
    }
    public void setShotsMadeFt(int shotsMadeFt) {
        this.shotsMadeFt = shotsMadeFt;
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
