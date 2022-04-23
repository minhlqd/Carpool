package com.example.carpool.models;


public class Ride {

    private String rideID;
    private String username;
    private String profile_picture;
    private String currentLocation;
    private String destination;
    private String lengthOfJourney;
    private String car;
    private String dateOfJourney;
    private String pickupTime;
    private Boolean sameGender;
    private int completeRides;
    private int seatsAvailable;
    private int extraTime;
    private long cost;
    private float userRating;
    private String duration;
    private String driverID;
    private String passengerID;
    private String pickupLocation;
    private String licencePlate;

    public Ride() {
    }

    public String getDriverID() {
        return driverID;
    }

    public String getPassengerID() {
        return passengerID;
    }

    public Ride(String rideID, String username, String profile_picture, String currentLocation, String destination, String lengthOfJourney, String car, String dateOfJourney, String pickupTime, Boolean sameGender, int completeRides, int seatsAvailable, int extraTime, int cost, float userRating, String duration, String driverID, String passengerID, String pickupLocation, String licencePlate) {
        this.rideID = rideID;
        this.username = username;
        this.profile_picture = profile_picture;
        this.currentLocation = currentLocation;
        this.destination = destination;
        this.lengthOfJourney = lengthOfJourney;
        this.car = car;
        this.dateOfJourney = dateOfJourney;
        this.pickupTime = pickupTime;
        this.sameGender = sameGender;
        this.completeRides = completeRides;
        this.seatsAvailable = seatsAvailable;
        this.extraTime = extraTime;
        this.cost = cost;
        this.userRating = userRating;
        this.duration = duration;
        this.driverID = driverID;
        this.passengerID = passengerID;
        this.pickupLocation = pickupLocation;
        this.licencePlate = licencePlate;
    }

    public void setPassengerID(String passengerID) {
        this.passengerID = passengerID;
    }

    public String getRideID() {
        return rideID;
    }

    public void setRideID(String rideID) {
        this.rideID = rideID;
    }

    public Boolean getSameGender() {
        return sameGender;
    }

    public void setSameGender(Boolean sameGender) {
        this.sameGender = sameGender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getUserRating() {
        return userRating;
    }

    public void setUserRating(float userRating) {
        this.userRating = userRating;
    }

    public String getDateOfJourney() {
        return dateOfJourney;
    }

    public void setDateOfJourney(String dateOfJourney) {
        this.dateOfJourney = dateOfJourney;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public int getCompleteRides() {
        return completeRides;
    }

    public void setCompleteRides(int completeRides) {
        this.completeRides = completeRides;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getLengthOfJourney() {
        return lengthOfJourney;
    }

    public void setLengthOfJourney(String lengthOfJourney) {
        this.lengthOfJourney = lengthOfJourney;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public int getExtraTime() {
        return extraTime;
    }

    public void setExtraTime(int extraTime) {
        this.extraTime = extraTime;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUserId() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    @Override
    public String toString() {
        return "Ride{" +
                "rideID='" + rideID + '\'' +
                ", username='" + username + '\'' +
                ", profile_picture='" + profile_picture + '\'' +
                ", currentLocation='" + currentLocation + '\'' +
                ", destination='" + destination + '\'' +
                ", lengthOfJourney='" + lengthOfJourney + '\'' +
                ", car='" + car + '\'' +
                ", dateOfJourney='" + dateOfJourney + '\'' +
                ", pickupTime='" + pickupTime + '\'' +
                ", sameGender=" + sameGender +
                ", completeRides=" + completeRides +
                ", seatsAvailable=" + seatsAvailable +
                ", extraTime=" + extraTime +
                ", cost=" + cost +
                ", userRating=" + userRating +
                ", duration='" + duration + '\'' +
                ", driverID='" + driverID + '\'' +
                ", passengerID='" + passengerID + '\'' +
                ", pickupLocation='" + pickupLocation + '\'' +
                ", licencePlate='" + licencePlate + '\'' +
                '}';
    }
}
