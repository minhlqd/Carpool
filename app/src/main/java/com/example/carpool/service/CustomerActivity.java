package com.example.carpool.service;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carpool.R;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.utils.UniversalImageLoader;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerActivity extends AppCompatActivity {
    private static final String TAG = "CustomerActivity";

    private TextView txtUsername, txtTo, txtFrom;
    private String title, body, username, profile_photo, to, from, userID, rideID;
    private Boolean rideAccepted;

    //Widgets
    private FloatingActionButton acceptBtn, declineBtn;
    private CircleImageView mRequestProfilePhoto;

    //Firebase
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseMethods mFirebaseMethods;

    private final Context mContext = CustomerActivity.this;

    public CustomerActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        final CustomerActivity c = this;

        getActivityData();
        setupDialog();

        mFirebaseMethods = new FirebaseMethods(mContext);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        acceptBtn = findViewById(R.id.confirmRideBtn);
        declineBtn = findViewById(R.id.declineRideBtn);
        mRequestProfilePhoto = findViewById(R.id.requestProfilePhoto);
        txtTo = findViewById(R.id.to);
        txtFrom = findViewById(R.id.from);
        txtUsername = findViewById(R.id.message);
        txtUsername.setText("Hi, i'm " + username + " and would like to request a seat on your journey!");


        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptRide();
            }
        });

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineRide();
            }
        });
//     txtAddress = (TextView) findViewById(R.id.txtAddress);
//     txtTime.setText(title);
//     txtDistance.setText(body);
       txtTo.setText("To: " + to);
       txtFrom.setText("From: " + from);

        UniversalImageLoader.setImage(profile_photo, mRequestProfilePhoto, null,"");
    }

    private void getActivityData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = getIntent().getStringExtra("title");
            body = getIntent().getStringExtra("userID");
            username = getIntent().getStringExtra("username");
            profile_photo = getIntent().getStringExtra("profile_photo");
            to = getIntent().getStringExtra("to").replaceAll("\n", ", ");
            from = getIntent().getStringExtra("from").replaceAll("\n", ", ");
            rideID = getIntent().getStringExtra("rideID");
        }
    }

    private void setupDialog(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void acceptRide(){
        myRef.child("requestRide")
                .child(rideID)
                .child(userID)
                .child("accepted")
                .setValue(true);

        //Will close the intent when the ride is accepted
        finish();
    }

    private void declineRide(){

        myRef.child("requestRide")
                .child(rideID)
                .child(userID)
                .removeValue();

        //Will close the intent when the ride is accepted
        finish();
    }


}
