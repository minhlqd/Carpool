package com.example.carpool.remote;


import com.example.carpool.models.FCMResponse;
import com.example.carpool.models.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAzBNMfdo:APA91bGuQgsCfVGiw_5HlFXmHOAWXeQfmP1wmnaiPm8E6HNNXk94y002SpKbTiH3vIZgCeULH2U80XEV7WbmgjGyZI6Tf75b6eNqgUntnoXsqwkJB4JSRQ7d_FVHk3IuyKLVs43n5lUK"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
