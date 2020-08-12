package com.example.moovy.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "movies_table")
public class Movie implements Serializable {

    @PrimaryKey
    @NonNull
    private String id;

    private String name;

    private String genre;

    private String director;

    private String starring;

    private String summary;

    private int photoHash;

    private float averageRating;

    public Movie(@NonNull String id, String name, String genre, String director, String starring, String summary, int photoHash, float averageRating) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.director = director;
        this.starring = starring;
        this.summary = summary;
        this.photoHash = photoHash;
        this.averageRating = averageRating;
    }

    public Movie() {
        this.id = "";
        this.name = "";
        this.genre = "";
        this.director = "";
        this.starring = "";
        this.summary = "";
        this.photoHash = 0;
        this.averageRating = (float) 0.0;
    }

    public boolean isNewMovie() {
        return this.id.equals("");
    }

    @NonNull
    public String getId() {
        return this.id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
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

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }
}