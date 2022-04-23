package com.example.carpool.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carpool.R;
import com.example.carpool.login.LoginActivity;
import com.example.carpool.models.User;
import com.example.carpool.utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangeEmailActivity extends AppCompatActivity {
    private static final String TAG = "MinhMX";


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //Widgets
    private EditText mEmail;
    private Button mChangeEmailButton;

    //vars
    private User mUserSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_update_email);
        mEmail = (EditText) findViewById(R.id.update_email);
        mChangeEmailButton = (Button) findViewById(R.id.snippetEmailBtn);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(this);
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setupFirebaseAuth();

        mChangeEmailButton.setOnClickListener(v -> saveEmailSettings());

        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            onBackPressed();
        });
    }


    private void saveEmailSettings(){
        final String email = mEmail.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updateEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mRef.child("user").child(userID).child("email").setValue(email);
                Toast.makeText(ChangeEmailActivity.this, "The email updated.", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void setProfileWidgets(User user){
        mUserSettings = user;

        mEmail.setText(user.getEmail());
    }



    private void setupFirebaseAuth(){

        userID = mAuth.getCurrentUser().getUid();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve user information from the database
                setProfileWidgets(mFirebaseMethods.getUser(dataSnapshot));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}