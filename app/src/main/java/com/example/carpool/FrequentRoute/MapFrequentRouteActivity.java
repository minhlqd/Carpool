package com.example.carpool.FrequentRoute;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carpool.R;
import com.example.carpool.mapdirection.FetchURL;
import com.example.carpool.mapdirection.TaskLoadedCallback;
import com.example.carpool.models.FrequentRouteResults;
import com.example.carpool.remote.FrequentRouteClient;
import com.example.carpool.remote.FrequentRouteService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFrequentRouteActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private GoogleMap mMap;
    private FrequentRouteResults route;
    private Marker markerStart, markerEnd;

    private LinearLayout layout_radiobtn;
    private Button btn_share, btn_cancle_share;
    private RadioGroup radioGroup;

    private FrequentRouteService mService;

    private String type_share = "Participant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_frequent_route);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        init();

    }

    private void init() {

        layout_radiobtn = (LinearLayout) findViewById(R.id.layout_radiobtn);
        btn_share = (Button) findViewById(R.id.btn_share);
        btn_cancle_share = (Button) findViewById(R.id.btn_cancle_share);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mService = FrequentRouteClient.getFrequentRouteClient();

        if (getIntent() != null) {
            route = (FrequentRouteResults) getIntent().getSerializableExtra("data");
        }

        if (route.getIs_shared() != 0) {
            btn_cancle_share.setVisibility(View.VISIBLE);
            btn_share.setVisibility(View.GONE);
            layout_radiobtn.setVisibility(View.GONE);
        } else {
            btn_cancle_share.setVisibility(View.GONE);
            btn_share.setVisibility(View.VISIBLE);
            layout_radiobtn.setVisibility(View.VISIBLE);
        }

        setOnclick();
    }

    private void setOnclick() {
        btn_share.setOnClickListener(view -> {
            mService.updateIsShared(1, type_share, route.getId(), route.getUser_id())
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(MapFrequentRouteActivity.this, "Share successful!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(MapFrequentRouteActivity.this, "Share failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        btn_cancle_share.setOnClickListener(view -> {
            mService.updateIsShared(0, "none", route.getId(), route.getUser_id())
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(MapFrequentRouteActivity.this, "Cancle share successful!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(MapFrequentRouteActivity.this, "Cancle share failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });


//    private void setOnclick() {
//        btn_share.setOnClickListener(view -> {
//            mService.updateIsShared(1,type_share,route.getId(),route.getUser_id())
//                    .enqueue(new Callback<ExceptionResult>() {
//                        @Override
//                        public void onResponse(Call<ExceptionResult> call, Response<ExceptionResult> response) {
//                            if(response.isSuccessful()) {
//                                Toast.makeText(MapFrequentRouteActivity.this,"Share successful!",Toast.LENGTH_SHORT).show();
//                                finish();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ExceptionResult> call, Throwable t) {
//                            Toast.makeText(MapFrequentRouteActivity.this,"Share failed!",Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        });
//        btn_cancle_share.setOnClickListener(view -> {
//            mService.updateIsShared(0,"none",route.getId(),route.getUser_id())
//                    .enqueue(new Callback<ExceptionResult>() {
//                        @Override
//                        public void onResponse(Call<ExceptionResult> call, Response<ExceptionResult> response) {
//                            if(response.isSuccessful()) {
//                                Toast.makeText(MapFrequentRouteActivity.this,"Cancle share successful!",Toast.LENGTH_SHORT).show();
//                                finish();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ExceptionResult> call, Throwable t) {
//                            Toast.makeText(MapFrequentRouteActivity.this,"Cancle share failed!",Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) findViewById(i);
                type_share = rb.getText().toString();
            }
        });
    }

    private void zoomMap(LatLng startPoint, LatLng endPoint) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(startPoint);
        builder.include(endPoint);
        LatLngBounds bounds = builder.build();

        int padding = 100;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    private void createMarkerStart(LatLng startPoint) {
        String snippet = route.getAddress_start() + "\n" +
                "Starting time: " + route.getTime_start() + "\n";

        MarkerOptions marker = new MarkerOptions()
                .position(startPoint)
                .title("Origin")
                .snippet(snippet);

        markerStart = mMap.addMarker(marker);
    }

    private void createMarkerEnd(LatLng endPoint) {
        String snippet = route.getAddress_destination() + "\n" +
                "Arrival time: " + route.getTime_destination() + "\n";

        MarkerOptions marker = new MarkerOptions()
                .position(endPoint)
                .title("Destination")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .snippet(snippet);

        markerEnd = mMap.addMarker(marker);
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String google_api = getResources().getString(R.string.google_maps_api_key);
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + google_api;
        return url;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (route != null) {
            LatLng startPoint = new LatLng(route.getLat_start(), route.getLng_start());
            LatLng endPoint = new LatLng(route.getLat_end(), route.getLng_end());
            createMarkerStart(startPoint);
            createMarkerEnd(endPoint);
            new FetchURL(MapFrequentRouteActivity.this, mMap).execute(getUrl(markerStart.getPosition(), markerEnd.getPosition(), "driving"), "driving");

            mMap.setOnMapLoadedCallback(() -> {
                zoomMap(startPoint, endPoint);
            });
        }

    }

    @Override
    public void onTaskDone(Object... values) {

    }
}
