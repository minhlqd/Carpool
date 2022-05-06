package com.example.carpool.interfaces;

public interface ResponseBooked {
    void responseBooked(Boolean isAccept, String requestID,String rideID, int pos, String passengerId, int seat);
}
