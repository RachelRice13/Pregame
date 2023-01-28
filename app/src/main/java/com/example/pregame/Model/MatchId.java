package com.example.pregame.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class MatchId {
    @Exclude
    private String MatchId;

    public <T extends MatchId> T withId(@NonNull final String id) {
        this.MatchId = id;
        return (T) this;
    }
}
