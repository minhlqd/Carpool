package com.example.carpool.dialogs;

import static com.example.carpool.utils.Utils.formatValue;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.carpool.account.ProfileActivity;
import com.example.carpool.home.EditRideActivity;
import com.example.carpool.R;
import com.example.carpool.utils.UniversalImageLoader;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ViewRideCreatedDialog extends Dialog implements
        View.OnClickListener  {

    private static final String TAG = "ViewRideCreatedDialog";
    public Context mContext;
    public Dialog d;

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
    private TextView durationTextView;
    private TextView mPickupLocation;
    private RatingBar mRatingBar;
    private ImageView photoView;
    private Button mEditRideBtn;
    private FloatingActionButton mDeleteRideBtn;
    private FloatingActionButton mPaticipantsRideBtn;
    private FloatingActionButton mViewProfileBtn;
    private final String userID;
    private final String rides;
    private final String seats;
    private final String location;
    private final String destination;
    private final String date;
    private final long cost;
    private final String username;
    private final String dateOnly;
    private final String extraTime;
    private final String rideID;
    private final String duration;
    private final String ridesCompleted;
    private final String pickupLocation;
    private final Float rating;
    private final String photo;
    private final String driverID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ride_details);

        setupWidgets();

        mPaticipantsRideBtn.setOnClickListener(this);
        mCancelDialogBtn.setOnClickListener(this);
        mEditRideBtn.setOnClickListener(this);
        mDeleteRideBtn.setOnClickListener(this);
        mViewProfileBtn.setOnClickListener(this);
    }

    public ViewRideCreatedDialog(Context context, String rideID, String username, String rides, String seats, String location, String destination, String date, long cost, Float rating, String dateOnly, String extraTime,
                                 String duration, String ridesCompleted, String pickupLocation, String userID, String photo, String driverID) {
        super(context);
        this.mContext = context;
        this.rideID = rideID;
        this.username = username;
        this.rides = rides;
        this.seats = seats;
        this.location = location;
        this.destination = destination;
        this.date = date;
        this.cost = cost;
        this.rating = rating;
        this.extraTime = extraTime;
        this.dateOnly = dateOnly;
        this.duration = duration;
        this.ridesCompleted = ridesCompleted;
        this.pickupLocation = pickupLocation;
        this.userID = userID;
        this.photo = photo;
        this.driverID = driverID;
        Log.d(TAG, "ViewRideCreatedDialog: " + userID);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_and_book:
                Log.d(TAG, "onClick: " + pickupLocation);
                Intent intent1 = new Intent(mContext, EditRideActivity.class);
                intent1.putExtra("COST", cost);
                intent1.putExtra("EXTRATIME", extraTime);
                intent1.putExtra("DATE", date);
                intent1.putExtra("SEATS", seats);
                intent1.putExtra("DESTINATION", destination);
                intent1.putExtra("LOCATION", location);
                intent1.putExtra("PICKUPTIME", dateOnly);
                intent1.putExtra("LENGTH", duration);
                intent1.putExtra("PICKUPLOCATION", pickupLocation);
                mContext.startActivity(intent1);
                break;
            case R.id.dialogCancel:
                dismiss();
                break;
            case R.id.deleteRideBtn:
                showDialog();
                dismiss();
                break;
            case R.id.paticipantsRideBtn:
                showDialogParticipants();
                break;
            case R.id.viewProfileBtn:
                showIntentProfile();
                break;
            default:
                break;
        }
    }

    private void showDialog(){
        //Confirmation to delete the ride dialog
        DeleteConfirmationDialog dialog = new DeleteConfirmationDialog(mContext, rideID, driverID);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    private void showDialogParticipants(){
        //Confirmation to delete the ride dialog
        ParticipantsDialog dialog = new ParticipantsDialog(mContext, userID, rideID);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void showIntentProfile(){
        //Confirmation to delete the ride dialog
        Intent intent = new Intent(mContext, ProfileActivity.class);
        mContext.startActivity(intent);
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
        durationTextView = findViewById(R.id.durationNew);
        mPickupLocation = findViewById(R.id.pickupLocationNew);

        mRatingBar = findViewById(R.id.ratingBar);


        mEditRideBtn = findViewById(R.id.pay_and_book);
        mDeleteRideBtn = findViewById(R.id.deleteRideBtn);
        mPaticipantsRideBtn = findViewById(R.id.paticipantsRideBtn);
        mViewProfileBtn = findViewById(R.id.viewProfileBtn);
        mCancelDialogBtn = findViewById(R.id.dialogCancel);

        photoView = findViewById(R.id.profile_logo_1);

        mCost.setText(formatValue(cost));
        mUsername.setText(username);
        mRatingBar.setRating(rating);
        mDepartureTime.setText(dateOnly);
        mExtraTime.setText(extraTime);
        mFromStreet.setText(location);
        mToStreet.setText(destination);
        durationTextView.setText("Duration: " + duration);
        mRidesCompleted.setText(ridesCompleted + " Rides");
        mPickupLocation.setText("Pickup: " + pickupLocation);
        UniversalImageLoader.setImage(photo, photoView, null,"");
    }

}
