package com.example.moovy.models;

import java.util.Date;

public class Comment {
    private String content;
    private User user;
    private Date date;
    private String id;

    public Comment() {
    }

    public Comment(String content, User user, Date date) {
        this.date = date;
        this.content = content;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
