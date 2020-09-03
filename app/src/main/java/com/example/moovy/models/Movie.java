package com.example.moovy.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.UUID;

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

    private String documentId;

    public Movie(@NonNull String id, String name, String genre, String director, String starring, String summary, int photoHash, String documentId) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.director = director;
        this.starring = starring;
        this.summary = summary;
        this.photoHash = photoHash;
        this.documentId = documentId;
    }

    @Ignore
    public Movie() {
        this.id = UUID.randomUUID().toString();
        this.name = "";
        this.genre = "";
        this.director = "";
        this.starring = "";
        this.summary = "";
        this.photoHash = 0;
        this.documentId = "";
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

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}