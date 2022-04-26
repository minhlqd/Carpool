package com.example.carpool.models;

public class Participants {
    private String username;
    private String userProfilePhoto;
    private String id;
    private Boolean accepted;

    public Participants() {
    }

    public Participants(String username, String userProfilePhoto, Boolean accepted, String id) {
        this.username = username;
        this.userProfilePhoto = userProfilePhoto;
        this.accepted = accepted;
        this.id = id;
    }

    public Participants(String username, Boolean accepted) {
        this.username = username;
        this.accepted = accepted;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserProfilePhoto() {
        return userProfilePhoto;
    }

    public void setUserProfilePhoto(String userProfilePhoto) {
        this.userProfilePhoto = userProfilePhoto;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    @Override
    public String toString() {
        return "Participants{" +
                "username='" + username + '\'' +
                ", accepted=" + accepted +
                '}';
    }
}