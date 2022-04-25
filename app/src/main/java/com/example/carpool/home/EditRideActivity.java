package com.example.carpool.home;

import static com.example.carpool.utils.Utils.formatValue;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carpool.common.ApplicationContext;
import com.example.carpool.common.Common;
import com.example.carpool.R;
import com.example.carpool.models.Info;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.utils.UniversalImageLoader;
import com.example.carpool.models.User;
import com.firebase.geofire.GeoFire;
//import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.github.kmenager.materialanimatedswitch.MaterialAnimatedSwitch;
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

public class EditRideActivity extends AppCompatActivity {

    private static final String TAG = "EditRideActivity";
    private EditRideActivity mContext = EditRideActivity.this;;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //Widgets
    private EditText mDateOfJourneyEditText;
    private EditText mCostEditText;
    private EditText mPickupEditText;
    private EditText mExtraTimeEditText;
    private EditText mLuggageEditText;
    private TextView mPickupLocationEditText;
    private MaterialAnimatedSwitch mSameGender;
    private Button mSnippetOfferRideButton;
    private Boolean sameGenderBoolean = false;
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
    private String destination;
    private String location;
    private String profile_photo;
    private String username;
    private String pickupTime;
    private Long cost;
    private String dateOfJourney;
    private String lengthOfJourneyID;
    private String extraTime;
    private String licencePlate;
    private String car;
    private String luggageID;
    private String destinationId2;
    private String locationId2;
    private String duration;
    private String pickupLocation;
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
        setContentView(R.layout.fragment_edit_ride);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        getActivityData();
        setupFirebase();
        setupFirebaseAuth();

        mUsername = (TextView) findViewById(R.id.usernameTxt);
        mDestinationEditText = (TextView) findViewById(R.id.destination);
        mFromEditText = (TextView) findViewById(R.id.location);
        mCostEditText = (EditText) findViewById(R.id.costEditText);
        mLicencePlateEditText = (TextView) findViewById(R.id.licencePlateEditText);
        mExtraTimeEditText = (EditText) findViewById(R.id.extraTimeEditText);
        mSeatsEditText = (TextView) findViewById(R.id.seatsEditText);
        mCarEditText = (TextView) findViewById(R.id.carEditText);
        mLuggageEditText = (EditText) findViewById(R.id.luggageEditText);
        mDateOfJourneyEditText = (EditText) findViewById(R.id.DateOfJourneyEditText);
        mPickupEditText = (EditText) findViewById(R.id.pickupEditText);
        mPickupLocationEditText = (TextView) findViewById(R.id.pickupLocationEditText);
        mCarPhoto = (CircleImageView) findViewById(R.id.car_image);
        durationTxt = (TextView) findViewById(R.id.durationTxt);
        mDateOfJourneyEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 0);

            DatePickerDialog datePickerDialog = new DatePickerDialog(EditRideActivity.this, date, mCalendar
                    .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });

        mFromEditText.setText(location);
        mDestinationEditText.setText(destination);
        mCostEditText.setText(formatValue(cost));
        mExtraTimeEditText.setText(extraTime);
