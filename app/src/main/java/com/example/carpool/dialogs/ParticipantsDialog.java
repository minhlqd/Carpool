package com.example.carpool.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carpool.adapter.ParticipantsAdapter;
import com.example.carpool.R;
import com.example.carpool.models.Info;
import com.example.carpool.models.RequestUser;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.utils.UniversalImageLoader;
import com.example.carpool.models.Participants;
import com.example.carpool.models.User;
import com.example.carpool.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ParticipantsDialog extends Dialog implements
        View.OnClickListener  {

    private static final String TAG = "ParticipantsDialog";
    public Context context;
    public Dialog d;
    private ImageView user_id_1;
    private TextView username1;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mRecycleAdapter;
    private ParticipantsAdapter myAdapter;
    private ArrayList<Participants> participants;

    // variables
    private TextView mCancelDialogBtn;
    private final String userID;
    private final String rideID;
    private String profilePhoto;
    private String username;

    //Firebase
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_participants);

        mFirebaseMethods = new FirebaseMethods(context);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        setupWidgets();

        findParticipantDetails();

        mCancelDialogBtn = findViewById(R.id.dialogCancel);
        mCancelDialogBtn.setOnClickListener(this);

    }

    public ParticipantsDialog(Context context, String userID, String rideID) {
        super(context);
        this.context = context;
        this.userID = userID;
        this.rideID = rideID;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogCancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void setupWidgets(){

        //Setup recycler view
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(mRecycleAdapter);
        participants = new ArrayList<Participants>();
    }

    private void findParticipantDetails(){
        mRef.child("participant").child(rideID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String userId =  snapshot1.getValue(String.class);
                    mRef.child("user").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            participants.add(new Participants(user.getUsername(), user.getProfilePhoto(), true, userId));

                            myAdapter = new ParticipantsAdapter(context, participants);
                            mRecyclerView.setAdapter(myAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}
