package com.example.carpool.interfaces;

public interface ResponseBooked {
    void responseBooked(Boolean isAccept, String rideId, int pos, String passengerId, int seat);
}