//        mSeatsEditText.setText(seatsID);
        mDateOfJourneyEditText.setText(dateOfJourney);
        mPickupEditText.setText(pickupTime);
        durationTxt.setText(duration);
        mPickupLocationEditText.setText(pickupLocation);

        mSameGender = (MaterialAnimatedSwitch) findViewById(R.id.genderSwitch);


        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSnippetOfferRideButton = (Button) findViewById(R.id.snippetOfferRideButton);
        mSnippetOfferRideButton.setOnClickListener(v -> {
            //int cost = Integer.parseInt(mCostEditText.getText().toString());
            String dateOfJourney = mDateOfJourneyEditText.getText().toString();
            int extraTime = Integer.parseInt(mExtraTimeEditText.getText().toString());
            int seatsAvailable = seatsID;
            int luggageAllowance = Integer.parseInt(mLuggageEditText.getText().toString());

            String licencePlate = mLicencePlateEditText.getText().toString();
            String pickupLocation = mPickupLocationEditText.getText().toString();
            String pickupTime = mPickupEditText.getText().toString();
            String car = mCarEditText.getText().toString();
            String destination = mDestinationEditText.getText().toString();
            String from = mFromEditText.getText().toString();
            String duration = durationTxt.getText().toString().replaceAll("Duration: " , "");

            if(!isStringNull(pickupTime) && pickupTime != null && cost != 0 &&
                    !isStringNull(dateOfJourney) && dateOfJourney != null
                    && !isIntNull(extraTime)){

                //Creates the ride information and adds it to the database
                mFirebaseMethods.offerRide(userID , username, from, destination, dateOfJourney, seatsAvailable, licencePlate,  currentLongtitude, currentLatitude,
                        sameGenderBoolean, luggageAllowance, car, pickupTime, extraTime, profile_photo, cost, completeRides, userRating, duration, pickupLocation);

                //Adds a notification to firebase
                mFirebaseMethods.checkNotifications(getCurrentDate(), "You have created a ride!");

                //Shows the ride has been created successfully
                /*OfferRideCreatedDialog dialog = new OfferRideCreatedDialog(mContext);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();*/

                finish();

            } else {
                Toast.makeText(EditRideActivity.this, "You must fill in empty fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isStringNull(String string){
        Toast.makeText(this, "All fields must be filled in!", Toast.LENGTH_LONG).show();
        if (string.equals("")){
            return true;
        } else {
            return false;
        }
    }

    private boolean isIntNull(int integer){
        Toast.makeText(this, "All fields must be filled in!", Toast.LENGTH_LONG).show();
        if (integer < 0 || integer == 0){
            return true;
        } else {
            return false;
        }
    }

    private String getCurrentDate(){
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String todayString = formatter.format(todayDate);

        return todayString;
    }


    private void updateLabel() {
        String dateFormat = "dd/MM/yy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.UK);

        mDateOfJourneyEditText.setText(simpleDateFormat.format(mCalendar.getTime()));
    }

    private void getActivityData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
                destination = getIntent().getStringExtra("DESTINATION");
                location = getIntent().getStringExtra("LOCATION");
                pickupTime = getIntent().getStringExtra("PICKUPTIME");
                cost = getIntent().getLongExtra("COST", 0);
                dateOfJourney = getIntent().getStringExtra("DATE");
                lengthOfJourneyID = getIntent().getStringExtra("LENGTH");
                extraTime = getIntent().getStringExtra("EXTRATIME");
               // seatsID = getIntent().getStringExtra("SEATS");
                licencePlate = getIntent().getStringExtra("LICENCE");
                car = getIntent().getStringExtra("CAR");
                luggageID = getIntent().getStringExtra("LUGGAGE");
                locationId2 = getIntent().getStringExtra("FROM2");
                duration = getIntent().getStringExtra("LENGTH");
                pickupLocation = getIntent().getStringExtra("PICKUPLOCATION");
        }
    }

    private void setProfileWidgets(User user, Info info){


        mUserSettings = user;

        UniversalImageLoader.setImage(info.getCarPhoto(), mCarPhoto, null,"");

        username = user.getUsername();
        userRating = info.getUserRating();
        completeRides = info.getCompletedRides();
        profile_photo = info.getProfilePhoto();
        car = info.getCar();
        seatsID = info.getSeats() - 1;
        licencePlate = info.getRegistrationPlate();

        mUsername.setText(username);
        mLicencePlateEditText.setText(licencePlate);
        mDestinationEditText.setText(destination);
        mFromEditText.setText(location);
        mCarEditText.setText(car);
        mSeatsEditText.setText(String.valueOf(seatsID) + " Seats left!");
        durationTxt.setText("Duration: "+ ApplicationContext.getDuration());

        mCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();
        mPickupLocationEditText.setText(Common.getClassName());
    }

    /*----------------------------- SETUP FIREBASE -----------------------------------*/

    private void setupFirebaseAuth(){

        userID = mAuth.getCurrentUser().getUid();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve user information from the database
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
