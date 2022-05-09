package com.example.carpool.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.carpool.R;
import com.example.carpool.register.DepthPageTransformer;
import com.example.carpool.register.RegisterViewPagerAdapter;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.utils.SectionsStatePageAdapter;
import com.example.carpool.register.RegisterStepFourFragment;
import com.example.carpool.register.RegisterStepOneFragment;
import com.example.carpool.register.RegisterStepThreeFragment;
import com.example.carpool.register.RegisterStepTwoFragment;
import com.google.android.material.tabs.TabLayout;
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

    private static final String TAG = "RegisterActivity";

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
    private String startPoint;
    private String destination;
    private Long mobileNumber;
    private int seats;
    private Boolean carOwner;
    private String role;

    //Fragment variables
    private SectionsStatePageAdapter pageAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;

    //Fragments
    private RegisterStepOneFragment mRegisterStepOneFragment;
    private RegisterStepTwoFragment mRegisterStepTwoFragment;
    private RegisterStepThreeFragment mRegisterStepThreeFragment;
    private RegisterStepFourFragment mRegisterStepFourFragment;

    private TabLayout mTabLayout;


    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = RegisterActivity.this;
        mFirebaseMethods = new FirebaseMethods(mContext);

        setupFragments();

        //instantiate objects
        //mViewPager = findViewById(R.id.container);
        /*mRelativeLayout = findViewById(R.id.removeableLayout);*/

        /*mTabLayout = findViewById(R.id.tabs);
        mViewPager = findViewById(R.id.viewpager);*/

        /*setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("TabLayout ViewPager");*/

        setViewPager();
        /*mTabLayout.setupWithViewPager(mViewPager);*/

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
        /*mRelativeLayout.setVisibility(View.GONE);*/
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, mRegisterStepOneFragment).commit();
        //RegisterViewPagerAdapter adapter = new RegisterViewPagerAdapter(getSupportFragmentManager());
        /*mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onButtonClicked(View view) {
        //int currPos = mViewPager.getCurrentItem();

        switch (view.getId()) {

            case R.id.loginBackArrow:
                //handle currPos is zero
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;

            case R.id.next_btn_one:
                //handle currPos is reached last item
                //mViewPager.setCurrentItem(1);
                //mViewPager.setCurrentItem(2);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, mRegisterStepThreeFragment).commit();
                break;

            case R.id.next_btn_register_two:
                //handle currPos is reached last item
                //mViewPager.setCurrentItem(2);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, mRegisterStepThreeFragment).commit();
                break;

            case R.id.nextBtnThree:
                //handle currPos is reached last item
                //mViewPager.setCurrentItem(3);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, mRegisterStepFourFragment).commit();
                break;

            case R.id.finish:
                gatherData();
                break;
            case R.id.restartRegistrationBtn:
                //handle currPos is zero
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, mRegisterStepOneFragment).commit();
                //mViewPager.setCurrentItem(0);
                break;

            case R.id.loginBackArrowStep:
                /*mViewPager.setCurrentItem(currPos - 1);*/
                onBackPressed();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }


    private void gatherData() {

        //Fragment 1 data (main details create account for firebase)
        this.email = mRegisterStepOneFragment.getEmail();
        String password = mRegisterStepOneFragment.getPassword();

        this.username = mRegisterStepOneFragment.getUsername();

        this.fullName = mRegisterStepThreeFragment.getFullName();
        this.dob = mRegisterStepThreeFragment.getDob();
        this.mobileNumber = mRegisterStepThreeFragment.getMobileNumber();
        this.gender = mRegisterStepThreeFragment.getGender();
        this.profile_photo = mRegisterStepThreeFragment.getUserImgURL();
        this.education = mRegisterStepThreeFragment.getEducation();
        this.work = mRegisterStepThreeFragment.getWork();
        this.bio = mRegisterStepThreeFragment.getBio();

        this.licence_number = mRegisterStepFourFragment.getLicence();
        this.car = mRegisterStepFourFragment.getCar();
        this.registration_plate = mRegisterStepFourFragment.getRegistration();
        this.seats = mRegisterStepFourFragment.getSeats();
        this.carOwner = mRegisterStepFourFragment.getCarToggle();
        this.car_photo = mRegisterStepFourFragment.getCarPhoto();
        this.role = mRegisterStepFourFragment.getRole();

        mFirebaseMethods.createAccount(this.email, password);
    }

    private void checkIfUsernameExists(final String username) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("user")
                .orderByChild("username")
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFirebaseMethods.addNewUser(
                        email, fullName, username, profile_photo, mobileNumber
                        , dob, licence_number, car, registration_plate, seats
                        , education, work, bio, carOwner, gender, car_photo
                        , startPoint, destination, role);

                mAuth.signOut();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        checkIfUsernameExists(username);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                finish();

            } else {
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
