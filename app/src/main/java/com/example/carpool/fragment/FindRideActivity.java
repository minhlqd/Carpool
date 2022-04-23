package com.example.carpool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carpool.home.HomeActivity;
import com.example.carpool.R;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FindRideActivity extends AppCompatActivity {

    private static final String TAG = "FindRideActivity";


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //Widgets
    private EditText mDestinationEditText, mFromEditText;
    private Button mChangeEmailButton;

    //vars
    private User mUserSettings;
    private String destinationId, locationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_ride);

        setupWidgets();
        getActivityData();
        populateActivityWidgets();

        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindRideActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void populateActivityWidgets() {
        mDestinationEditText.setText(destinationId);
        mFromEditText.setText(locationId);
    }

    private void setupWidgets() {
        mDestinationEditText = (EditText) findViewById(R.id.destination);
        mFromEditText = (EditText) findViewById(R.id.location);
    }

    private void getActivityData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            destinationId = getIntent().getStringExtra("DESTINATION");
            locationId = getIntent().getStringExtra("LOCATION");
        }
    }
}
