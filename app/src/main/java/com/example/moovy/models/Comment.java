package com.example.moovy.models;

import java.util.Date;

public class Comment {
    String content;
    User user;
    Date date;

    public Comment() {
    }

    public Comment(String content, User user, Date date) {
        this.date = date;
        this.content = content;
        this.user = user;
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
