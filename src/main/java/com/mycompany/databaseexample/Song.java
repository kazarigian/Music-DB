package com.mycompany.databaseexample;

public class Song {

    private int id;
    private String title;

    Song(int id, String title) {

        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
