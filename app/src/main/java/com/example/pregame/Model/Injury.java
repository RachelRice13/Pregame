package com.example.pregame.Model;

import java.util.Date;

public class Injury extends InjuryId{
    private String bodyPart, descriptionOfInjury, lengthOfInjury, otherDetails;
    private Date dateOfInjury;
    private int levelOfPain;
    private boolean seenPhysio;

    public Injury(String bodyPart, String descriptionOfInjury, String lengthOfInjury, String otherDetails, Date dateOfInjury, int levelOfPain, boolean seenPhysio) {
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
        this.dateOfInjury = new Date();
        this.levelOfPain = 0;
        this.seenPhysio = false;
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

    public Date getDateOfInjury() {
        return dateOfInjury;
    }
    public void setDateOfInjury(Date dateOfInjury) {
        this.dateOfInjury = dateOfInjury;
    }

    public int getLevelOfPain() {
        return levelOfPain;
    }
    public void setLevelOfPain(int levelOfPain) {
        this.levelOfPain = levelOfPain;
    }

    public boolean isSeenPhysio() {
        return seenPhysio;
    }
    public void setSeenPhysio(boolean seenPhysio) {
        this.seenPhysio = seenPhysio;
    }
}
