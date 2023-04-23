package com.example.pregame.Model;

import com.google.firebase.firestore.DocumentReference;

public class IndividualStats extends Stats {
    DocumentReference player;
    int pointsScored;

    public IndividualStats() {}

    public IndividualStats(DocumentReference player, int pointsScored, int shotsTakenTwo, int shotsMissedTwo, int shotsMadeTwo, int shotsTakenThree, int shotsMissedThree, int shotsMadeThree, int shotsTakenFt, int shotsMissedFt, int shotsMadeFt, int offensiveRebounds, int defensiveRebounds, int assists, int blocks, int turnovers, int steals, int fouls) {
        super(shotsTakenTwo, shotsMissedTwo, shotsMadeTwo, shotsTakenThree, shotsMissedThree, shotsMadeThree, shotsTakenFt, shotsMissedFt, shotsMadeFt, offensiveRebounds, defensiveRebounds, assists, blocks, turnovers, steals, fouls);
        this.player = player;
        this.pointsScored = pointsScored;
    }

    public DocumentReference getPlayer() {
        return player;
    }
    public void setPlayer(DocumentReference player) {
        this.player = player;
    }

    public int getPointsScored() {
        return pointsScored;
    }
    public void setPointsScored(int pointsScored) {
        this.pointsScored = pointsScored;
    }

}
