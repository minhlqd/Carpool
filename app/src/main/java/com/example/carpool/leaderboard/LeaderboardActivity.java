package com.example.carpool.leaderboard;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carpool.adapter.LeaderboardsAdapter;
import com.example.carpool.R;
import com.example.carpool.models.Leaderboards;
import com.example.carpool.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LeaderboardActivity extends AppCompatActivity {
    private static final String TAG = "MinhMX";
    private final Context mContext = this;

    //Widgets
    private ImageView backBtn;

    //Recycle View variables
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mRecycleAdapter;
    private LeaderboardsAdapter myAdapter;
    private ArrayList<Leaderboards> leaderboards;

    //Firebase variables
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private String user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        setupWidgets();
        setupFirebase();

        //Setup back arrow for navigating back to 'AccountActivity'
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final List<Leaderboards> model = new ArrayList<>();


        mRef = FirebaseDatabase.getInstance().getReference().child(Utils.INFO);
        mRef.orderByChild("points").limitToFirst(20)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                Leaderboards l = dataSnapshot1.getValue(Leaderboards.class);
                                leaderboards.add(l);
                            }
                            Collections.sort(leaderboards, (o1, o2) ->
                                    o1.getPoints() > o2.getPoints() ? -1 : (o1.points < o2.points ) ? 1 : 0
                            );

                            myAdapter = new LeaderboardsAdapter(mContext, leaderboards);
                            mRecyclerView.setAdapter(myAdapter);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupWidgets(){
        backBtn = (ImageView) findViewById(R.id.leaderboardsBackBtn);
        
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_request);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRecycleAdapter);
        leaderboards = new ArrayList<Leaderboards>();
    }

    private void setupFirebase(){
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            user_id = mAuth.getCurrentUser().getUid();
        }

    }

}
