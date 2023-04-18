package com.example.pregame.Model;

public class Media extends MediaId{
    String user, mediaPath, date, title;

    public Media() {}

    public Media(String user, String mediaPath, String date, String title) {
        this.user = user;
        this.mediaPath = mediaPath;
        this.date = date;
        this.title = title;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getMediaPath() {
        return mediaPath;
    }
    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
