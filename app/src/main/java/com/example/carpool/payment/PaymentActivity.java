package com.example.carpool.payment;

import static com.example.carpool.utils.Utils.formatValue;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.example.carpool.R;
import com.example.carpool.common.Common;
import com.example.carpool.models.FCMResponse;
import com.example.carpool.models.Notification;
import com.example.carpool.models.RequestUser;
import com.example.carpool.models.Sender;
import com.example.carpool.remote.IFCMService;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "FirebaseFCM";
    private static final int REQUEST_CODE = 1234;

    private final Context mContext = PaymentActivity.this;

    private String token, amount;
    private HashMap<String, String> paramsHash;

    //widgets
    private Button mPaymentBtn;
    private TextView mEditAmount, mCancelBtn;
    private LinearLayout mGroupWaiting, mGroupPayment;

    //Activity data
    private String driverID;
    private String currentLocation;
    private String destination;
    private String rideID;
    private String profile_photo;
    private String profile_photo2;
    private String pickupLocation;
    private String passengerID;
    private String username;
    private String cost;
    private String pickupTime;
    private String dateOnly;
    private String licencePlate;
    private int seatsAvailable = 0;


    //Firebase
    private IFCMService mService;
    private FirebaseMethods mFirebaseMethods;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        passengerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getActivityData();

        mService = Common.getFCMService();
        mFirebaseMethods = new FirebaseMethods(getBaseContext());

        init();
        getUserInformation();
        getsSeatsRemaining();

        new getToken().execute();

        mPaymentBtn.setOnClickListener(v ->
                submitPayment());

        mCancelBtn.setOnClickListener(v -> finish());
    }

    private void submitPayment() {
        if (!mEditAmount.getText().toString().isEmpty()) {

            sendPayments();
        } else {
            Toast.makeText(mContext, "Please enter valid amount", Toast.LENGTH_SHORT).show();
        }
    }

    private void init(){
        mPaymentBtn = findViewById(R.id.paymentBtn);
        mCancelBtn = findViewById(R.id.cancelBtn);
        mEditAmount = findViewById(R.id.moneyTextview);
        mGroupWaiting = findViewById(R.id.waiting_group);
        mGroupPayment = findViewById(R.id.payment_group);

        Long costPassenger = Long.parseLong(cost)/4;
        mEditAmount.setText(formatValue(costPassenger));
    }

    private void getActivityData(){
        Intent intent = this.getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            driverID = intent.getStringExtra("userID");
            currentLocation = intent.getStringExtra("currentLocation");
            destination = intent.getStringExtra("destination");
            rideID = intent.getStringExtra("rideID");
            profile_photo2 = intent.getStringExtra("profile_photo");
            pickupLocation = intent.getStringExtra("pickupLocation");
            cost = intent.getStringExtra("cost");
            licencePlate = intent.getStringExtra("licencePlate");
            dateOnly = intent.getStringExtra("dateOnly");
            pickupTime = intent.getStringExtra("pickupTime");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String strNonce = nonce.getNonce();
                if (!mEditAmount.getText().toString().isEmpty()) {
                    amount = getCurrentAmount();
                    paramsHash = new HashMap<>();
                    paramsHash.put("amount", amount);
                    paramsHash.put("nonce", strNonce);

                    sendPayments();
                } else {
                    Toast.makeText(mContext, "Please enter valid amount", Toast.LENGTH_SHORT).show();
                }
            } else {
                Exception exception = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
    }

    private void sendPayments() {
        requestPickupHere();
    }

    private class getToken extends AsyncTask{

        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(mContext, android.R.style.Theme_DeviceDefault);
            mDialog.setCancelable(false);
            mDialog.setMessage("Please wait");
            mDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            mGroupWaiting.setVisibility(View.GONE);
            mGroupPayment.setVisibility(View.VISIBLE);
            HttpClient client = new HttpClient();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            mDialog.dismiss();
        }
    }

    private String getCurrentAmount(){
        return mEditAmount.getText().toString().split("£")[1];
    }

    private void requestPickupHere() {
        myRef.child(Utils.TOKENS).child(driverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String token = dataSnapshot.getValue(String.class);
                String extraData = username + "," + profile_photo + "," + currentLocation;
                Notification data =
                        new Notification("Carpool", "Hi, i'm " + username +" and would like to request a seat on your journey!", rideID, extraData, destination);
                Sender content = new Sender(data, token);

                mService.sendMessage(content).enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FCMResponse> call, @NonNull Response<FCMResponse> response) {
                        assert response.body() != null;
                        if (response.body().success == 1 || response.code() == 200){
                            Log.e(TAG, "onResponse: " + response.body().results);
                            Toast.makeText(mContext, "Booking request sent!", Toast.LENGTH_SHORT).show();
                            String requestId = FirebaseDatabase.getInstance().getReference().push().getKey();
                            RequestUser request = new RequestUser(requestId,driverID, passengerID, profile_photo2, profile_photo,
                                    username, 1, destination, currentLocation, 1, false,
                                    rideID, dateOnly, pickupTime,   Float.parseFloat(cost.substring(2)), pickupLocation,
                                    licencePlate);

                            myRef.child(Utils.REQUEST_RIDE)
                                    .child(driverID)
                                    .child(requestId)
                                    .setValue(request);
                                //updateSeatsRemaining();
                                mFirebaseMethods.addPoints(driverID, 200);
                        } else {
                            Toast.makeText(mContext, "Booking request failed!", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull Call<FCMResponse> call, @NonNull Throwable t) {
                        Log.e("MinhMX", "onFailure: "+ t.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getUserInformation(){
        myRef.child(Utils.USER).child(passengerID).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRef.child(Utils.INFO).child(passengerID).child("profile_photo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profile_photo = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getsSeatsRemaining(){
        myRef.child(Utils.AVAILABLE_RIDE).child(rideID).child("seatsAvailable").addValueEventListener(new ValueEventListener() {
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
        myRef.child("available_ride").child(rideID).child("seatsAvailable").setValue(seatsAvailable - 1);
    }



}
