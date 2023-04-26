package com.example.pregame.Model;

import com.google.firebase.firestore.DocumentReference;

import java.util.Comparator;

public class StatLeaders {
    DocumentReference reference;
    int number;

    public StatLeaders() {
    }

    public StatLeaders(DocumentReference reference, int number) {
        this.reference = reference;
        this.number = number;
    }

    public DocumentReference getReference() {
        return reference;
    }
    public void setReference(DocumentReference reference) {
        this.reference = reference;
    }

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
}
