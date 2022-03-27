package com.example.carpool.models;

import androidx.annotation.NonNull;

public class User {

    private String userId;
    private String email;
    private String fullName;
    private String username;


    public User() { }

    public User(String userId, String email, String fullName, String username) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.username = username;
    }



    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "user_id='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
