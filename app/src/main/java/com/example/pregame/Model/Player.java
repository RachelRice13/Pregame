package com.example.pregame.Model;

import java.util.ArrayList;

public class Player {
    private String firstName, surname, dob, phoneNumber, email, password;
    private ArrayList<Team> teams;

    public Player(String firstName, String surname, String dob, String phoneNumber, String email, String password, ArrayList<Team> teams) {
        this.firstName = firstName;
        this.surname = surname;
        this.dob = dob;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.teams = teams;
    }
    public Player() {
        this.firstName = "";
        this.surname = "";
        this.dob = "";
        this.phoneNumber = "";
        this.email = "";
        this.password = "";
        this.teams = null;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDob() {
        return dob;
    }
    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }
    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

}
