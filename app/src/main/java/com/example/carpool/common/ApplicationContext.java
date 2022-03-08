package com.example.carpool.common;


import android.app.Application;

public class ApplicationContext extends Application {

    private static ApplicationContext mContext;
    private static String sds;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static ApplicationContext getContext() {
        return mContext;
    }

    public static String getDuration() {
        return sds;
    }

    public static void setDuration(String duration) {
        sds = duration;
    }
}
