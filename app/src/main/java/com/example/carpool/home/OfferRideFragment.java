package com.example.carpool.home;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carpool.common.ApplicationContext;
import com.example.carpool.common.Common;
import com.example.carpool.models.Info;
import com.example.carpool.pickup.PickupLocationActivity;
import com.example.carpool.R;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.utils.UniversalImageLoader;
import com.example.carpool.dialogs.OfferRideCreatedDialog;
import com.example.carpool.models.User;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class OfferRideFragment<MaterialAnimatedSwitch> extends AppCompatActivity {

    private static final String TAG = "OfferRideFragment";
    private OfferRideFragment mContext;
    private ApplicationContext applicationContext;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseMethods mFirebaseMethods;
    private String driverID;

    //Widgets
    private EditText mDateOfJourneyEditText;
    private EditText mCostEditText;
    private EditText mPickupEditText;
    private EditText mExtraTimeEditText;
    private EditText mLuggageEditText;
    private EditText mPickupLocationEditText;
    private MaterialAnimatedSwitch mSameGender;
    private Button mSnippetOfferRideButton;
    private final Boolean sameGenderBoolean = false;
    private Calendar mCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private CircleImageView mCarPhoto;
    private TextView mLicencePlateEditText;
    private TextView mCarEditText;
    private TextView mSeatsEditText;
    private TextView mDestinationEditText;
    private TextView mFromEditText;
    private TextView mUsername;
    private TextView durationTxt;


    //vars
    private User mUserSettings;
    private String destinationId;
    private String locationId;
    private String profile_photo;
    private String username;
    private String pickupTimeID;
    private String costID;
    private String dateOfJourneyID;
    private String lengthOfJourneyID;
    private String extraTimeID;
    private String licencePlateID;
    private String carID;
    private String luggageID;
    private String destinationId2;
    private String locationId2;
    private float userRating;
    private int seatsID;
    private int completeRides;
    private double currentLatitude, currentLongtitude;
    private LatLng currentLocation;


    //GeoFire
    private DatabaseReference mRef;
    private GeoFire mGeoFire;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_offer_ride);
        Log.d(TAG, "onCreate: starting.");
        mContext = OfferRideFragment.this;

        //Disables focused keyboard on view startup
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getActivityData();
        setupFirebase();
        setupFirebaseAuth();

        mUsername = findViewById(R.id.usernameTxt);
        mDestinationEditText = findViewById(R.id.destinationEditText);
        mFromEditText = findViewById(R.id.fromEditText);
        mCostEditText = findViewById(R.id.costEditText);
        mLicencePlateEditText = findViewById(R.id.licencePlateEditText);
        mExtraTimeEditText = findViewById(R.id.extraTimeEditText);
        mSeatsEditText = findViewById(R.id.seatsEditText);
        mCarEditText = findViewById(R.id.carEditText);
        mLuggageEditText = findViewById(R.id.luggageEditText);
        mDateOfJourneyEditText = findViewById(R.id.DateOfJourneyEditText);
        mPickupLocationEditText = findViewById(R.id.pickupLocationEditText);
        mCarPhoto = findViewById(R.id.car_image);
        durationTxt = findViewById(R.id.durationTxt);
        mDateOfJourneyEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 0);
            DatePickerDialog datePickerDialog = new DatePickerDialog(OfferRideFragment.this, date, mCalendar
                    .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });

        mSameGender = (MaterialAnimatedSwitch) findViewById(R.id.genderSwitch);
