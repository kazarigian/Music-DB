package com.mycompany.databaseexample;

public class Song {

    private int id;
    private String title;
    private int albumID;
    private String album;

    Song(int id, String title, int albumID, String album) {

        this.id = id;
        this.title = title;
        this.albumID = albumID;
        this.album = album;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

     public int getAlbumID() {
        return albumID;
    }

    public void setAlbumID(int albumID) {
        this.albumID = albumID;
    }
    
    /**
     * @return the album
     */
    public String getAlbum() {
        return album;
    }

    /**
     * @param album the album to set
     */
    public void setAlbum(String album) {
        this.album = album;
    }
}
