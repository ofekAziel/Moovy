package com.example.moovy.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "users_table")
public class User implements Serializable {

    @NonNull
    @PrimaryKey
    private String userUid;

    private String firstName;

    private String lastName;

    private boolean isAdmin;

    public User(String firstName, String lastName) {
        this.userUid = "1";
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Ignore
    public User() {
        this.userUid = "1";
    }

    public String getFullName() {
        String fullName = firstName + " " + lastName;
        return fullName.trim();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @NonNull
    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(@NonNull String userUid) {
        this.userUid = userUid;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
