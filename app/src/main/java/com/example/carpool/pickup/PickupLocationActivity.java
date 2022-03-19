package com.example.carpool.pickup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carpool.common.Common;
import com.example.carpool.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("FieldCanBeLocal")
public class PickupLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "PickupLocationActivity";
    private final Context mContext = this;

    //Google map variables
    private GoogleMap mMap;
    private LatLng currentLocation;
    private LatLngBounds bounds;
    private LatLngBounds.Builder builder;

    private Geocoder geocoder;
    private List<Address> addresses;

    private String streetName;


    //View viarables
    private Button mConfirm;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_location);

        getActivityData();
        setupWidgets();
        initMap();

        mConfirm.setOnClickListener(v -> {
            try {
                getAddress(currentLocation);
                Common.setClassName(streetName);
                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void setupWidgets() {
        mConfirm = (Button) findViewById(R.id.confirmLocationBtn);
        helpDialog();

    }

    private void getActivityData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Bundle b = getIntent().getExtras();
            currentLocation = b.getParcelable("LatLng");
        }
    }

    private void helpDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_pickup_help);
        dialog.setCanceledOnTouchOutside(true);

        View masterView = dialog.findViewById(R.id.coach_mark_master_view);
        masterView.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }


    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.pickupLocationMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(PickupLocationActivity.this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        int height = 120;
        int width = 120;

        Log.d("MinhMX", "onMapReady: "  + currentLocation);
        if (currentLocation != null) {
            @SuppressLint("UseCompatLoadingForDrawables")
            BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.marker_pickup_location);

            Bitmap b = bitmapDraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

            mMap.addMarker(new MarkerOptions().position(currentLocation)
                    .title("Pickup location")
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));

            builder = new LatLngBounds.Builder();
            builder.include(currentLocation);
            bounds = builder.build();


            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(@NonNull Marker marker) {

                }

                @Override
                public void onMarkerDrag(@NonNull Marker marker) {
                }

                @Override
                public void onMarkerDragEnd(@NonNull Marker marker) {
                    currentLocation = marker.getPosition();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                }
            });
        }
    }

    private void getAddress(LatLng latLng) throws IOException {
        geocoder = new Geocoder(mContext, Locale.getDefault());

        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        streetName = addresses.get(0).getAddressLine(0);

        Log.i(TAG, "getAddress: " + streetName);
    }
}
