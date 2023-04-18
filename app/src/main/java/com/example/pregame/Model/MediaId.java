package com.example.pregame.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class MediaId {
    @Exclude
    public String MediaId;

    public <T extends MediaId> T withId(@NonNull final String id) {
        this.MediaId = id;
        return (T) this;
    }
}
