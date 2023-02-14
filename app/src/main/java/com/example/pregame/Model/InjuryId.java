package com.example.pregame.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class InjuryId {
    @Exclude
    public String TrainingId;

    public <T extends InjuryId> T withId(@NonNull final String id) {
        this.TrainingId = id;
        return (T) this;
    }
}
