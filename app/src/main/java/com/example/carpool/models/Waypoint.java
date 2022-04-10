package com.example.carpool.models;

import java.util.UUID;

public class Waypoint {
    private UUID id;

    private UUID on_trip;

    private Double latitude;

    private Double longitude;

    private String time;

    public Waypoint() {
    }

    public Waypoint(UUID id, UUID on_trip, Double latitude, Double longitude, String time) {
        this.id = id;
        this.on_trip = on_trip;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOn_trip() {
        return on_trip;
    }

    public void setOn_trip(UUID on_trip) {
        this.on_trip = on_trip;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
