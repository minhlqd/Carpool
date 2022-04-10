package com.example.carpool.common;

import com.example.carpool.remote.FCMClient;
import com.example.carpool.remote.IFCMService;

public class Common {

    public static String fcmURL = "https://fcm.googleapis.com/";
    public static String START = "start";
    public static String className;
    public static String userID = null;
    public static String statusTrip = null;

    public static IFCMService getFCMService(){
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }

    public static String getClassName() {
        return className;
    }

    public static void setClassName(String className) {
        Common.className = className;
    }
}
