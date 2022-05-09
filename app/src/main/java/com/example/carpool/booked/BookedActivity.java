package com.example.carpool.booked;

import static com.example.carpool.utils.Utils.AVAILABLE_RIDE;
import static com.example.carpool.utils.Utils.REQUEST_RIDE;
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
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carpool.adapter.BookingAdapter;
import com.example.carpool.adapter.RequestAdapter;
import com.example.carpool.R;
import com.example.carpool.interfaces.ResponseBooked;
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

/**
 * create by minhmx on 22/04/2022
 */

public class BookedActivity extends AppCompatActivity implements ResponseBooked {

    private static final String TAG = "BookedActivity";
    private static final int ACTIVITY_NUMBER = 3;

    //View variables
    private RelativeLayout mNoResultsFoundLayout;
    private BottomNavigationView bottomNavigationView;

    //Recycle View variables
    private final Context mContext = BookedActivity.this;
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewBooked;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mRecycleAdapter;
    private RequestAdapter myAdapter;

    private BookingAdapter bookedAdapter;
    private ArrayList<BookingResults> rides;
    private ArrayList<BookingResults> books;

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

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked);
        setupBottomNavigationView();


        notFoundBooked = findViewById(R.id.notFoundBooked);
        notFoundIcon = findViewById(R.id.notFoundIcon);

        mRecyclerView = findViewById(R.id.recycler_view_request);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRecycleAdapter);
        rides = new ArrayList<BookingResults>();
        books = new ArrayList<>();

        mRecyclerViewBooked = findViewById(R.id.recycler_view_booked);
        mRecyclerViewBooked.setHasFixedSize(true);
        mRecyclerViewBooked.setLayoutManager(new LinearLayoutManager(this));

        mNoResultsFoundLayout = findViewById(R.id.noResultsFoundLayout);
        toolbar = findViewById(R.id.toolbar_booked_ride);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        if (mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getUid();
            mRef.child(REQUEST_RIDE).child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            BookingResults booking = dataSnapshot1.getValue(BookingResults.class);
                            if (!booking.getAccepted()) {
                                rides.add(booking);
                            }
                            notFoundBooked.setVisibility(View.INVISIBLE);
                            notFoundIcon.setVisibility(View.INVISIBLE);
                        }
                    }

                    myAdapter = new RequestAdapter(BookedActivity.this, rides, true, BookedActivity.this, mRef);
                    mRecyclerView.setAdapter(myAdapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            mRef.child(REQUEST_RIDE).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                BookingResults r = dataSnapshot2.getValue(BookingResults.class);

                                if (r.getPassengerID() != null) {
                                    if (r.getPassengerID().equals(user_id)) {
                                        books.add(r);
                                    }
                                }
                                notFoundBooked.setVisibility(View.INVISIBLE);
                                notFoundIcon.setVisibility(View.INVISIBLE);
                                /*mNoResultsFoundLayout.setVisibility(View.INVISIBLE);*/
                            }
                        }
                    }

                    bookedAdapter = new BookingAdapter(BookedActivity.this, books, false, BookedActivity.this, mRef);
                    mRecyclerViewBooked.setAdapter(bookedAdapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

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

    int seatsAvailable = 0;

    @Override
    public void responseBooked(Boolean isAccept, String requestID, String rideID, int pos, String passengerId, int seat) {
        if (isAccept) {
            Log.d(TAG, "responseBooked: " + user_id + " " + requestID);
            mRef.child(REQUEST_RIDE).child(user_id).child(requestID).child("accepted").setValue(true);
            mRef.child(AVAILABLE_RIDE).child(rideID).child("seatsAvailable").setValue(seat - 1);
            String user = "passenger_" + (4-seat);
            mRef.child("participant").child(rideID).child(user).setValue(passengerId);
        } else {
            mRef.child(REQUEST_RIDE).child(user_id).child(requestID).removeValue();
        }
        rides.remove(pos);
        myAdapter.notifyDataSetChanged();
        checkNotifications(mRef, user_id, mContext, bottomNavigationView);
    }
}
