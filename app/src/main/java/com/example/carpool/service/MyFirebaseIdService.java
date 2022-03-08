package com.example.carpool.service;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.example.carpool.models.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseIdService extends FirebaseMessagingService {

//    @Override
//    public void onNewToken(@NonNull String s) {
//        super.onNewToken(s);
//        String refreshedToken = FirebaseInstallations.getInstance().getToken();
//        updateTokenToServer(refreshedToken);
//    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        updateTokenToServer(s);
    }


//    @Override
//    public void onTokenRefresh() {
//        super.onTokenRefresh();
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        updateTokenToServer(refreshedToken);
//    }

    private void updateTokenToServer(String refreshedToken) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");

        Token token = new Token(refreshedToken);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(token);

        }
    }
}
