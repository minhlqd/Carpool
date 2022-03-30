package com.example.carpool.account;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.carpool.leaderboard.LeaderboardActivity;
import com.example.carpool.R;
import com.example.carpool.models.Info;
import com.example.carpool.settings.SettingsActivity;
import com.example.carpool.utils.BottomNavigationViewHelper;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.utils.SectionsStatePageAdapter;
import com.example.carpool.utils.UniversalImageLoader;
import com.example.carpool.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";
    private static final int ACTIVITY_NUMBER = 3;

    private final Context mContext = AccountActivity.this;

    private SectionsStatePageAdapter pageAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;
    private BottomNavigationView bottomNavigationView;

    //activity widgets
    private RelativeLayout mEmailUpdate;
    private RelativeLayout mPasswordUpdate;
    private RelativeLayout mDetailsUpdate;
    private RelativeLayout mCarUpdate;
    private Button mSettingsBtn;
    private Button mHelpBtn;
    private RelativeLayout mInformation;
    private ImageView profilePhoto;
    private ImageView leaderboards;
    private TextView mDisplayUsername;
    private TextView mCompleteRides;
    private TextView mEmail;
    private RatingBar mRatingBar;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(mContext);

        if (mAuth.getCurrentUser() != null){
            //Gets userID of current user signed in
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        checkNotifications();
        setupFirebaseAuth();
        setupFragments();
        setupBottomNavigationView();
        setupActivityWidgets();

        // OnClick Listener to navigate to the fragments
        mEmailUpdate.setOnClickListener(v ->
                getSupportFragmentManager().beginTransaction().replace(R.id.account_content, new EmailUpdateFragment()).commit()
        );

        mSettingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, SettingsActivity.class);
            startActivity(intent);
        });


        mHelpBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, HelpFragment.class);
            startActivity(intent);
        });


        leaderboards.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, LeaderboardActivity.class);
            startActivity(intent);
        });

        mPasswordUpdate.setOnClickListener(v ->
                getSupportFragmentManager().beginTransaction().replace(R.id.account_content, new PasswordUpdateFragment()).commit()
        );

        mDetailsUpdate.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.account_content, new DetailsUpdateFragment()).commit();
        });

        mInformation.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ProfileActivity.class);
            startActivity(intent);
        });

        mCarUpdate.setOnClickListener(v -> {
            mRelativeLayout.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.account_content, new CarUpdateFragment()).commit();
        });
    }

    private void setupFragments() {
        pageAdapter = new SectionsStatePageAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(new EmailUpdateFragment(), getString(R.string.edit_email)); //fragment 0
        pageAdapter.addFragment(new PasswordUpdateFragment(), getString(R.string.edit_password));  //fragment 1
        pageAdapter.addFragment(new DetailsUpdateFragment(),  getString(R.string.edit_details));  //fragment 2
        pageAdapter.addFragment(new CarUpdateFragment(),  getString(R.string.car_information));  //fragment 3
    }

    private void setViewPager(int fragmentNumber) {
        mRelativeLayout.setVisibility(View.GONE);
        mViewPager.setAdapter(pageAdapter);
        Objects.requireNonNull(mViewPager.getAdapter()).notifyDataSetChanged();
        mViewPager.setCurrentItem(fragmentNumber);
    }

    private void setupActivityWidgets(){
        mViewPager = findViewById(R.id.container);
        mRelativeLayout = findViewById(R.id.relative_layout_profile);
        mEmailUpdate = findViewById(R.id.email_update);
        mPasswordUpdate = findViewById(R.id.change_password);
        mDetailsUpdate = findViewById(R.id.update_profile);
        mInformation = findViewById(R.id.information_update);
        mCarUpdate = findViewById(R.id.update_car);
        profilePhoto = findViewById(R.id.profile_image);
        mDisplayUsername = findViewById(R.id.displayUsername);
        mEmail = findViewById(R.id.email_textview);
        mCompleteRides = findViewById(R.id.rides_textview);
        mSettingsBtn = findViewById(R.id.settingsBtn);
        mHelpBtn = findViewById(R.id.helpBtn);
        mRatingBar = findViewById(R.id.ratingBar);
        leaderboards = findViewById(R.id.leaderboards);
    }


    @SuppressLint("SetTextI18n")
    private void setProfileWidgets(User user, Info info){

        UniversalImageLoader.setImage(info.getProfilePhoto(), profilePhoto, null,"");

        mDisplayUsername.setText(user.getUsername());
        mCompleteRides.setText(info.getCompletedRides() + " rides");
        mEmail.setText(user.getEmail());
        mRatingBar.setRating(info.getUserRating());
    }

    private void setupBadge(int reminderLength){
        if (reminderLength > 0){
            BottomNavigationViewHelper.addBadge(mContext, bottomNavigationView, reminderLength);
        }
    }

    private void setupBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }

    private void setupFirebaseAuth(){

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                setProfileWidgets(
                        mFirebaseMethods.getUser(dataSnapshot),
                        mFirebaseMethods.getInfo(dataSnapshot)
                );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    
    private void checkNotifications(){
        mRef.child("reminder").child(userID).addValueEventListener(new ValueEventListener() {
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
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mRelativeLayout.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }
}
