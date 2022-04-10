package com.example.carpool.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FrequentRouteResults implements Serializable {

    private int id;

    @SerializedName("frequent_route_id")
    private String frequent_route_id;
    @SerializedName("account_id")
    private String user_id;
    @SerializedName("address_start")
    private String address_start;
    @SerializedName("lat_start")
    private double lat_start;
    @SerializedName("lng_start")
    private double lng_start;
    @SerializedName("address_end")
    private String address_destination;
    @SerializedName("lat_end")
    private double lat_end;
    @SerializedName("lng_end")
    private double lng_end;
    @SerializedName("time_start")
    private String time_start;
    @SerializedName("time_end")
    private String time_destination;
    @SerializedName("length_route")
    private double length_route;
    @SerializedName("is_shared")
    private int is_shared;
    @SerializedName("type_shared")
    private String type_shared;


    public FrequentRouteResults() {
    }

    public FrequentRouteResults(int id, String frequent_route_id, String user_id, String address_start, double lat_start, double lng_start, String address_destination, double lat_end, double lng_end, String time_start, String time_destination, double length_route, int is_shared, String type_shared) {
        this.id = id;
        this.frequent_route_id = frequent_route_id;
        this.user_id = user_id;
        this.address_start = address_start;
        this.lat_start = lat_start;
        this.lng_start = lng_start;
        this.address_destination = address_destination;
        this.lat_end = lat_end;
        this.lng_end = lng_end;
        this.time_start = time_start;
        this.time_destination = time_destination;
        this.length_route = length_route;
        this.is_shared = is_shared;
        this.type_shared = type_shared;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLength_route() {
        return length_route;
    }

    public void setLength_route(double length_route) {
        this.length_route = length_route;
    }

    public String getFrequent_route_id() {
        return frequent_route_id;
    }

    public void setFrequent_route_id(String frequent_route_id) {
        this.frequent_route_id = frequent_route_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public String getAddress_start() {
        return address_start;
    }

    public void setAddress_start(String address_start) {
        this.address_start = address_start;
    }

    public double getLat_start() {
        return lat_start;
    }

    public void setLat_start(double lat_start) {
        this.lat_start = lat_start;
    }

    public double getLng_start() {
        return lng_start;
    }

    public void setLng_start(double lng_start) {
        this.lng_start = lng_start;
    }

    public String getAddress_destination() {
        return address_destination;
    }

    public void setAddress_destination(String address_destination) {
        this.address_destination = address_destination;
    }

    public double getLat_end() {
        return lat_end;
    }

    public void setLat_end(double lat_end) {
        this.lat_end = lat_end;
    }

    public double getLng_end() {
        return lng_end;
    }

    public void setLng_end(double lng_end) {
        this.lng_end = lng_end;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_destination() {
        return time_destination;
    }

    public void setTime_destination(String time_destination) {
        this.time_destination = time_destination;
    }

    public int getIs_shared() {
        return is_shared;
    }

    public void setIs_shared(int is_shared) {
        this.is_shared = is_shared;
    }

    public String getType_shared() {
        return type_shared;
    }

    public void setType_shared(String type_shared) {
        this.type_shared = type_shared;
    }
}
