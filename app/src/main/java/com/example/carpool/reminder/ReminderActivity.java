package com.example.carpool.reminder;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carpool.adapter.ReminderAdapter;
import com.example.carpool.R;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.models.Reminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReminderActivity extends AppCompatActivity {
    private static final String TAG = "ReminderActivity";


    private Context mContext = ReminderActivity.this;
    private Activity mActivity = this;

    //Recycler view setup
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mRecycleAdapter;
    private ReminderAdapter mReminderAdapter;
    private ArrayList<Reminder> reminders;

    //widgets
    private TextView mUsername;
    private ImageView mBack;

    //Firebase variables
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseMethods mFirebaseMethods;

    private String userID;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        Log.d(TAG, "onCreate: starting.");

        setupFirebase();
        setupWidgets();
        setupRecyclerView();

        mRef = FirebaseDatabase.getInstance().getReference().child("reminder").child(userID);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Log.i(TAG, "onDataChange: " + dataSnapshot1);
                            Reminder reminder = dataSnapshot1.getValue(Reminder.class);
                            reminders.add(reminder);
                    }
                }
                mReminderAdapter = new ReminderAdapter(ReminderActivity.this, reminders, mActivity);
                mRecyclerView.setAdapter(mReminderAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mBack.setOnClickListener(v -> finish());
    }

    private void setupWidgets(){
        mUsername = (TextView) findViewById(R.id.reminderUsername);
        mBack = (ImageView) findViewById(R.id.notificationBack);
    }

    private void setupRecyclerView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRecycleAdapter);
        reminders = new ArrayList<>();
    }

    private void setupFirebase(){
        mFirebaseMethods = new FirebaseMethods(mContext);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();
        if (mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

}
