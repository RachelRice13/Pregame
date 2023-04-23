package com.example.pregame.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class StatsId {
    @Exclude
    public String StatsId;

    public <T extends StatsId> T withId(@NonNull final String id) {
        this.StatsId = id;
        return (T) this;
    }
}