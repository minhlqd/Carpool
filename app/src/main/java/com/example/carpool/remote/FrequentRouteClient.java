package com.example.carpool.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FrequentRouteClient {

    public static String BASE_URI = "http://localhost:8081/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseURL){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static FrequentRouteService getFrequentRouteClient(){
        return FrequentRouteClient.getClient(BASE_URI).create(FrequentRouteService.class);
    }
}
