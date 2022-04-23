package com.example.carpool.home;

import static com.example.carpool.utils.Utils.KEY_DESTINATION;
import static com.example.carpool.utils.Utils.KEY_LOCATION;
import static com.example.carpool.utils.Utils.KEY_SAME_GENDER;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carpool.adapter.SearchAdapter;
import com.example.carpool.R;
import com.example.carpool.models.OfferRide;
import com.example.carpool.models.Ride;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings("ALL")
public class SearchResultsActivity extends AppCompatActivity {

    private static final String TAG = "MinhMX";

    //Recycle View variables
    private final Context mContext = this;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mRecycleAdapter;
    private SearchAdapter myAdapter;
    private ArrayList<OfferRide> rides;

    //Firebase variables
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;

    //Variables
    private String user_id;
    private String location;
    private String destination;
    private String date;
    private String genderCurrentUser;
    private String genderDriverUser;
    private String user_id_driver;
    private RelativeLayout mNoResultsFoundLayout;
    private Boolean sameGender;

    public SearchResultsActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_results);
        getActivityData();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();
        if (mAuth.getCurrentUser() != null) {
            user_id = mAuth.getCurrentUser().getUid();
        }

        mNoResultsFoundLayout = (RelativeLayout) findViewById(R.id.noResultsFoundLayout);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRecycleAdapter);
        rides = new ArrayList<OfferRide>();
        Log.d(TAG, "onCreate: " + destination  + " "+  mRef.child("available_ride").orderByChild("destination").equalTo(destination).toString());


        mRef.child("available_ride").orderByChild("destination").equalTo(destination).limitToFirst(20)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                        if(dataSnapshot.exists()){
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                OfferRide ride = dataSnapshot1.getValue(OfferRide.class);
                                rides.add(ride);
                                mNoResultsFoundLayout.setVisibility(View.GONE);
                            }
                            myAdapter = new SearchAdapter(SearchResultsActivity.this, rides);
                            mRecyclerView.setAdapter(myAdapter);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SearchResultsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

        ImageView backArrow = (ImageView) findViewById(R.id.backArrowSearchRide);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ImageView exitSearchRide = (ImageView) findViewById(R.id.exitSearchRide);
        exitSearchRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getActivityData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            location = getIntent().getStringExtra(KEY_LOCATION);
            destination = getIntent().getStringExtra(KEY_DESTINATION);
            sameGender = getIntent().getExtras().getBoolean(KEY_SAME_GENDER);
            date = getIntent().getStringExtra("DATE");
        }
    }

    private Date parseDate(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatter.parse(date);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
