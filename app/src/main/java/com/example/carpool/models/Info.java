package com.example.carpool.models;

import androidx.annotation.NonNull;

public class Info {
    private String profilePhoto;
    private String dateOfBird;
    private String licenceNumber;
    private String gender;
    private String registrationPlate;
    private String car;
    private String carPhoto;
    private String education;
    private String work;
    private String bio;
    private Long mobileNumber;
    private int completedRides;
    private int seats;
    private int userRating;
    private int points;
    private Boolean isCarOwner;
    private String startPoint;
    private String destination;
    private String role;

    public Info(String profilePhoto, String dateOfBird, String licenceNumber, String gender,
                String registrationPlate, String car, String carPhoto, String education,
                String work, String bio, Long mobileNumber, int completedRides, int seats, int userRating,
                int points, Boolean isCarOwner, String startPoint, String destination, String role) {
        this.profilePhoto = profilePhoto;
        this.dateOfBird = dateOfBird;
        this.licenceNumber = licenceNumber;
        this.gender = gender;
        this.registrationPlate = registrationPlate;
        this.car = car;
        this.carPhoto = carPhoto;
        this.education = education;
        this.work = work;
        this.bio = bio;
        this.mobileNumber = mobileNumber;
        this.completedRides = completedRides;
        this.seats = seats;
        this.userRating = userRating;
        this.points = points;
        this.isCarOwner = isCarOwner;
        this.startPoint = startPoint;
        this.destination = destination;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getDateOfBird() {
        return dateOfBird;
    }

    public void setDateOfBird(String dateOfBird) {
        this.dateOfBird = dateOfBird;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public void setLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRegistrationPlate() {
        return registrationPlate;
    }

    public void setRegistrationPlate(String registrationPlate) {
        this.registrationPlate = registrationPlate;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getCarPhoto() {
        return carPhoto;
    }

    public void setCarPhoto(String carPhoto) {
        this.carPhoto = carPhoto;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(Long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public int getCompletedRides() {
        return completedRides;
    }

    public void setCompletedRides(int completedRides) {
        this.completedRides = completedRides;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Boolean getCarOwner() {
        return isCarOwner;
    }

    public void setCarOwner(Boolean carOwner) {
        isCarOwner = carOwner;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @NonNull
    @Override
    public String toString() {
        return "Info{" +
                "profilePhoto='" + profilePhoto + '\'' +
                ", date_of_bird='" + dateOfBird + '\'' +
                ", licenceNumber='" + licenceNumber + '\'' +
                ", gender='" + gender + '\'' +
                ", registration_plate='" + registrationPlate + '\'' +
                ", car='" + car + '\'' +
                ", carPhoto='" + carPhoto + '\'' +
                ", education='" + education + '\'' +
                ", work='" + work + '\'' +
                ", bio='" + bio + '\'' +
                ", mobile_number=" + mobileNumber +
                ", isCompletedRides=" + completedRides +
                ", seats=" + seats +
                ", userRating=" + userRating +
                ", points=" + points +
                ", isCarOwner=" + isCarOwner +
                ", start_point='" + startPoint + '\'' +
                ", destination='" + destination + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
