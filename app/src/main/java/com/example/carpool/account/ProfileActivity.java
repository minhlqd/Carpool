package com.example.carpool.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carpool.R;
import com.example.carpool.login.LoginActivity;
import com.example.carpool.models.Info;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.utils.UniversalImageLoader;
import com.example.carpool.models.User;
import com.example.carpool.models.UserReview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Objects;


public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    private final Context mContext = ProfileActivity.this;

    private ImageView profilePhoto, mBackBtn;
    private TextView mDisplayUsername, mPersonalBio, mEducationTextview, mWorkTextview, mReview1, mReview2, mReview3;
    private RatingBar mRatingBar;
    private LinearLayout mReviewLayout;
    private Button logout;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;
    private final HashMap<Integer, TextView> textViewHashMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(mContext);

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }


        setupActivityWidgets();

        logout.setOnClickListener(v -> {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(userID);
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        getActivityData();
        setupFirebaseAuth();

        mBackBtn.setOnClickListener(v -> finish());

    }

    private void getActivityData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = getIntent().getStringExtra("userID");
        }
    }


    private void setupActivityWidgets(){
        //instantiate objects
        mBackBtn = findViewById(R.id.backBtn);
        profilePhoto = findViewById(R.id.profile_photo);
        mDisplayUsername = findViewById(R.id.nameTextview);
        mPersonalBio = findViewById(R.id.personalBio);
        mEducationTextview = findViewById(R.id.educationTextview);
        mPersonalBio = findViewById(R.id.personaBioText);
        mWorkTextview = findViewById(R.id.workTextview);
        //mReview1 = (TextView) findViewById(R.id.review1);
        mRatingBar = findViewById(R.id.ratingBar);
        logout = findViewById(R.id.logout);

        //mReviewLayout = (LinearLayout) findViewById(R.id.reviewLayout);
        //mReview1.setVisibility(View.INVISIBLE);
    }


    private void setProfileWidgets(User user, Info info){

        UniversalImageLoader.setImage(info.getProfilePhoto(), profilePhoto, null,"");

        mDisplayUsername.setText(user.getUsername());
        mRatingBar.setRating(info.getUserRating());
        mPersonalBio.setText(info.getBio());
        mEducationTextview.setText(info.getEducation());
        mWorkTextview.setText(info.getWork());
    }

    private void fetchUserReviews(){

        final UserReview userReview = new UserReview();

        mRef.child("user").child(userID).child("userReviews").limitToFirst(3).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        for (DataSnapshot snapshot : ds.getChildren()){
                            if (snapshot.getValue() != null && snapshot.getKey() != null) {
                                userReview.setComment(snapshot.getValue().toString());
                                userReview.setRating(Float.parseFloat(snapshot.getKey()));

                                int textViewCount = 1;

                                Objects.requireNonNull(textViewHashMap.get(textViewCount)).setText(userReview.getComment());
                                Log.i(TAG, "onDataChange: " + userReview);

                                textViewCount++;
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setupFirebaseAuth(){
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setProfileWidgets(mFirebaseMethods.getSpecificUser(dataSnapshot, userID),
                        mFirebaseMethods.getInfo(dataSnapshot));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
