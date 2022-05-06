package com.example.carpool.models;

public class BookingResults {
    private String requestID;
    private String passengerID;
    private String profile_photo;
    private String username;
    private int seats;
    private String destination;
    private String location;
    private int luggage;
    private boolean accepted;
    private String ride_id;
    private String pickupLocation;
    private String pickupTime;
    private String dateOfJourney;
    private String licencePlate;
    private long cost;

    public BookingResults() {
    }

    public BookingResults(String requestID, String passengerID, String profile_photo, String username, String licencePlate, int seats, String destination, String dateOfJourney, String pickupTime, String location, int luggage, boolean accepted, String ride_id, long cost, String pickupLocation) {
        this.requestID = requestID;
        this.passengerID = passengerID;
        this.profile_photo = profile_photo;
        this.username = username;
        this.seats = seats;
        this.destination = destination;
        this.location = location;
        this.luggage = luggage;
        this.accepted = accepted;
        this.cost = cost;
        this.licencePlate = licencePlate;
        this.ride_id = ride_id;
        this.pickupLocation = pickupLocation;
        this.pickupTime = pickupTime;
        this.dateOfJourney = dateOfJourney;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getPassengerID() {
        return passengerID;
    }

    public void setPassengerID(String passengerID) {
        this.passengerID = passengerID;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getLuggage() {
        return luggage;
    }

    public void setLuggage(int luggage) {
        this.luggage = luggage;
    }

    public boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getDateOfJourney() {
        return dateOfJourney;
    }

    public void setDateOfJourney(String dateOfJourney) {
        this.dateOfJourney = dateOfJourney;
    }

    @Override
    public String toString() {
        return "BookingResults{" +
                "requestID=" + requestID + '\'' +
                "passengerID='" + passengerID + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", username='" + username + '\'' +
                ", seats=" + seats +
                ", destination='" + destination + '\'' +
                ", location='" + location + '\'' +
                ", luggage=" + luggage +
                ", accepted=" + accepted +
                ", ride_id='" + ride_id + '\'' +
                ", pickupLocation='" + pickupLocation + '\'' +
                ", pickupTime='" + pickupTime + '\'' +
                ", dateOfJourney='" + dateOfJourney + '\'' +
                ", licencePlate='" + licencePlate + '\'' +
                ", cost=" + cost +
                '}';
    }
}
