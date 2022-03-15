package com.example.carpool.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.carpool.R;
import com.example.carpool.models.OfferRide;
import com.example.carpool.models.Reminder;
import com.example.carpool.models.User;
import com.example.carpool.models.UserReview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
            userID = mAuth.getCurrentUser().getUid().toString();
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
                        userID = mAuth.getCurrentUser().getUid();
                    }
                });
    }

    public void offerRide(String user_id, String username, String currentLocation, String destination, String dateOfJourney,
                          int seatsAvailable, String licencePlate, double currentlongitude, double currentlatitude, boolean sameGender, int luggageAllowance, String car,
                          String pickupTime, int extraTime, String profile_photo, int cost, int completeRides, int userRating, String duration, String pickupLocation){

        String rideKey = mFirebaseDatabase.getReference().push().getKey();


        OfferRide offerRide = new OfferRide(rideKey, user_id, username,destination, currentLocation, dateOfJourney, seatsAvailable, licencePlate,
                                            currentlongitude, currentlatitude, sameGender, luggageAllowance, car, pickupTime, extraTime, profile_photo, cost,
                                            completeRides, userRating, duration, pickupLocation);

        myRef.child(mContext.getString(R.string.dbName_availableRide))
                .child(rideKey)
                .setValue(offerRide);
    }


    public void deleteRide(String rideID){

        myRef.child("availableRide")
            .child(rideID)
            .removeValue();

    }

    int totalPoints = 0;

    public void addPoints(String userID, int points) {
        myRef.child("user").child(userID).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalPoints = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.child("user").child(userID).child("points").setValue(totalPoints + points);
    }



    public void addReview(String userID, Float rating, String comment) {
        UserReview userReview = new UserReview(rating, comment);

        String reviewNumber = checkAmountOfReviews(userID);

        myRef.child("userReview")
                .child(userID)
                .child(reviewNumber)
                .setValue(userReview);
    }

    public void addNewUser(String email, String full_name, String username, String profile_photo, long mobile_number, String dob, String licence_number,
                           String car, String registration_plate, int seats, String education, String work, String bio ,Boolean carOwner, String gender, String car_photo){

        User user = new User(userID ,email, full_name, username, profile_photo, mobile_number, dob, licence_number, 0, 0,  car, registration_plate, seats,education, work, bio, carOwner, gender, 50 ,car_photo);

        myRef.child("user")
                .child(userID)
                .setValue(user);
    }

    public void addReminder(String date, String reminder,  long reminderLength){
        Reminder reminder1 = new Reminder(date, reminder);

        myRef.child("Reminder").child(userID).child(String.valueOf(reminderLength + 1)).setValue(reminder1);

    }

    public String checkAmountOfReviews(final String notificationComment){
        myRef.child("Reminder").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
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
        myRef.child("Reminder").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
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
    }

    public void deleteReminder(String notificationNumber){
        myRef.child("Reminder").child(userID).child(notificationNumber).removeValue();
    }

    public void checkNotifications(final String date, final String reminder){
        myRef.child("Reminder").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
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

    public User getUserSettings(DataSnapshot dataSnapshot){

        User user = new User();
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            if(ds.getKey().equals("user")){

                try {
                    user.setUsername(ds.child(userID)
                            .getValue(User.class)
                            .getUsername());

                    user.setEmail(ds.child(userID)
                            .getValue(User.class)
                            .getEmail());

                    user.setProfile_photo(ds.child(userID)
                            .getValue(User.class)
                            .getProfile_photo());

                    user.setFull_name(ds.child(userID)
                            .getValue(User.class)
                            .getFull_name());

                    user.setDob(ds.child(userID)
                            .getValue(User.class)
                            .getDob());

                    user.setMobile_number(ds.child(userID)
                            .getValue(User.class)
                            .getMobile_number());

                    user.setLicence_number(ds.child(userID)
                            .getValue(User.class)
                            .getLicence_number());

                    user.setCompletedRides(ds.child(userID)
                            .getValue(User.class)
                            .getCompletedRides());

                    user.setUserRating(ds.child(userID)
                            .getValue(User.class)
                            .getUserRating());

                    user.setLicence_number(ds.child(userID)
                            .getValue(User.class)
                            .getLicence_number());

                    user.setCar(ds.child(userID)
                            .getValue(User.class)
                            .getCar());

                    user.setRegistration_plate(ds.child(userID)
                            .getValue(User.class)
                            .getRegistration_plate());

                    user.setSeats(ds.child(userID)
                            .getValue(User.class)
                            .getSeats());

                    user.setCarOwner(ds.child(userID)
                            .getValue(User.class)
                            .getCarOwner());

                    user.setGender(ds.child(userID)
                            .getValue(User.class)
                            .getGender());

                    user.setCar_photo(ds.child(userID)
                            .getValue(User.class)
                            .getCar_photo());

                    user.setEducation(ds.child(userID)
                            .getValue(User.class)
                            .getEducation());

                    user.setWork(ds.child(userID)
                            .getValue(User.class)
                            .getWork());

                    user.setBio(ds.child(userID)
                            .getValue(User.class)
                            .getBio());

                } catch (NullPointerException e){
                    Log.d(TAG, "getUserSettings: NullPointerException: " + e.getMessage());
                }

            }
        }
        return user;
    }

    public User getSpeficUserSettings(DataSnapshot dataSnapshot, String user_id){

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

                        user.setProfile_photo(ds.child(user_id)
                                .getValue(User.class)
                                .getProfile_photo());

                        user.setFull_name(ds.child(user_id)
                                .getValue(User.class)
                                .getFull_name());

                        user.setDob(ds.child(user_id)
                                .getValue(User.class)
                                .getDob());

                        user.setMobile_number(ds.child(user_id)
                                .getValue(User.class)
                                .getMobile_number());

                        user.setLicence_number(ds.child(user_id)
                                .getValue(User.class)
                                .getLicence_number());

                        user.setCompletedRides(ds.child(user_id)
                                .getValue(User.class)
                                .getCompletedRides());

                        user.setUserRating(ds.child(user_id)
                                .getValue(User.class)
                                .getUserRating());

                        user.setLicence_number(ds.child(user_id)
                                .getValue(User.class)
                                .getLicence_number());

                        user.setCar(ds.child(user_id)
                                .getValue(User.class)
                                .getCar());

                        user.setRegistration_plate(ds.child(user_id)
                                .getValue(User.class)
                                .getRegistration_plate());

                        user.setSeats(ds.child(user_id)
                                .getValue(User.class)
                                .getSeats());

                        user.setCarOwner(ds.child(user_id)
                                .getValue(User.class)
                                .getCarOwner());

                        user.setGender(ds.child(user_id)
                                .getValue(User.class)
                                .getGender());

                        user.setCar_photo(ds.child(user_id)
                                .getValue(User.class)
                                .getCar_photo());

                        user.setEducation(ds.child(user_id)
                                .getValue(User.class)
                                .getEducation());

                        user.setWork(ds.child(user_id)
                                .getValue(User.class)
                                .getWork());

                        user.setBio(ds.child(user_id)
                                .getValue(User.class)
                                .getBio());
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

}
