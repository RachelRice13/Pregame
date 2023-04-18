package com.example.pregame.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class MatchTrainingId {
    @Exclude
    public String MatchTrainingId;

    public <T extends MatchTrainingId> T withId(@NonNull final String id) {
        this.MatchTrainingId = id;
        return (T) this;
    }
}
