package com.mycompany.databaseexample;

public class Album {
    private int id;
    private String title;
    private String year;
    private int artistID;
    private String artist;
    
    Album(int id, String title, String year, int artistID, String artist) {

        this.id = id;
        this.title = title;
        this.year = year;
        this.artistID = artistID;
        this.artist = artist;
    }
    /**
     * 
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

    /**
     * @return the year
     */
    public String getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @return the artistID
     */
    public int getArtistID() {
        return artistID;
    }

    /**
     * @param artistID the artistID to set
     */
    public void setArtistID(int artistID) {
        this.artistID = artistID;
    }

    /**
     * @return the artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * @param artist the artist to set
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }
   

}
