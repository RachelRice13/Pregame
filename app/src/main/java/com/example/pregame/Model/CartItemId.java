package com.example.pregame.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class CartItemId {
    @Exclude
    public String CartItemId;

    public <T extends CartItemId> T withId(@NonNull final String id) {
        this.CartItemId = id;
        return (T) this;
    }
}
