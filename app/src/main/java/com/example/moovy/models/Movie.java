package com.example.moovy.models;

import java.io.Serializable;

public class Movie implements Serializable {

    private String name, genre, director, starring, summary;
    private int photoHash;

    public Movie(String name, String genre, String director, String starring, String summary, int photoHash) {
        this.name = name;
        this.genre = genre;
        this.director = director;
        this.starring = starring;
        this.summary = summary;
        this.photoHash = photoHash;
    }

    public Movie() {
    }

    public int getPhotoHash() {
        return this.photoHash;
    }

    public void setPhotoHash(int photoHash) {
        this.photoHash = photoHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getStarring() {
        return starring;
    }

    public void setStarring(String starring) {
        this.starring = starring;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}