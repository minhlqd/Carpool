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
import com.example.carpool.models.BookingResults;
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
    public Context c;
    public Dialog d;

    //View variables
    private RelativeLayout[] mUserBooked;
    private ImageView[] mProfilePicture;
    private ImageView user_id_1;
    private TextView[] mUsernames;
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

        mFirebaseMethods = new FirebaseMethods(c);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        setupWidgets();

        findDriverInformation();

        findParticipantDetails();

        mCancelDialogBtn = findViewById(R.id.dialogCancel);
        mCancelDialogBtn.setOnClickListener(this);

    }

    public ParticipantsDialog(Context a, String userID, String rideID) {
        super(a);
        this.c = a;
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
        username1 = findViewById(R.id.username1);
        user_id_1 = findViewById(R.id.user_id_1);

        //Setup recycler view
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(c);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(c));
        mRecyclerView.setAdapter(mRecycleAdapter);
        participants = new ArrayList<Participants>();
    }

    private void findDriverInformation(){

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve user information from the database
                setDriverWidgets(mFirebaseMethods.getSpecificUser(dataSnapshot, userID),
                        mFirebaseMethods.getInfo(dataSnapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setDriverWidgets(User user, Info info){
        UniversalImageLoader.setImage(info.getProfilePhoto(), user_id_1, null,"");

        username1.setText(user.getUsername());
    }

    private void findParticipantDetails(){
        mRef.child(Utils.REQUEST_RIDE).child(userID).child(rideID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                        RequestUser requestUser = dataSnapshot.getValue(RequestUser.class);
                        if (requestUser.getAccepted().equals(true)){
                            participants.add(new Participants(requestUser.getUsername(),requestUser.getAccepted()));
                    }
                }

                myAdapter = new ParticipantsAdapter(c, participants);
                mRecyclerView.setAdapter(myAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



}
