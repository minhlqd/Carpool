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

public class Utils {
    public static final String KEY_LOCATION = "location";

    public static final String KEY_DESTINATION = "destination";

    public static final String KEY_SAME_GENDER = "same_gender";

    public static void checkNotifications(DatabaseReference reference, String id, Context context, BottomNavigationView bottomNavigationView){
        reference.child("reminder").child(id).addValueEventListener(new ValueEventListener() {
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

        reference.child("info").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("MinhMX", "onDataChange: " + snapshot.getValue());
                /*Info info = snapshot.getValue(Info.class);
                if (info.getCarOwner()) {
                    reference.child("request_ride").child(id).addValueEventListener(new ValueEventListener() {
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
                }*/
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
}
