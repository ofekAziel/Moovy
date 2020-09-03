package com.example.moovy.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.UUID;

@Entity(tableName = "comments_table")
@TypeConverters(DateConverter.class)
public class Comment {

    @PrimaryKey
    @NonNull
    private String id;

    private String content;

    private String userUid;

    private String userFullName;

    private Date date;

    private String movieId;

    private String documentId;

    public Comment(@NonNull String id, String content, String userUid, String userFullName, Date date, String movieId, String documentId) {
        this.id = id;
        this.content = content;
        this.userUid = userUid;
        this.userFullName = userFullName;
        this.date = date;
        this.movieId = movieId;
        this.documentId = documentId;
    }

    @Ignore
    public Comment(String content, String userUid, String userFullName, Date date, String movieId) {
        this.id = UUID.randomUUID().toString();
        this.content = content;
        this.userUid = userUid;
        this.userFullName = userFullName;
        this.date = date;
        this.movieId = movieId;
        this.documentId = "";
    }

    @Ignore
    public Comment() {

    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
