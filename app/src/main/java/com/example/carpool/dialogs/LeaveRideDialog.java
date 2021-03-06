package com.example.carpool.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.carpool.booked.BookedActivity;
import com.example.carpool.R;
import com.example.carpool.utils.FirebaseMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LeaveRideDialog extends Dialog implements
        View.OnClickListener  {

    private static final String TAG = "LeaveRideDialog";
    public Context context;
    public Dialog d;
    private TextView cancelDialog;
    private Button confirmDialog;
    private final String currentUserID;
    private final String rideID;

    //Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;

    private int seatsAvailable = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_leave_ride);
        cancelDialog = findViewById(R.id.dialogCancel);
        confirmDialog = (Button) findViewById(R.id.pay_and_book);
        cancelDialog.setOnClickListener(this);
        confirmDialog.setOnClickListener(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        getsSeatsRemaining();
    }

    public LeaveRideDialog(Context context, String currentUserID, String rideID) {
        super(context);
        this.context = context;
        this.currentUserID = currentUserID;
        this.rideID = rideID;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_and_book:
                leaveRide();
                dismiss();
                updateSeatsRemaining();
                ((Activity) context).finish();
                Intent intent = new Intent(context, BookedActivity.class);
                context.startActivity(intent);
                break;
            case R.id.dialogCancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void leaveRide(){
        mRef.child("request_ride").child(rideID).child(currentUserID).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Toast.makeText(context, "Left ride successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getsSeatsRemaining(){
        mRef.child("available_ride").child(rideID).child("seatsAvailable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                seatsAvailable = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateSeatsRemaining(){
        mRef.child("available_ride").child(rideID).child("seatsAvailable").setValue(seatsAvailable + 1);
    }

}
