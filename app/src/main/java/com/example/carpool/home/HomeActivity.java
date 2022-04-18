package com.example.carpool.home;

import static com.example.carpool.utils.Utils.checkNotifications;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.carpool.R;
import com.example.carpool.common.Common;
import com.example.carpool.dialogs.WelcomeDialog;
import com.example.carpool.login.LoginActivity;
import com.example.carpool.map.CustomInfoWindowAdapter;
import com.example.carpool.map.DirectionAsyncTask;
import com.example.carpool.map.PlaceAutocompleteAdapter;
import com.example.carpool.map.PlaceInfo;
import com.example.carpool.mapdirection.FetchURL;
import com.example.carpool.mapdirection.TaskLoadedCallback;
import com.example.carpool.models.Trip;
import com.example.carpool.models.Waypoint;
import com.example.carpool.remote.FrequentRouteClient;
import com.example.carpool.remote.FrequentRouteService;
import com.example.carpool.utils.BottomNavigationViewHelper;
import com.example.carpool.utils.UniversalImageLoader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.AutocompletePrediction;
import com.google.android.libraries.places.compat.GeoDataClient;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.PlaceBufferResponse;
import com.google.android.libraries.places.compat.PlaceDetectionClient;
import com.google.android.libraries.places.compat.Places;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, TaskLoadedCallback {
    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUMBER = 0;

    private final Context mContext = HomeActivity.this;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    private Boolean mLocationPermissionsGranted = false;
    private Place To, From;
    private PlaceInfo placeInfoFrom, placeInfoTo;


    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;

    private GeoDataClient mGeoDataClient;
    private PlaceInfo mPlace;
    private Marker mMarker;
    private double currentLatitude, currentLongtitude;
    private Polyline currentPolyline;
    private MarkerOptions location, destination;
    private LatLng currentLocation, preLocation;

    private String directionsRequestUrl;
    private String userID;

    private AutoCompleteTextView destinationTextview, locationTextView;
    private Button mSearchBtn;
    private Button mDirectionsBtn;
    private Button mSwitchTextBtn;
    private Button mStartTrip;
    private Button mEndTrip;
    private RadioButton findButton, offerButton, shareButton;
    private RadioGroup mRideSelectionRadioGroup;
    private BottomNavigationView bottomNavigationView;
    private ImageView mLocationBtn;
    private FloatingActionButton currentLocationFAB;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private ScheduledExecutorService mExecutor;
    private FrequentRouteService mFrequentService;
    private Trip currentTrip;

    private Boolean carOwner;
    private String typeOfAction;

    @SuppressLint({"ClickableViewAccessibility", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        initImageLoader();
        setupBottomNavigationView();
        if (mAuth.getCurrentUser() != null) {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            findButton = findViewById(R.id.findButton);
            offerButton = findViewById(R.id.offerButton);
            shareButton = findViewById(R.id.shareButton);

            getUserInformation(userID);

            updateFirebaseToken();

            checkNotifications(mRef, userID, mContext, bottomNavigationView);

            FirebaseMessaging.getInstance().subscribeToTopic(userID);
        }

        typeOfAction = "to";

        destinationTextview = findViewById(R.id.destination);
        destinationTextview.setOnFocusChangeListener((view, motionEvent) -> {
            typeOfAction = "to";

        });
        locationTextView = findViewById(R.id.location);

        locationTextView.setOnFocusChangeListener((view, motionEvent) -> {
            typeOfAction = "from";
        });
        mSearchBtn = findViewById(R.id.searchBtn);
        mSwitchTextBtn = findViewById(R.id.switchTextBtn);
        mDirectionsBtn = findViewById(R.id.directionsBtn);
        mStartTrip = findViewById(R.id.btn_start_trip);
        mEndTrip = findViewById(R.id.btn_end_trip);
        mRideSelectionRadioGroup = findViewById(R.id.toggle);
        mLocationBtn = findViewById(R.id.locationImage);

        mLocationBtn.setOnClickListener(v -> getDeviceLocationAndAddMarker());

        mSwitchTextBtn.setOnClickListener(v -> {
            if (destinationTextview.getText().toString().trim().length() > 0 && locationTextView.getText().toString().trim().length() > 0) {
                String tempDestination1 = destinationTextview.getText().toString();
                String tempDestination12 = locationTextView.getText().toString();

                locationTextView.setText(tempDestination1);
                destinationTextview.setText(tempDestination12);

                locationTextView.dismissDropDown();
                destinationTextview.dismissDropDown();
            } else {
                Toast.makeText(mContext, "Please enter location and destination", Toast.LENGTH_SHORT).show();
            }
        });

        mSearchBtn.setOnClickListener(v -> {
            int whichIndex = mRideSelectionRadioGroup.getCheckedRadioButtonId();
            if (whichIndex == R.id.offerButton && destinationTextview.getText().toString().trim().length() > 0 && locationTextView.getText().toString().trim().length() > 0) {
                Intent offerRideActivity = new Intent(mContext, OfferRideFragment.class);
                offerRideActivity.putExtra("LOCATION", locationTextView.getText().toString());
                offerRideActivity.putExtra("DESTINATION", destinationTextview.getText().toString());
                offerRideActivity.putExtra("currentLatitue", currentLatitude);
                offerRideActivity.putExtra("currentLongtitude", currentLongtitude);
                Bundle b = new Bundle();
                b.putParcelable("LatLng", currentLocation);
                offerRideActivity.putExtras(b);
                startActivity(offerRideActivity);
            } else if (whichIndex == R.id.findButton && destinationTextview.getText().toString().trim().length() > 0 && locationTextView.getText().toString().trim().length() > 0) {
                Intent findRideActivity = new Intent(mContext, SearchRideActivity.class);
                findRideActivity.putExtra("LOCATION", locationTextView.getText().toString());
                findRideActivity.putExtra("DESTINATION", destinationTextview.getText().toString());
                findRideActivity.putExtra("currentLatitue", currentLatitude);
                findRideActivity.putExtra("currentLongtitude", currentLongtitude);
                startActivity(findRideActivity);
            } else if (whichIndex == R.id.shareButton) {

            } else {
                Toast.makeText(mContext, "Please enter location and destination", Toast.LENGTH_SHORT).show();
            }
        });

        userLocationFAB();
        mRideSelectionRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            switch (checkedId) {
                case R.id.shareButton: {
                    mStartTrip.setVisibility(View.VISIBLE);
                    currentLocationFAB.setVisibility(View.VISIBLE);
                    mSearchBtn.setVisibility(View.INVISIBLE);
                    mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    });
                    moveCameraNoMarker(currentLocation, 17f, "");
                    break;
                }
                case R.id.findButton: {
                    mSearchBtn.setBackgroundResource(R.drawable.ic_baseline_search_24);
                    mStartTrip.setVisibility(View.GONE);
                    mSearchBtn.setVisibility(View.VISIBLE);
                    if (mFusedLocationProviderClient!= null) {
                        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        });
                        moveCameraNoMarker(currentLocation, DEFAULT_ZOOM, "");
                    }
                    break;
                }
                case R.id.offerButton: {
                    mSearchBtn.setBackgroundResource(R.drawable.ic_baseline_add_circle_24);
                    mStartTrip.setVisibility(View.GONE);
                    mSearchBtn.setVisibility(View.VISIBLE);
                    if (mFusedLocationProviderClient!= null) {
                        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        });
                        moveCameraNoMarker(currentLocation, DEFAULT_ZOOM, "");
                    }
                    break;
                }
            }
        });

        mStartTrip.setOnClickListener(view -> {
            mStartTrip.setVisibility(View.GONE);
            mEndTrip.setVisibility(View.VISIBLE);
            Common.statusTrip = Common.START;
            mFrequentService = FrequentRouteClient.getFrequentRouteClient();
            createTrip(userID);
            moveCameraNoMarker(currentLocation, 17f, "");

            offerButton.setEnabled(false);
            offerButton.setAlpha(.5f);
            offerButton.setClickable(false);

            findButton.setEnabled(false);
            findButton.setAlpha(.5f);
            findButton.setClickable(false);

        });

        mEndTrip.setOnClickListener(view -> {
            LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
            new AlertDialog.Builder(HomeActivity.this)
                    .setView(inflater.inflate(R.layout.dialog_stop_trip, null))
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mMap.clear();
                            mEndTrip.setVisibility(View.GONE);
                            mStartTrip.setVisibility(View.VISIBLE);
                            stopExecutor();
                            moveCameraNoMarker(currentLocation, 17f, "");

                            offerButton.setEnabled(true);
                            offerButton.setAlpha(1f);
                            offerButton.setClickable(true);

                            findButton.setEnabled(true);
                            findButton.setAlpha(1f);
                            findButton.setClickable(true);

                        }
                    })

                    .setNegativeButton("Cancel", null)
                    .show();
        });

        boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
        if (firstrun) {
            //... Display the dialog message here ...
            setupDialog();
            // Save the state
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstrun", false)
                    .apply();
        }

        if (isServicesOk()) {
            getLocationPermission();
        }

        mDirectionsBtn.setOnClickListener(v -> {
            if (location != null && destination != null) {
                Log.d(TAG, "onCreate: " + location + " " + destination);
                directionsRequestUrl = getUrl(location.getPosition(), destination.getPosition());

                Log.d("MinhMX", "moveCamera: " + directionsRequestUrl);
                new FetchURL(HomeActivity.this, mMap).execute(getUrl(location.getPosition(), destination.getPosition()), "driving");
            }
        });

    }


    private void updateFirebaseToken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            tokens.child(userID)
                    .setValue(task.getResult());
        });
    }


    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + "driving";
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        String google_api = getResources().getString(R.string.google_map_api);
        return "https://maps.googleapis.com/maps/api/directions/" +
                output + "?" + parameters + "&key=" + google_api;
    }

    public void setupDialog() {
        WelcomeDialog dialog = new WelcomeDialog(mContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public boolean isServicesOk() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is ok and user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occurred but it can be resolved
            Log.d(TAG, "isServicesOK: an error occurred but it can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "App cannot make map requests current", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    /**
     * sets up map from the view
     */
    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        init();
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(TAG, "onComplete: getting found location!");
                        Location currentLocation = (Location) task.getResult();
                        placeInfoTo = new PlaceInfo();
                        placeInfoTo.setName("My Location");
                        moveCameraNoMarker(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                DEFAULT_ZOOM,
                                "My location");
                        currentLatitude = currentLocation.getLatitude();
                        currentLongtitude = currentLocation.getLongitude();
                    } else {
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(HomeActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void getDeviceLocationAndAddMarker() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {
                @SuppressLint("MissingPermission")
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(TAG, "onComplete: getting found location!");
                        Location result = (Location) task.getResult();
                        currentLocation = new LatLng(result.getLatitude(), result.getLongitude());
                        mMap.clear();
                        To = null;

                        moveCamera(new LatLng(result.getLatitude(), result.getLongitude()),
                                DEFAULT_ZOOM,
                                "My location", null);
                        drawMapMarker(From);
                        currentLatitude = result.getLatitude();
                        currentLongtitude = result.getLongitude();

                        LatLng latLng = new LatLng(currentLatitude, currentLongtitude);
                        this.location = new MarkerOptions()
                                .position(latLng)
                                .title("My location");

                        geoDecoder(result);
                    } else {
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(HomeActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCameraNoMarker(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        hideKeyboard(HomeActivity.this);
    }

    private void moveCamera(LatLng latLng, float zoom, String title, String type) {
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .snippet("");
            if (type.equals("from")) {
                location = markerOptions;

            } else {
                destination = markerOptions;

            }
            mMap.addMarker(markerOptions);
        }

        hideKeyboard(HomeActivity.this);
    }


    private void createMarker(LatLng latLng, PlaceInfo placeInfo) {
        String snippet = "Your Location";
        if (placeInfo != null) {
            snippet = "Address: " + placeInfo.getAddress() + "\n" +
                    "Phone Number: " + placeInfo.getPhoneNumber() + "\n" +
                    "Website: " + placeInfo.getWebsiteUri() + "\n" +
                    "Price Rating: " + placeInfo.getRating() + "\n";

            MarkerOptions marker = new MarkerOptions()
                    .position(latLng)
                    .title(placeInfo.getName())
                    .snippet(snippet);

            mMarker = mMap.addMarker(marker);
        } else {

            MarkerOptions marker = new MarkerOptions()
                    .position(latLng)
                    .title(snippet);

            mMarker = mMap.addMarker(marker);
        }

    }

    private LatLng fromLatlng;

    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo, String typeOfAction) {
        Log.d(TAG, "moveCamera: moving the camera to place: lat:" + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(mContext));
        Log.d(TAG, "moveCamera: " + placeInfo);

        if (placeInfo != null) {
            try {
                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
                        "Phone Number: " + placeInfo.getPhoneNumber() + "\n" +
                        "Website: " + placeInfo.getWebsiteUri() + "\n" +
                        "Price Rating: " + placeInfo.getRating() + "\n";

                Log.d(TAG, "moveCamera: des " + destinationTextview.hasFocus());
                if (!destinationTextview.hasFocus()) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

                    location = new MarkerOptions()
                            .position(latLng)
                            .title(placeInfo.getName())
                            .snippet(snippet);

                    fromLatlng = latLng;

                    Log.d(TAG, "moveCamera: place1: " + location);

                    mMarker = mMap.addMarker(location);

                } else {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

                    destination = new MarkerOptions()
                            .position(latLng)
                            .title(placeInfo.getName())
                            .snippet(snippet);

                    Log.e(TAG, "moveCamera: place2: " + destination + " " + location);

                    mMarker = mMap.addMarker(destination);

                    directionsRequestUrl = getUrl(location.getPosition(), destination.getPosition());

                    //findDirections(fromLatlng.latitude, fromLatlng.longitude, latLng.latitude, latLng.longitude, MODE_DRIVING);
                    Log.d("MinhMX", "moveCamera: " + directionsRequestUrl);
                    new FetchURL(HomeActivity.this, mMap).execute(getUrl(location.getPosition(), destination.getPosition()), "driving");
                }

            } catch (NullPointerException e) {
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
            }
        } else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }
        hideKeyboard(HomeActivity.this);
    }

    private static LatLng AMSTERDAM;
    private static LatLng PARIS;
    private Polyline newPolyline;
    private LatLngBounds latlngBounds;

    public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
        PolylineOptions rectLine = new PolylineOptions().width(5).color(
                Color.RED);

        for (int i = 0; i < directionPoints.size(); i++) {
            rectLine.add(directionPoints.get(i));
        }
        if (newPolyline != null) {
            newPolyline.remove();
        }
        newPolyline = mMap.addPolyline(rectLine);
        latlngBounds = createLatLngBoundsObject(AMSTERDAM, PARIS);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                latlngBounds, 400, 400, 150));

    }
    private LatLngBounds createLatLngBoundsObject(LatLng firstLocation,
                                                  LatLng secondLocation) {
        if (firstLocation != null && secondLocation != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(firstLocation).include(secondLocation);

            return builder.build();
        }
        return null;
    }

    public void findDirections(double fromPositionDoubleLat,
                               double fromPositionDoubleLong, double toPositionDoubleLat,
                               double toPositionDoubleLong, String mode) {
        Map<String, String> map = new HashMap<>();
        map.put(DirectionAsyncTask.USER_CURRENT_LAT,
                String.valueOf(fromPositionDoubleLat));
        map.put(DirectionAsyncTask.USER_CURRENT_LONG,
                String.valueOf(fromPositionDoubleLong));
        map.put(DirectionAsyncTask.DESTINATION_LAT,
                String.valueOf(toPositionDoubleLat));
        map.put(DirectionAsyncTask.DESTINATION_LONG,
                String.valueOf(toPositionDoubleLong));
        map.put(DirectionAsyncTask.DIRECTIONS_MODE, mode);

        DirectionAsyncTask asyncTask = new DirectionAsyncTask(this);
        asyncTask.execute(map);
        // asyncTask.cancel(true);
    }



    private void geoDecoder(Location latLng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.getLatitude(), latLng.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0);
        destinationTextview.setText(address);
        destinationTextview.dismissDropDown();
    }
