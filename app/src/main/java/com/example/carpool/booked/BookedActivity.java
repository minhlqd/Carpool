package com.example.carpool.booked;

import static com.example.carpool.utils.Utils.checkNotifications;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carpool.adapter.BookingAdapter;
import com.example.carpool.R;
import com.example.carpool.interfaces.ResponseBooked;
import com.example.carpool.models.Info;
import com.example.carpool.utils.BottomNavigationViewHelper;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.models.BookingResults;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookedActivity extends AppCompatActivity implements ResponseBooked {

    private static final String TAG = "BookedActivity";
    private static final int ACTIVITY_NUMBER = 3;

    //View variables
    private RelativeLayout mNoResultsFoundLayout;
    private BottomNavigationView bottomNavigationView;

    //Recycle View variables
    private final Context mContext = BookedActivity.this;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mRecycleAdapter;
    private BookingAdapter myAdapter;
    private ArrayList<BookingResults> rides;

    //Firebase variables
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private ImageView notFoundIcon;
    private TextView notFoundBooked;

    private String user_id;
    private Boolean isDriver = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked);
        setupBottomNavigationView();


        notFoundBooked = findViewById(R.id.notFoundBooked);
        notFoundIcon = findViewById(R.id.notFoundIcon);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRecycleAdapter);
        rides = new ArrayList<BookingResults>();

        mNoResultsFoundLayout = findViewById(R.id.noResultsFoundLayout);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        if (mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getUid();
            mRef.child("info").child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Info info = snapshot.getValue(Info.class);
                    isDriver = info.getCarOwner();
                    Log.d(TAG, "onDataChange: " + isDriver);
                    if (isDriver) {
                        mRef.child("request_ride").child(user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                        BookingResults r = dataSnapshot1.getValue(BookingResults.class);
                                        if (!r.getAccepted()) {
                                            rides.add(r);
                                        }
                                        Log.d(TAG, "onDataChange: " + r);
                                        notFoundBooked.setVisibility(View.INVISIBLE);
                                        notFoundIcon.setVisibility(View.INVISIBLE);
                                    }
                                }
                                Log.d(TAG, "onDataChange: " + rides.size());

                                myAdapter = new BookingAdapter(BookedActivity.this, rides, isDriver, BookedActivity.this);
                                mRecyclerView.setAdapter(myAdapter);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        mRef.child("request_ride").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                        for (DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()) {
                                            BookingResults r = dataSnapshot2.getValue(BookingResults.class);

                                            if (r.getPassengerID().equals(user_id)) {
                                                rides.add(r);
                                            }
                                            Log.d(TAG, "onDataChange: " + r);
                                            notFoundBooked.setVisibility(View.INVISIBLE);
                                            notFoundIcon.setVisibility(View.INVISIBLE);
                                            /*mNoResultsFoundLayout.setVisibility(View.INVISIBLE);*/
                                        }
                                    }
                                }
                                Log.d(TAG, "onDataChange: " + rides.size());

                                myAdapter = new BookingAdapter(BookedActivity.this, rides, isDriver, BookedActivity.this);
                                mRecyclerView.setAdapter(myAdapter);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            Log.i(TAG, "onCreate: "+ user_id);
        }

        checkNotifications(mRef, user_id, mContext, bottomNavigationView);

    }


    private void setupBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }


    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void responseBooked(Boolean isAccept, String rideId, int pos) {
        Log.d(TAG, "responseBooked: " + isAccept + " " +  rideId);
        if (isAccept) {
            mRef.child("request_ride").child(user_id).child(rideId).child("accepted").setValue(true);
            mRef.child("available_ride").child(rideId).child("seatsAvailable").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int seatsAvailable = dataSnapshot.getValue(Integer.class);
                    mRef.child("available_ride").child(rideId).child("seatsAvailable").setValue(seatsAvailable - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            mRef.child("participant").child(rideId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            mRef.child("request_ride").child(user_id).child(rideId).removeValue();
        }
        rides.remove(pos);
        myAdapter.notifyDataSetChanged();
        checkNotifications(mRef, user_id, mContext, bottomNavigationView);
    }

}
