package com.example.carpool.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.carpool.R;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.utils.NonSwipeableViewPager;
import com.example.carpool.utils.SectionsStatePageAdapter;
import com.example.carpool.models.User;
import com.example.carpool.register.RegisterStepFourFragment;
import com.example.carpool.register.RegisterStepOneFragment;
import com.example.carpool.register.RegisterStepThreeFragment;
import com.example.carpool.register.RegisterStepTwoFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class RegisterActivity  extends AppCompatActivity implements RegisterStepOneFragment.OnButtonClickListener,
                                                                    RegisterStepTwoFragment.OnButtonClickListener,
                                                                    RegisterStepThreeFragment.OnButtonClickListener,
                                                                    RegisterStepFourFragment.OnButtonClickListener  {

    private static final String TAG = "MinhMX";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;


    private String append = "";
    private String username;
    private String email;
    private String fullName;
    private String dob;
    private String gender;
    private String profile_photo;
    private String licence_number;
    private String registration_plate;
    private String car;
    private String car_photo;
    private String education;
    private String work;
    private String bio;
    private Long mobileNumber;
    private int seats;
    private Boolean carOwner;

    //Fragment variables
    private SectionsStatePageAdapter pageAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;

    //Fragments
    private RegisterStepOneFragment mRegisterStepOneFragment;
    private RegisterStepTwoFragment mRegisterStepTwoFragment;
    private RegisterStepThreeFragment mRegisterStepThreeFragment;
    private RegisterStepFourFragment mRegisterStepFourFragment;


    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: started.");

        mContext = RegisterActivity.this;
        mFirebaseMethods = new FirebaseMethods(mContext);

        setupFragments();

        //instantiate objects
        mViewPager = findViewById(R.id.container);
        mRelativeLayout = findViewById(R.id.removeableLayout);

        setViewPager();

        /*mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();*/


        setupFirebaseAuth();
    }

    private void setupFragments() {
        mRegisterStepOneFragment = new RegisterStepOneFragment();
        mRegisterStepTwoFragment = new RegisterStepTwoFragment();
        mRegisterStepThreeFragment = new RegisterStepThreeFragment();
        mRegisterStepFourFragment = new RegisterStepFourFragment();
        pageAdapter = new SectionsStatePageAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(mRegisterStepOneFragment, getString(R.string.fragment)); //fragment 0
        pageAdapter.addFragment(mRegisterStepTwoFragment, getString(R.string.fragment)); //fragment 1
        pageAdapter.addFragment(mRegisterStepThreeFragment, getString(R.string.fragment)); //fragment 2
        pageAdapter.addFragment(mRegisterStepFourFragment, getString(R.string.fragment)); //fragment 3
    }

    private void setViewPager() {
        mRelativeLayout.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, mRegisterStepOneFragment).commit();
        /*Log.d(TAG, "setViewPager: navigating to fragment #: " + 0 + pageAdapter.getCount());

        if (mViewPager.getAdapter() != null) {
            mViewPager.setAdapter(null);
        }

        mViewPager.setAdapter(pageAdapter);
        mViewPager.setCurrentItem(0, true);*/
        //mViewPager.postDelayed(() -> mViewPager.setCurrentItem(0), 100);
       /*  mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Log.d(TAG, "onGlobalLayout: " + true);
            mViewPager.setCurrentItem(0, false);
        });*/
        Log.d(TAG, "setViewPager: " + mViewPager.getCurrentItem());
    }

    @Override
    public void onButtonClicked(View view) {
        int currPos = mViewPager.getCurrentItem();

        switch (view.getId()) {

            case R.id.loginBackArrow:
                //handle currPos is zero
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;

            case R.id.nextBtn1:
                //handle currPos is reached last item
                //mViewPager.setCurrentItem(1);
                //mViewPager.setCurrentItem(2);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, mRegisterStepTwoFragment).commit();
                break;

            case R.id.nextBtn2:
                //handle currPos is reached last item
                //mViewPager.setCurrentItem(2);
                Log.d(TAG, "onButtonClicked: " + "next 2");
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, mRegisterStepThreeFragment).commit();
                break;

            case R.id.nextBtn3:
                //handle currPos is reached last item
                //mViewPager.setCurrentItem(3);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, mRegisterStepFourFragment).commit();
                break;

            case R.id.nextBtn4:
                //handle currPos is reached last item
                //mViewPager.setCurrentItem(currPos + 1);
                //getSupportFragmentManager().beginTransaction().replace(R.id.content_main, mRegisterStepFourFragment).commit();
                gatherData(); //To create the account on the last step of fragment
                break;
            case R.id.restartRegistrationBtn:
                Log.d(TAG, "onButtonClicked: true");
                //handle currPos is zero
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, mRegisterStepOneFragment).commit();
                //mViewPager.setCurrentItem(0);
                break;

            case R.id.loginBackArrowStep:
                mViewPager.setCurrentItem(currPos - 1);
                break;
        }
    }


    private void gatherData() {

        //Fragment 1 data (main details create account for firebase)
        this.email = mRegisterStepOneFragment.getEmail();
        String password = mRegisterStepOneFragment.getPassword();

        //Fragment 2 data (username check)
        this.username = mRegisterStepTwoFragment.getUsername();

        //Fragment 3 data (personal info for database)
        this.fullName = mRegisterStepThreeFragment.getFullname();
        this.dob = mRegisterStepThreeFragment.getDob();
        this.mobileNumber = mRegisterStepThreeFragment.getMobileNumber();
        this.gender = mRegisterStepThreeFragment.getGender();
        this.profile_photo = mRegisterStepThreeFragment.getRegistrationPicture();
        this.education = mRegisterStepThreeFragment.getEducation();
        this.work = mRegisterStepThreeFragment.getWork();
        this.bio = mRegisterStepThreeFragment.getBio();

        //Fragment 4 car data (car info for firebase)
        this.licence_number = mRegisterStepFourFragment.getLicence();
        this.car = mRegisterStepFourFragment.getCar();
        this.registration_plate = mRegisterStepFourFragment.getRegistration();
        this.seats = mRegisterStepFourFragment.getSeats();
        this.carOwner = mRegisterStepFourFragment.getCarToggle();
        this.car_photo = mRegisterStepFourFragment.getCarPhoto();

        //Main registration once all data is gathered
        mFirebaseMethods.createAccount(this.email, password);
    }

   /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Check is @param username already exists in teh database
     * @param username
     */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: Checking if  " + username + " already exists.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("user")
                .orderByChild("username")
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + singleSnapshot.getValue(User.class).getUsername());
                        append = myRef.push().getKey().substring(3,10);
                        Log.d(TAG, "onDataChange: username already exists. Appending random string to name: " + append);
                    }
                }

                String mUsername = "";
                mUsername = username + append;

                //add new user to the database
                mFirebaseMethods.addNewUser(email, fullName, mUsername, profile_photo, mobileNumber, dob, licence_number, car, registration_plate, seats, education, work, bio, carOwner, gender, car_photo);

                Toast.makeText(mContext, "Signup successful. You may login now!.", Toast.LENGTH_SHORT).show();

                mAuth.signOut();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        checkIfUsernameExists(username);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                finish();

            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
