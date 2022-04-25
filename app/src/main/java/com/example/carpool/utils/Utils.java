package com.example.carpool.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.carpool.adapter.BookingAdapter;
import com.example.carpool.booked.BookedActivity;
import com.example.carpool.models.BookingResults;
import com.example.carpool.models.Info;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Stack;

public class Utils {
    public static final String KEY_LOCATION = "location";

    public static final String KEY_DESTINATION = "destination";

    public static final String KEY_SAME_GENDER = "same_gender";

    public static final String KEY_DISTANCE = "distance";

    public static final String KEY_LAT = "lat";

    public static final String KEY_LNG = "lng";

    public static final String KEY_PICKUP_LOCATION = "pickup_location";

    public static final String KEY_PICKUP_TIME = "pickup_time";

    public static final String LAT_LNG = "LatLng";

    public static final String REMINDER = "reminder";

    public static final String INFO = "info";

    public static final String REQUEST_RIDE = "request_ride";

    public static final String AVAILABLE_RIDE = "available_ride";

    public static final String USER = "user";

    public static final String TOKENS = "Tokens";

    public static void checkNotifications(DatabaseReference reference, String id, Context context, BottomNavigationView bottomNavigationView){
        reference.child(REMINDER).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int reminderLength = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        reminderLength++;
                    }
                }
                setupBadge(reminderLength, context, bottomNavigationView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child(INFO).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Info info = snapshot.getValue(Info.class);
                if (info != null) {
                    if (info.getCarOwner()) {
                        reference.child(REQUEST_RIDE).child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int countRequest = 0;
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                        BookingResults r = dataSnapshot1.getValue(BookingResults.class);
                                        if (!r.getAccepted()) {
                                            countRequest++;
                                        }
                                    }
                                }
                                setupBadgeRequest(countRequest, context, bottomNavigationView);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private static void setupBadge(int reminderLength, Context context, BottomNavigationView bottomNavigationView){
        if (reminderLength > 0){
            BottomNavigationViewHelper.addBadge(context, bottomNavigationView, reminderLength);
        }
    }

    private static void setupBadgeRequest(int request, Context context, BottomNavigationView bottomNavigationView){
        if (request > 0){
            BottomNavigationViewHelper.addBadgeRequest(context, bottomNavigationView, request);
        }
    }

    public static String formatValue(long value) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);
        format.setCurrency(Currency.getInstance("VND"));
        return format.format(value);
    }
}
