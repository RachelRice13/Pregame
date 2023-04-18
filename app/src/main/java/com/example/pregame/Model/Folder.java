package com.example.pregame.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Folder extends FolderId implements Serializable {
    String user, date, title;
    ArrayList<String> photos, videos;

    public Folder() {}

    public Folder(String user, String date, String title, ArrayList<String> photos, ArrayList<String> videos) {
        this.user = user;
        this.date = date;
        this.title = title;
        this.photos = photos;
        this.videos = videos;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
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

    public ArrayList<String> getPhotos() {
        return photos;
    }
    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

    public ArrayList<String> getVideos() {
        return videos;
    }
    public void setVideos(ArrayList<String> videos) {
        this.videos = videos;
    }
}
