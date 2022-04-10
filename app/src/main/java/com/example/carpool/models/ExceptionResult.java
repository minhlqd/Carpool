package com.example.carpool.models;

import java.io.Serializable;

public class ExceptionResult implements Serializable {
    int statusCode;
    String message;

    public ExceptionResult(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public ExceptionResult() {
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
