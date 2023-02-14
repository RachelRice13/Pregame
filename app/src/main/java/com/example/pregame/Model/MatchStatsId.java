package com.example.pregame.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class MatchStatsId {
    @Exclude
    public String TrainingId;

    public <T extends MatchStatsId> T withId(@NonNull final String id) {
        this.TrainingId = id;
        return (T) this;
    }
}
