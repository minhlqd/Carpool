package com.example.carpool.remote;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FCMClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseURL){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        Log.d("FirebaseFCM", "getClient: " + retrofit.baseUrl());
        return retrofit;
    }
}
