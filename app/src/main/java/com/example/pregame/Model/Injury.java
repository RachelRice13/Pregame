package com.example.pregame.Model;

public class Injury extends InjuryId{
    private String bodyPart, descriptionOfInjury, lengthOfInjury, otherDetails, dateOfInjury, seenPhysio;
    private float levelOfPain;

    public Injury(String bodyPart, String descriptionOfInjury, String lengthOfInjury, String otherDetails, String dateOfInjury, float levelOfPain, String seenPhysio) {
        this.bodyPart = bodyPart;
        this.descriptionOfInjury = descriptionOfInjury;
        this.lengthOfInjury = lengthOfInjury;
        this.otherDetails = otherDetails;
        this.dateOfInjury = dateOfInjury;
        this.levelOfPain = levelOfPain;
        this.seenPhysio = seenPhysio;
    }
    public Injury() {
        this.bodyPart = "";
        this.descriptionOfInjury = "";
        this.lengthOfInjury = "";
        this.otherDetails = "";
        this.dateOfInjury = "";
        this.levelOfPain = 0;
        this.seenPhysio = "";
    }

    public String getBodyPart() {
        return bodyPart;
    }
    public void setBodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
    }

    public String getDescriptionOfInjury() {
        return descriptionOfInjury;
    }
    public void setDescriptionOfInjury(String descriptionOfInjury) {
        this.descriptionOfInjury = descriptionOfInjury;
    }

    public String getLengthOfInjury() {
        return lengthOfInjury;
    }
    public void setLengthOfInjury(String lengthOfInjury) {
        this.lengthOfInjury = lengthOfInjury;
    }

    public String getOtherDetails() {
        return otherDetails;
    }
    public void setOtherDetails(String otherDetails) {
        this.otherDetails = otherDetails;
    }

    public String getDateOfInjury() {
        return dateOfInjury;
    }
    public void setDateOfInjury(String dateOfInjury) {
        this.dateOfInjury = dateOfInjury;
    }

    public float getLevelOfPain() {
        return levelOfPain;
    }
    public void setLevelOfPain(float levelOfPain) {
        this.levelOfPain = levelOfPain;
    }

    public String isSeenPhysio() {
        return seenPhysio;
    }
    public void setSeenPhysio(String seenPhysio) {
        this.seenPhysio = seenPhysio;
    }
}
