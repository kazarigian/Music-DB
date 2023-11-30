/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.databaseexample;

/**
 *
 * @author erick
 */
public class Artist {
 
    public int id;
    public String artist_name;
    public String genre;
    
    public Artist(int id, String artist_name, String genre) {
        this.id = id;
        this.artist_name = artist_name;
        this.genre = genre; 
    }
    
    public int getID() {
        return id;
    }
    
    public void setID(int id) {
        this.id = id;
    }
    
    public String getName() {
        return artist_name;
    }
    
    public void setName(String artist_name) {
        this.artist_name = artist_name; 
    }
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    
}