//        mSameGender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    sameGenderBoolean = true;
//                } else {
//                    sameGenderBoolean = false;
//                }
//            }
//        });

       mPickupEditText = findViewById(R.id.pickupEditText);
       mPickupEditText.setOnClickListener(v -> {
           Calendar mCurrentTime = Calendar.getInstance();
           int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
           int minute = mCurrentTime.get(Calendar.MINUTE);
           TimePickerDialog mTimePicker;
           mTimePicker = new TimePickerDialog(OfferRideFragment.this, new TimePickerDialog.OnTimeSetListener() {
               @SuppressLint("SetTextI18n")
               @Override
               public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                   mPickupEditText.setText( selectedHour + ":" + selectedMinute);
               }
           }, hour, minute, true);//Yes 24 hour time
           mTimePicker.setTitle("Select Time");
           mTimePicker.show();
       });

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Log.d(TAG, "onClick: navigating back to HomeActivity");
            finish();
        });

        mPickupLocationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PickupLocationActivity.class);

                Bundle b = new Bundle();

                b.putParcelable("LatLng", currentLocation);

                intent.putExtras(b);

                startActivityForResult(intent, 1);
            }
        });


        mSnippetOfferRideButton = findViewById(R.id.snippetOfferRideButton);
        mSnippetOfferRideButton.setOnClickListener(v -> {
            int cost = Integer.parseInt(mCostEditText.getText().toString());
            String dateOfJourney = mDateOfJourneyEditText.getText().toString();
            int extraTime = Integer.parseInt(mExtraTimeEditText.getText().toString());
            int seatsAvailable = seatsID;
            int luggageAllowance = Integer.parseInt(mLuggageEditText.getText().toString());

            String licencePlate = mLicencePlateEditText.getText().toString();
            String pickupLocation = mPickupLocationEditText.getText().toString();
            String pickupTime = mPickupEditText.getText().toString();
            String car = mCarEditText.getText().toString();
            String destination = mDestinationEditText.getText().toString();
            String location = mFromEditText.getText().toString();
            String duration = durationTxt.getText().toString().replaceAll("Duration: " , "");

            if(!isStringNull(pickupTime) && !isIntNull(cost) && cost != 0 && !isStringNull(dateOfJourney) && !isIntNull(extraTime)){
                mFirebaseMethods.offerRide(driverID, username, location, destination, dateOfJourney, seatsAvailable, licencePlate,  currentLongtitude, currentLatitude,
                        sameGenderBoolean, luggageAllowance, car, pickupTime, extraTime, profile_photo, cost, completeRides, userRating, duration, pickupLocation);

                mFirebaseMethods.checkNotifications(getCurrentDate(), "You have created a ride!");

                mFirebaseMethods.addPoints(driverID, 100);

                OfferRideCreatedDialog dialog = new OfferRideCreatedDialog(mContext);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                /*finish();*/

            } else {
                Toast.makeText(OfferRideFragment.this, "You must fill in empty fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isStringNull(String string){
        Toast.makeText(this, "All fields must be filled in!", Toast.LENGTH_LONG).show();
        return string.equals("");
    }

    private boolean isIntNull(int integer){
        Toast.makeText(this, "All fields must be filled in!", Toast.LENGTH_LONG).show();
        return integer < 0 || integer == 0;
    }

    private String getCurrentDate(){
        Date todayDate = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        return formatter.format(todayDate);
    }


    private void updateLabel() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.UK);

        mDateOfJourneyEditText.setText(simpleDateFormat.format(mCalendar.getTime()));
    }

    private void getActivityData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
          //  if (extras.containsKey("DESTINATION")){
                //from Home view passed to this class
                locationId = getIntent().getStringExtra("LOCATION");
                destinationId = getIntent().getStringExtra("DESTINATION");
                Bundle b = getIntent().getExtras();
                currentLocation = b.getParcelable("LatLng");
        }
    }

    @SuppressLint("SetTextI18n")
    private void setProfileWidgets(User user, Info info){

        mUserSettings = user;

        UniversalImageLoader.setImage(info.getProfilePhoto(), mCarPhoto, null,"");

        username = user.getUsername();
        userRating = info.getUserRating();
        completeRides = info.getCompletedRides();
        profile_photo = info.getProfilePhoto();
        carID = info.getCar();
        seatsID = info.getSeats() - 1;
        licencePlateID = info.getRegistrationPlate();

        mUsername.setText(username);
        mLicencePlateEditText.setText(licencePlateID);
        mDestinationEditText.setText(destinationId);
        mFromEditText.setText(locationId);
        mCarEditText.setText(carID);
        mSeatsEditText.setText(seatsID + " " +getString(R.string.seats_left));
        durationTxt.setText("Duration: "+ ApplicationContext.getDuration());

        mCalendar = Calendar.getInstance();
        date = (view, year, month, dayOfMonth) -> {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };
    }


    @Override
    public void onResume() {
        super.onResume();
        mPickupLocationEditText.setText(Common.getClassName());
    }

    private void setupFirebaseAuth(){

        driverID = mAuth.getCurrentUser().getUid();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                setProfileWidgets(mFirebaseMethods.getUser(dataSnapshot), mFirebaseMethods.getInfo(dataSnapshot));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(this);

//        mGeoFire = new GeoFire(mRef);
//
//        mGeoFire.setLocation("availableRides", new GeoLocation(37.7853889, -122.4056973), new GeoFire.CompletionListener() {
//            @Override
//            public void onComplete(String key, DatabaseError error) {
//                if (error != null) {
//                    System.err.println("There was an error saving the location to GeoFire: " + error);
//                } else {
//                    System.out.println("Location saved on server successfully!");
//                }
//            }
//        });

    }
}
