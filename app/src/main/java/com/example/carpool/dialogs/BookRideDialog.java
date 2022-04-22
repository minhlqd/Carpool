package com.example.carpool.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.carpool.account.ProfileActivity;
import com.example.carpool.R;
import com.example.carpool.utils.SectionsStatePageAdapter;
import com.example.carpool.payment.PaymentActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BookRideDialog extends Dialog implements View.OnClickListener  {

    private static final String TAG = "MinhMX";
    public Context context;
    public Dialog dialog;

    // variables
    private TextView mUsername;
    private TextView mRidesCompleted;
    private TextView mCost;
    private TextView mDepartureTime;
    private TextView mExtraTime;
    private TextView mFromStreet;
    private TextView mFromPostcode;
    private TextView mFromCity;
    private TextView mToStreet;
    private TextView mToPostcode;
    private TextView mToCity;
    private TextView mCancelDialogBtn;
    private TextView mDurationTextview;
    private TextView mPickupLocation;
    private RatingBar mRatingBar;
    private Button mPayAndBook;

    private SectionsStatePageAdapter pageAdapter;

    private final String rides;
    private final String seats;
    private final String from;
    private final String to;
    private final String date;
    private final String cost;
    private final String username;
    private final String pickupTime;
    private final String extraTime;
    private final String rideID;
    private final String duration;
    private final String driverID;
    private final String profile_photo;
    private final String completedRides;
    private final String pickupLocation;
    private final String dateOnly;
    private final String licencePlate;
    private final Float rating;
    private FloatingActionButton mViewProfileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ride_confirm);

        setupWidgets();

        mCancelDialogBtn.setOnClickListener(this);
        mPayAndBook.setOnClickListener(this);
        mViewProfileBtn.setOnClickListener(this);
    }

    public BookRideDialog(Context context, String rideID, String username, String licencePlate,
                          String rides, String seats, String from, String to, String date, String dateOnly,
                          String cost, Float rating, String pickupTime, String extraTime, String duration, String driverID,
                          String profile_photo, String completedRides, String pickupLocation) {
        super(context);
        this.context = context;
        this.rideID = rideID;
        this.username = username;
        this.rides = rides;
        this.seats = seats;
        this.from = from;
        this.to = to;
        this.date = date;
        this.dateOnly = dateOnly;
        this.cost = cost;
        this.rating = rating;
        this.extraTime = extraTime;
        this.pickupTime = pickupTime;
        this.duration = duration;
        this.driverID = driverID;
        this.profile_photo = profile_photo;
        this.completedRides = completedRides;
        this.pickupLocation = pickupLocation;
        this.licencePlate = licencePlate;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_and_book:
                sendDataToPayment();
                break;
            case R.id.dialogCancel:
                dismiss();
                break;
            case R.id.viewProfileBtn:
                showIntentProfile();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void sendDataToPayment(){
        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtra("userID", driverID);
        Log.d(TAG, "showDialog: " + driverID);
        intent.putExtra("currentLocation", from);
        intent.putExtra("destination", to);
        intent.putExtra("dateOfJourney", date);
        intent.putExtra("dateOnly", dateOnly);
        intent.putExtra("rideID", rideID);
        intent.putExtra("profile_photo", profile_photo);
        intent.putExtra("pickupLocation", pickupLocation);
        intent.putExtra("pickupTime", pickupTime);
        intent.putExtra("licencePlate", licencePlate);
        intent.putExtra("cost", cost);
        intent.putExtra("username", username);
        context.startActivity(intent);
    }

    private void showIntentProfile(){
        //Confirmation to delete the ride dialog
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("userID", driverID);
        context.startActivity(intent);
    }


    @SuppressLint("SetTextI18n")
    private void setupWidgets(){
        //Setup widgets
        mUsername = findViewById(R.id.usernameTxt);
        mRidesCompleted = findViewById(R.id.completedRidesTxt);
        mCost = findViewById(R.id.costTxt);
        mDepartureTime = findViewById(R.id.timeTxt);
        mExtraTime = findViewById(R.id.extraTimeTxt);
        mFromStreet = findViewById(R.id.streetNameTxt);
        mToStreet = findViewById(R.id.streetName2Txt);
        mPickupLocation = findViewById(R.id.pickupLocationConfirm);

        mRatingBar = findViewById(R.id.ratingBar);

        mPayAndBook = findViewById(R.id.pay_and_book);
        mCancelDialogBtn = findViewById(R.id.dialogCancel);
        mDurationTextview = findViewById(R.id.durationConfirm);
        mViewProfileBtn = findViewById(R.id.viewProfileBtn);

        mCost.setText(cost);
        mUsername.setText(username);
        mRatingBar.setRating(rating);
        mDepartureTime.setText(pickupTime);
        mExtraTime.setText(extraTime);
        mFromStreet.setText(from);
        mToStreet.setText(to);
        mDurationTextview.setText("Duration: " + duration);
        mRidesCompleted.setText(completedRides + " Rides");
        mPickupLocation.setText("Pickup: " + pickupLocation);
    }

}
