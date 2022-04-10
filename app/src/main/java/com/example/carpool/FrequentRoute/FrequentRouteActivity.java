package com.example.carpool.FrequentRoute;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carpool.R;
import com.example.carpool.adapter.FrequentRouteAdapter;
import com.example.carpool.models.FrequentRouteResults;
import com.example.carpool.remote.FrequentRouteClient;
import com.example.carpool.remote.FrequentRouteService;
import com.example.carpool.utils.BottomNavigationViewHelper;
import com.example.carpool.utils.FirebaseMethods;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrequentRouteActivity extends AppCompatActivity {


    private static final String TAG = "FrequentRouteActivity";
    private static final int ACTIVITY_NUMBER = 1;

    //View variables
    private RelativeLayout mNoResultsFoundLayout;
    private BottomNavigationView bottomNavigationView;
    private Context mContext = FrequentRouteActivity.this;

    //Recycle View variables
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mRecycleAdapter;
    private FrequentRouteAdapter mAdapter;
    private ArrayList<FrequentRouteResults> routes;


    //Firebase variables
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;

    private String user_id;

    //Backend
    private FrequentRouteService mFrequentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequent_route);
        setupBottomNavigationView();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();
        if (mAuth.getCurrentUser() != null) {
            user_id = mAuth.getCurrentUser().getUid();
            Log.i(TAG, "onCreate: " + user_id);
        }

        //setup recycle view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRecycleAdapter);

        mNoResultsFoundLayout = (RelativeLayout) findViewById(R.id.noResultsFoundLayout);
        mFrequentService = FrequentRouteClient.getFrequentRouteClient();
        routes = new ArrayList<>();
    }

    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        //BottomNavigationViewHelper.addBadge(mContext, bottomNavigationView);

        //Change current highlighted icon
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            if (mAdapter.getItemCount() > 0) {
                mRecyclerView.removeAllViewsInLayout();
                createRecycleView();
            }
        }
        else {
            createRecycleView();
        }
    }

    private void createRecycleView() {
        mFrequentService.getRouteByAccountId(user_id)
                .enqueue(new Callback<List<FrequentRouteResults>>() {
                    @Override
                    public void onResponse(Call<List<FrequentRouteResults>> call, Response<List<FrequentRouteResults>> response) {
                        if (response.isSuccessful()) {
                            List<FrequentRouteResults> routeList = response.body();
                            if (!routeList.isEmpty()) {
                                routes = new ArrayList<FrequentRouteResults>(routeList);
                                mAdapter = new FrequentRouteAdapter(FrequentRouteActivity.this, routes);
                                mRecyclerView.setAdapter(mAdapter);
                                mNoResultsFoundLayout.setVisibility(View.INVISIBLE);
                            } else {
                                mNoResultsFoundLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FrequentRouteResults>> call, Throwable t) {
                        Toast.makeText(FrequentRouteActivity.this, "Have error when get frequent route!", Toast.LENGTH_SHORT).show();
                        mNoResultsFoundLayout.setVisibility(View.VISIBLE);
                        Log.e("Frequent Route",t.getLocalizedMessage());
                    }
                });
    }

}