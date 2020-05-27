package com.example.moovy.services;

import android.util.Patterns;

public class ValidationService {

    public boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    public boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
