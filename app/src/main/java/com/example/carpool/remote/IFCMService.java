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
            "Authorization:key=AAAAKGkpC4M:APA91bGpHzuEemLCrKxRllYmL2QCiqKoLiTzHNXfGxNyGNh2u0fmu2YB-XAr7idcmWhvvPvz4kt0GA9g8ekLDIAtAVIesyjKSIVnpNetZOvwkLU68V_XR1bQ0DyJmZQ-IX-8ExWKq4Ps"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
