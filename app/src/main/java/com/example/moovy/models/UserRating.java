package com.example.moovy.models;

import java.io.Serializable;

public class UserRating implements Serializable {

    private String id;
    private User user;
    private double rating;

    public UserRating(String id, User user, double rating) {
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
