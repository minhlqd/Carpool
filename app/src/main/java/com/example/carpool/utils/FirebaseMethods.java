package com.example.carpool.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.carpool.R;
import com.example.carpool.models.Info;
import com.example.carpool.models.OfferRide;
import com.example.carpool.models.Reminder;
import com.example.carpool.models.User;
import com.example.carpool.models.UserReview;
import com.example.carpool.rides.RidesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //Firebase
    private FirebaseAuth mAuth;
    private Context mContext;
    private String userID;
    private String profile_picture;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;


    public FirebaseMethods(Context context){
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void setUserID(String id){
        userID= id;
    }

    public String getUserID(){
        return userID;
    }


    public void createAccount(final String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(mContext, "Authenticated failed",
                                Toast.LENGTH_SHORT).show();
                    }
                    else if(task.isSuccessful()){
                        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    }
                });
    }

    public void offerRide(String driverID, String username, String currentLocation, String destination, String dateOfJourney,
                          int seatsAvailable, String licencePlate, double currentlongitude, double currentlatitude, boolean sameGender, int luggageAllowance, String car,
                          String pickupTime, int extraTime, String profile_photo, long cost, int completeRides, float userRating, String duration, String pickupLocation){

        String rideId = mFirebaseDatabase.getReference().push().getKey();


        OfferRide offerRide = new OfferRide(rideId, driverID, username, currentLocation, destination, dateOfJourney, seatsAvailable, licencePlate,
                                            currentlongitude, currentlatitude, sameGender, luggageAllowance, car, pickupTime, extraTime, profile_photo, cost,
                                            completeRides, userRating, duration, pickupLocation);

        myRef.child(Utils.AVAILABLE_RIDE)
                .child(rideId)
                .setValue(offerRide);

        myRef.child("participant")
                .child(rideId)
                .child("driver")
                .setValue(driverID);
    }


    public void deleteRide(String driverID, String rideID, Context context){
        myRef.child(Utils.AVAILABLE_RIDE)
                .child(rideID)
                .removeValue();

        Intent intent1 = new Intent(context, RidesActivity.class);
        context.startActivity(intent1);

    }

    int totalPoints = 0;

    public void addPoints(String userID, int points) {
        myRef.child("info").child(userID).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalPoints = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.child("info").child(userID).child("points").setValue(totalPoints + points);
    }



    public void addReview(String userID, Float rating, String comment) {
        UserReview userReview = new UserReview(rating, comment);

        String reviewNumber = checkAmountOfReviews(userID);

        myRef.child("user_review")
                .child(userID)
                .child(reviewNumber)
                .setValue(userReview);
    }

    public void addNewUser(String email, String full_name, String username, String profile_photo, long mobile_number, String dob, String licence_number,
                           String car, String registration_plate, int seats, String education, String work, String bio ,Boolean carOwner, String gender, String car_photo, String startPoint, String destination, String role){

        User user = new User(userID ,email, full_name, username, profile_photo);
        Info info = new Info(profile_photo, dob, licence_number, gender, registration_plate, car, car_photo, education, work, bio, mobile_number, 0 , seats, 0, 50, carOwner, startPoint, destination, role);
        myRef.child("user")
                .child(userID)
                .setValue(user);

        myRef.child("info")
                .child(userID)
                .setValue(info);
    }

    public void addReminder(String date, String reminder,  long reminderLength){
        Reminder reminder1 = new Reminder(date, reminder);

        myRef.child("reminder").child(userID).child(String.valueOf(reminderLength + 1)).setValue(reminder1);

    }

    public String checkAmountOfReviews(final String notificationComment){
        myRef.child("reminder").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        for (DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()){
                            if (dataSnapshot2.getValue() != null && dataSnapshot2.getValue().equals(notificationComment)){
                                deleteReminder(dataSnapshot1.getKey());
                            }
                        }
                    }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
        return null;
    }

    public void checkForReminder(final String notificationComment){
        myRef.child("reminder").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        for (DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()){
                            if (dataSnapshot2.getValue() != null && dataSnapshot2.getValue().equals(notificationComment)){
                                /*deleteReminder(dataSnapshot1.getKey());*/
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

    public void deleteReminder(String notificationNumber){
        myRef.child("reminder").child(userID).child(notificationNumber).removeValue();
    }

    public void checkNotifications(final String date, final String reminder){
        myRef.child("reminder").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long reminderLength = 0;
                if (dataSnapshot.exists()) {
                    reminderLength = dataSnapshot.getChildrenCount();
                }
                //Passes the number of notifications onto the setup badge method
                addReminder(date, reminder, reminderLength);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public User getUser(@NonNull DataSnapshot dataSnapshot){

        User user = new User();
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            if(Objects.equals(ds.getKey(), "user")){

                try {
                    user.setUsername(ds.child(userID)
                            .getValue(User.class)
                            .getUsername());

                    user.setEmail(ds.child(userID)
                            .getValue(User.class)
                            .getEmail());
                    user.setFullName(ds.child(userID)
                            .getValue(User.class)
                            .getEmail());
                } catch (NullPointerException e){
                    Log.d(TAG, "getUserSettings: NullPointerException: " + e.getMessage());
                }

            }
        }
        return user;
    }

    public Info getInfo(@NonNull DataSnapshot dataSnapshot, String userID){
        Info info = new Info();
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            if(Objects.equals(ds.getKey(), "info")){
                try {
                    info.setProfilePhoto(ds.child(userID)
                            .getValue(Info.class)
                            .getProfilePhoto());

                    info.setDateOfBird(ds.child(userID)
                            .getValue(Info.class)
                            .getDateOfBird());

                    info.setMobileNumber(ds.child(userID)
                            .getValue(Info.class)
                            .getMobileNumber());

                    info.setLicenceNumber(ds.child(userID)
                            .getValue(Info.class)
                            .getLicenceNumber());

                    info.setCompletedRides(ds.child(userID)
                            .getValue(Info.class)
                            .getCompletedRides());

                    info.setUserRating(ds.child(userID)
                            .getValue(Info.class)
                            .getUserRating());

                    info.setLicenceNumber(ds.child(userID)
                            .getValue(Info.class)
                            .getLicenceNumber());

                    info.setCar(ds.child(userID)
                            .getValue(Info.class)
                            .getCar());

                    info.setRegistrationPlate(ds.child(userID)
                            .getValue(Info.class)
                            .getRegistrationPlate());

                    info.setSeats(ds.child(userID)
                            .getValue(Info.class)
                            .getSeats());

                    info.setGender(ds.child(userID)
                            .getValue(Info.class)
                            .getGender());

                    info.setCarPhoto(ds.child(userID)
                            .getValue(Info.class)
                            .getCarPhoto());

                    info.setEducation(ds.child(userID)
                            .getValue(Info.class)
                            .getEducation());

                    info.setWork(ds.child(userID)
                            .getValue(Info.class)
                            .getWork());

                    info.setBio(ds.child(userID)
                            .getValue(Info.class)
                            .getBio());

                } catch (NullPointerException e){
                    Log.d(TAG, "getUserSettings: NullPointerException: " + e.getMessage());
                }

            }
        }
        return info;
    }

    public User getSpecificUser(@NonNull DataSnapshot dataSnapshot, String user_id){
        User user = new User();
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            if(ds.getKey() != null && ds.getKey().equals("user")){
                try {
                    user.setUsername(ds.child(user_id)
                            .getValue(User.class)
                            .getUsername());

                        user.setEmail(ds.child(user_id)
                                .getValue(User.class)
                                .getEmail());
                } catch (NullPointerException e){
                }

            }
        }
        return user;
    }

    public void updateUsername(String username){
        myRef.child("user")
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }

    public void updateEmail(String email){
        myRef.child("user")
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);
    }

    public void participant() {

    }

}
