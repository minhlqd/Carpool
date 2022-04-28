package com.example.carpool.fragment;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carpool.R;
import com.example.carpool.utils.Utils;


public class FindRideActivity extends AppCompatActivity {

    private static final String TAG = "FindRideActivity";

    private EditText mDestinationEditText, mLocationEditText;

    private String destination, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_ride);

        setupWidgets();
        getActivityData();
        populateActivityWidgets();

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void populateActivityWidgets() {
        mDestinationEditText.setText(destination);
        mLocationEditText.setText(location);
    }

    private void setupWidgets() {
        mDestinationEditText = findViewById(R.id.destination);
        mLocationEditText = findViewById(R.id.location);
    }

    private void getActivityData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            destination = getIntent().getStringExtra(Utils.KEY_DESTINATION);
            location = getIntent().getStringExtra(Utils.KEY_LOCATION);
        }
    }
}