//    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS_COMPONENTS,Place.Field.ADDRESS,Place.Field.ADDRESS_COMPONENTS,Place.Field.PLUS_CODE,Place.Field.TYPES);

    private void init() {
        Log.d(TAG, "init: initializing");

        mGeoDataClient = Places.getGeoDataClient(this, null);
        PlaceDetectionClient mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(HomeActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        destinationTextview.setOnItemClickListener(mAutoCompleteClickListener);

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, LAT_LNG_BOUNDS, null);

        destinationTextview.setAdapter(mPlaceAutocompleteAdapter);

        destinationTextview.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                    || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                Log.d("MinhMX", "init: " + true);
                //execute our method for searching
                goeLocate(destinationTextview.getText().toString(), "to");
            }

            return false;
        });

        locationTextView.setOnItemClickListener(mAutoCompleteClickListener);

        locationTextView.setAdapter(mPlaceAutocompleteAdapter);

        locationTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute our method for searching
                    goeLocate(locationTextView.getText().toString(), "from");
                }

                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void goeLocate(String place, String type) {
        Log.d(TAG, "goeLocate: goeLocating");

        Geocoder geocoder = new Geocoder(HomeActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(place, 1);
        } catch (IOException e) {
            Log.e(TAG, "goeLocate: IOException: " + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);

            Log.e(TAG, "goeLocate: found a location: " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0), type);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private final AdapterView.OnItemClickListener mAutoCompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemClick: + " + view);
            hideKeyboard(HomeActivity.this);

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            assert item != null;
            final String placeId = item.getPlaceId();

            Places.getGeoDataClient(HomeActivity.this)
                    .getPlaceById(placeId).addOnCompleteListener(place -> {
                Log.d(TAG, "onItemClick: " + place);
                getPlaceDetails(place, typeOfAction);

            }).addOnFailureListener(e -> {
                Log.e(HomeActivity.TAG, "Place can not be found", e);
            });
//            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };


    private void getPlaceDetails(Task<PlaceBufferResponse> places, String typeOfAction) {
        mMap.clear();
        final Place place = places.getResult().get(0);

        try {
            mPlace = new PlaceInfo();
            mPlace.setName(place.getName().toString());
            Log.d(TAG, "onResult: name: " + place.getName());
            mPlace.setAddress(place.getAddress().toString());
            Log.d(TAG, "onResult: address: " + place.getAddress());
            /*
             mPlace.setAttributions(place.getAttributions().toString());
            Log.d(TAG, "onResult: attributions: " + place.getAttributions());
            */
            mPlace.setId(place.getId());
            Log.d(TAG, "onResult: id: " + place.getId());
            mPlace.setLatLng(place.getLatLng());
            Log.d(TAG, "onResult: latLng: " + place.getLatLng());
            mPlace.setRating(place.getRating());
            Log.d(TAG, "onResult: rating: " + place.getRating());
            mPlace.setPhoneNumber(place.getPhoneNumber().toString());
            Log.d(TAG, "onResult: phoneNumber: " + place.getPhoneNumber());
            mPlace.setWebsiteUri(place.getWebsiteUri());
            Log.d(TAG, "onResult: websiteUri: " + place.getWebsiteUri());
            Log.d(TAG, "onResult: place: " + mPlace.toString());

            if (destinationTextview.isFocused()) {
                currentLocation = mPlace.getLatLng();
            }

            if (typeOfAction.equals("from")) {
                From = place;
                placeInfoFrom = mPlace;
                drawMapMarker(From);
            } else {
                To = place;
                placeInfoTo = mPlace;
                drawMapMarker(To);
            }


        } catch (NullPointerException e) {
            Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
        }

        moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace, typeOfAction);

//        places.release();

    }

    private void drawMapMarker(Place From) {

        if (From != null) {
            createMarker(new LatLng(this.From.getViewport().getCenter().latitude,
                    this.From.getViewport().getCenter().longitude), this.placeInfoFrom);
        }

        if (To != null) {
            createMarker(new LatLng(To.getViewport().getCenter().latitude,
                    To.getViewport().getCenter().longitude), placeInfoTo);
        } else if (currentLocation != null) {
            moveCamera(currentLocation, DEFAULT_ZOOM, "My location", null);
        }


    }


    /*private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try {

                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d(TAG, "onResult: name: " + place.getName());
                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: address: " + place.getAddress());
                // mPlace.setAttributions(place.getAttributions().toString());
                //Log.d(TAG, "onResult: attributions: " + place.getAttributions());
                mPlace.setId(place.getId());
                Log.d(TAG, "onResult: id: " + place.getId());
                mPlace.setLatLng(place.getLatLng());
                Log.d(TAG, "onResult: latLng: " + place.getLatLng());
                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating: " + place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: phoneNumber: " + place.getPhoneNumber());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult: websiteUri: " + place.getWebsiteUri());
                Log.d(TAG, "onResult: place: " + mPlace.toString());

                if (destinationTextview.isFocused()) {
                    currentLocation = mPlace.getLatLng();
                }

            } catch (NullPointerException e) {
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);

            places.release();

        }
    };*/

    /*
     * Permission locations
     * */

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: get location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /***
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // userID = currentUser.getUid();
        checkCurrentUser(currentUser);
    }

    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: checking if user if logged in");

        if (user == null) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null) {
            currentPolyline.remove();
            currentPolyline = mMap.addPolyline((PolylineOptions) values[1]);
        }
    }

    public void getUserInformation(String uid) {
        mRef.child("info").child(uid).child("carOwner").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carOwner = dataSnapshot.getValue(Boolean.class);
                if (carOwner == false) {
                    offerButton.setEnabled(false);
                    offerButton.setAlpha(.5f);
                    offerButton.setClickable(false);

                    shareButton.setEnabled(false);
                    shareButton.setAlpha(.5f);
                    shareButton.setClickable(false);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    private void userLocationFAB() {
        currentLocationFAB = findViewById(R.id.myLocationButton);
        currentLocationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMap.getMyLocation() != null) { // Check to ensure coordinates aren't null, probably a better way of doing this...
                    moveCameraNoMarker(new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude()),
                            17f,
                            "My location");
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void createTrip(String userID) {
        Trip trip = new Trip();
        trip.setAccount_owner(userID);

        SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd");
        String startDateStr = tf.format(new Date());
        trip.setDate(startDateStr);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) day = 7;
        else day -= 1;
        trip.setWeekday(day);

        mFrequentService.addTrip(trip)
                .enqueue(new Callback<Trip>() {
                    @Override
                    public void onResponse(Call<Trip> call, Response<Trip> response) {
                        currentTrip = response.body();
                        updatedRoute();
                    }

                    @Override
                    public void onFailure(Call<Trip> call, Throwable t) {
                        Log.e("Fail add trip", t.getLocalizedMessage());
                    }
                });
    }

    private void updatedRoute() {
        Runnable helloRunnable = new Runnable() {
            public void run() {
                mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    if (preLocation == null) {
                        preLocation = currentLocation;
                        updateWaypoint();
                    } else if (preLocation.latitude != currentLocation.latitude || preLocation.longitude != currentLocation.longitude) {
                        updateWaypoint();
                        Polyline line = mMap.addPolyline(new PolylineOptions()
                                .add(preLocation, currentLocation)
                                .width(10)
                                .color(Color.RED));
                        preLocation = currentLocation;
                    }
                });
            }
        };

        mExecutor = Executors.newScheduledThreadPool(1);
        mExecutor.scheduleAtFixedRate(helloRunnable, 0, 3, TimeUnit.SECONDS);

    }

    private void stopExecutor() {
        if (mExecutor != null) {
            mExecutor.shutdown();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateWaypoint() {
        Waypoint waypoint = new Waypoint();
        waypoint.setLatitude(mMap.getMyLocation().getLatitude());
        waypoint.setLongitude(mMap.getMyLocation().getLongitude());

        if (currentTrip != null) {
            waypoint.setOn_trip(currentTrip.getTrip_id());
        }

        SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        String startDateStr = tf.format(new Date());
        waypoint.setTime(startDateStr);

        mFrequentService.addWaypoint(waypoint)
                .enqueue(new Callback<Waypoint>() {
                    @Override
                    public void onResponse(Call<Waypoint> call, Response<Waypoint> response) {
                        Log.e(TAG, response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<Waypoint> call, Throwable t) {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopExecutor();
    }
}
