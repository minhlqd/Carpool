package com.example.carpool.models;

import java.util.UUID;

public class Trip {
    private UUID trip_id;

    private String account_owner;
    private String  date;

    private int weekday;

    public Trip() {
    }

    public Trip(UUID trip_id, String account_owner, String date, int weekday) {
        this.trip_id = trip_id;
        this.account_owner = account_owner;
        this.date = date;
        this.weekday = weekday;
    }

    public UUID getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(UUID trip_id) {
        this.trip_id = trip_id;
    }

    public String getAccount_owner() {
        return account_owner;
    }

    public void setAccount_owner(String account_owner) {
        this.account_owner = account_owner;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }
}
