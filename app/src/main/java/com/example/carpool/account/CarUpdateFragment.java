package com.example.carpool.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carpool.R;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.utils.UniversalImageLoader;
import com.example.carpool.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CarUpdateFragment extends Fragment {

    private static final String TAG = "CarUpdateFragment";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    private FirebaseUser firebaseUser;

    //Fragment view
    private View view;
    private CircleImageView mCarPhoto;
    private EditText mCar, mRegistration, mLicence, mSeats;
    private Button mSnippetCarBtn;
    private RadioGroup mCarOwnerRadioGroup;
    private RadioButton ownerYesButton, ownerNoButton;
    private RelativeLayout mCarDetailsLayout;

    //vars
    private User mUserSettings;
    private Boolean carOwner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_car, container, false);
        mCarPhoto = view.findViewById(R.id.uploadCarPicture);
        mCar = view.findViewById(R.id.carEditText);
        mRegistration = view.findViewById(R.id.registrationEditText);
        mLicence = view.findViewById(R.id.licenceEditText);
        mSeats = view.findViewById(R.id.seatsEditText);
        mCarOwnerRadioGroup = view.findViewById(R.id.carToggle);
        ownerYesButton = view.findViewById(R.id.driver);
        ownerNoButton = view.findViewById(R.id.passenger);
        mCarDetailsLayout = view.findViewById(R.id.carDetailsLayout);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("user").child(firebaseUser.getUid());
        mFirebaseMethods = new FirebaseMethods(getActivity());


        mSnippetCarBtn = view.findViewById(R.id.snippetCarDetailsBtn);
        mSnippetCarBtn.setOnClickListener(v -> saveProfileSettings());

        setupFirebaseAuth();

        getUserInformation();


        //Setup back arrow for navigating back to 'ProfileActivity'
        ImageView backArrow = view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), AccountActivity.class);
            startActivity(intent);
        });

        mCarOwnerRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.driver:
                        ownerYesButton.setChecked(true);
                        mCarDetailsLayout.setVisibility(View.VISIBLE);
                        break;
                    case R.id.passenger:
                        ownerNoButton.setChecked(true);
                        mCarDetailsLayout.setVisibility(View.GONE);
                        break;
                }
            }
        });

        return view;
    }

    private void saveProfileSettings(){
        final String car = mCar.getText().toString();
        final String registration = mRegistration.getText().toString();
        final String licence = mLicence.getText().toString();
        final int seats = Integer.parseInt(mSeats.getText().toString());

        if (car.length() > 0 && registration.length() > 0 && licence.length() > 0 && mSeats.getText().toString().length() > 0) {
            /*mRef.child("car").setValue(car);
            mRef.child("registration").setValue(registration);
            mRef.child("licence").setValue(licence);
            mRef.child("seats").setValue(seats);*/
            HashMap<String, Object> hashMapCarInfo = new HashMap<>();
            hashMapCarInfo.put("carOwner", true);
            hashMapCarInfo.put("car", car);
            hashMapCarInfo.put("registration", registration);
            hashMapCarInfo.put("licence",licence);
            hashMapCarInfo.put("seats", seats);
            mRef.updateChildren(hashMapCarInfo);
            requireActivity().onBackPressed();
        } else {
            Toast.makeText(requireContext(), "Fill", Toast.LENGTH_SHORT).show();
        }


//        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//
//
//
//                //If the user is the current one then no changes have been made
//                if (!mUserSettings.getUsername().equals(username)){
//
//                    checkIfUsernameExists(username);
//                }
//                //user changed there username, checking for uniquness
//                else {
//
//                }
//
//                //user did not change there username and email
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void checkIfUsernameExists(final String username) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("user")
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    //add the username
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(getActivity(), "Username changed", Toast.LENGTH_SHORT).show();

                }
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()){
                        Toast.makeText(getActivity(), "Username already exists", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setProfileWidgets(User userSettings){

        mUserSettings = userSettings;

        UniversalImageLoader.setImage(userSettings.getCarPhoto(), mCarPhoto, null,"");

        mCar.setText(userSettings.getCar());
        mRegistration.setText(userSettings.getRegistrationPlate());
        mLicence.setText(userSettings.getLicenceNumber());
        mSeats.setText(String.valueOf(userSettings.getSeats()));

        mCarPhoto.setOnClickListener(v -> {
           /* Intent intent = new Intent(getActivity(), ShareActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
            getActivity().startActivity(intent);
            getActivity().finish();*/
        });
    }


    private void setupFirebaseAuth(){

        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));

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

    public void getUserInformation() {
        mRef.child("user").child(userID).child("carOwner").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carOwner = dataSnapshot.getValue(Boolean.class);
                /*if (!carOwner) {
                    ownerNoButton.setChecked(true);
                    mCarDetailsLayout.setVisibility(View.GONE);
                    mCar.setText("");
                    mRegistration.setText("");
                    mLicence.setText("");
                    mSeats.setText("");
                    mCarPhoto.setImageDrawable(null);
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
