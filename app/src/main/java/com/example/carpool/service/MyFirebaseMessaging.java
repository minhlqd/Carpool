package com.example.carpool.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.carpool.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;


public class MyFirebaseMessaging extends FirebaseMessagingService {
    private FirebaseAuth mAuth;
    private String userID;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            //Gets userID of current user signed in
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

       if (remoteMessage.getData().isEmpty()){

       } else {
           if(remoteMessage.getData().get("body").contains(userID)){
               showNotification(remoteMessage.getData());
           }
       }
    }


    private void showNotification(@NonNull Map<String,String> data) {
        String title = data.get("title");
        String body = data.get("body");
        String[] username = data.get("username").split(",");
        String rideID = data.get("rideID");
        String to = data.get("to");


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "TEST";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification"
                    , NotificationManager.IMPORTANCE_DEFAULT );

            notificationChannel.setDescription("Testing");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 100});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());

        Intent intent = new Intent(this, CustomerActivity.class);
        intent.putExtra("username", username[0]);
        intent.putExtra("to", to);
        intent.putExtra("title", title);
        intent.putExtra("userID", body);
        intent.putExtra("rideID", rideID);
        intent.putExtra("from", username[2]);
        intent.putExtra("profile_photo", username[1]);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

}
