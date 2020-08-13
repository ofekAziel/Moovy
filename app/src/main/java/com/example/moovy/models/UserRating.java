package com.example.moovy.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "user_ratings_table")
public class UserRating implements Serializable {

    @PrimaryKey
    private String id;

    private User user;

    private float rating;

    public UserRating(String id, User user, float rating) {
        this.id = id;
        this.user = user;
        this.rating = rating;
    }

    public UserRating() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
