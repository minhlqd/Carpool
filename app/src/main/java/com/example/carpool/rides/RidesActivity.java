package com.example.carpool.rides;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carpool.adapter.RidesAdapter;
import com.example.carpool.R;
import com.example.carpool.reminder.ReminderActivity;
import com.example.carpool.utils.BadgeView;
import com.example.carpool.utils.BottomNavigationViewHelper;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.models.Ride;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RidesActivity extends AppCompatActivity {

    private static final String TAG = "RidesActivity";
    private static final int ACTIVITY_NUMBER = 2;

    private BottomNavigationView bottomNavigationView;
    private RelativeLayout mNoResultsFoundLayout;
    private ImageView mNotificationBtn;

    private final Context mContext = RidesActivity.this;
    private RecyclerView mRecyclerView;
    private RidesAdapter ridesAdapter;
    private ArrayList<Ride> rides;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;

    private String driverID;
    private final int reminderLength = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides);
        setupBottomNavigationView();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseMethods = new FirebaseMethods(mContext);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        if (mAuth.getCurrentUser() != null) {
            driverID = mFirebaseMethods.getUserID();
        }

        checkNotificationsRide();

        //Setup recycler view
        mRecyclerView = findViewById(R.id.recycler_view_request);
        mRecyclerView.setHasFixedSize(true);
        rides = new ArrayList<>();

        mNoResultsFoundLayout = findViewById(R.id.noResultsFoundLayout);
        mNotificationBtn = findViewById(R.id.notificationBtn);
        mNotificationBtn.setPadding(0, 0, 10, 0);
        mNotificationBtn.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ReminderActivity.class);
            startActivity(intent);
        });

        mRef = FirebaseDatabase.getInstance().getReference().child("available_ride");

        mRef.orderByChild(driverID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                Ride ride = dataSnapshot1.getValue(Ride.class);
                                if (ride.getDriverID().equals(driverID)) {
                                    Log.i(TAG, "rideID: " + ride.getDriverID().equals(driverID));
                                    rides.add(ride);
                                    mNoResultsFoundLayout.setVisibility(View.INVISIBLE);
                                }
                            }
                            ridesAdapter = new RidesAdapter(RidesActivity.this, rides);
                            mRecyclerView.setAdapter(ridesAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RidesActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });
    }


    private void setupBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }


    private void setupBadge(int reminderLength){
        if (reminderLength > 0){
            BadgeView badgeView = new BadgeView(mContext, mNotificationBtn);
            badgeView.setTextSize(10);
            badgeView.setTextColor(Color.parseColor("#ffffff"));
            badgeView.setBadgeBackgroundColor(Color.parseColor("#ff0000"));
            badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            badgeView.setText(String.valueOf(reminderLength));
            badgeView.setBadgeMargin(5, 0);
            badgeView.show();

            //Adds badge and notification number to the BottomViewNavigation
            BottomNavigationViewHelper.addBadge(mContext, bottomNavigationView, reminderLength);
        }
    }

    private void checkNotificationsRide(){
        mRef.child("reminder").child(driverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int reminderLength = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        reminderLength++;
                    }
                }

                setupBadge(reminderLength);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }


}
