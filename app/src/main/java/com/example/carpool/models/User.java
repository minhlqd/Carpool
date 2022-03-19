package com.example.carpool.models;

public class User {

    private String userId;
    private String email;
    private String fullName;
    private String username;
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
    private String role;
    private int completedRides;
    private int seats;
    private int userRating;
    private int points;
    private Boolean isCarOwner;
    private String startPoint;
    private String destination;

    public User() { }

    public User(String userId, String email, String fullName, String username, String profilePhoto, Long mobileNumber, String dateOfBird, String licenceNumber, int completedRides, int userRating,
                String car, String registrationPlate, int seats, String education, String work, String bio, Boolean isCarOwner, String gender, int points, String carPhoto, String startPoint, String destination, String role
    ) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.username = username;
        this.profilePhoto = profilePhoto;
        this.mobileNumber = mobileNumber;
        this.completedRides = completedRides;
        this.dateOfBird = dateOfBird;
        this.licenceNumber = licenceNumber;
        this.userRating = userRating;
        this.isCarOwner = isCarOwner;
        this.seats = seats;
        this.registrationPlate = registrationPlate;
        this.gender = gender;
        this.car = car;
        this.carPhoto = carPhoto;
        this.education = education;
        this.work = work;
        this.points = points;
        this.bio = bio;
        this.startPoint = startPoint;
        this.destination = destination;
        this.role = role;
    }

    public User(String userId, String email, String fullName, String username) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getUserId() {
        return userId;
    }


    public String getEmail() {
        return email;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getUsername() {
        return username;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getMobileNumber() {
        return mobileNumber;
    }

    public String getDateOfBird() {
        return dateOfBird;
    }

    public void setMobileNumber(Long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setDateOfBird(String dateOfBird) {
        this.dateOfBird = dateOfBird;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public void setLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }

    public int getCompletedRides() {
        return completedRides;
    }

    public void setCompletedRides(int completedRides) {
        this.completedRides = completedRides;
    }

    public String getGender() {
        return gender;
    }

    public String getRegistrationPlate() {
        return registrationPlate;
    }

    public String getCar() {
        return car;
    }

    public int getSeats() {
        return seats;
    }

    public Boolean getIsCarOwner() {
        return isCarOwner;
    }

    public String getCarPhoto() {
        return carPhoto;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setRegistrationPlate(String registrationPlate) {
        this.registrationPlate = registrationPlate;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public void setCarPhoto(String carPhoto) {
        this.carPhoto = carPhoto;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public void setIsCarOwner(Boolean isCarOwner) {
        this.isCarOwner = isCarOwner;
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

    /*@Override
    public String toString() {
        return "User{" +
                "user_id='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", full_name='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", profile_photo='" + profilePhoto + '\'' +
                ", date_of_birth='" + dateOfBird + '\'' +
                ", licence_number='" + licenceNumber + '\'' +
                ", gender='" + gender + '\'' +
                ", registration_plate='" + registrationPlate + '\'' +
                ", car='" + car + '\'' +
                ", car_photo='" + carPhoto + '\'' +
                ", education='" + education + '\'' +
                ", work='" + work + '\'' +
                ", bio='" + bio + '\'' +
                ", startPoint='" + startPoint + '\'' +
                ", destination='" + destination + '\'' +
                ", mobile_number=" + mobileNumber +
                ", isCompletedRides=" + completedRides +
                ", seats=" + seats +
                ", user_rating=" + userRating +
                ", points=" + points +
                ", carOwner=" + isCarOwner +
                '}';
    }*/

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
