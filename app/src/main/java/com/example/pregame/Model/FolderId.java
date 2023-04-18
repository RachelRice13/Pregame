package com.example.pregame.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class FolderId {
    @Exclude
    public String FolderId;

    public <T extends FolderId> T withId(@NonNull final String id) {
        this.FolderId = id;
        return (T) this;
    }
}