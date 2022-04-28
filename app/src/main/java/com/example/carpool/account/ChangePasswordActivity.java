package com.example.carpool.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ChangePasswordActivity";

    //Fragment view
    private View view;

    //widgets
    private Button mSnippetPasswordBtn;
    private EditText mPassword;

    //vars
    private User mUserSettings;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        //Firebase setup
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mFirebaseDatabase.getReference();
        FirebaseMethods mFirebaseMethods = new FirebaseMethods(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mPassword = findViewById(R.id.passwordEditTextSnippet);



        mSnippetPasswordBtn = findViewById(R.id.snippetPasswordBtn);
        mSnippetPasswordBtn.setOnClickListener(v -> savePasswordSettings());


        //Setup back arrow for navigating back to 'ProfileActivity'
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            onBackPressed();
        });

        setupFirebaseAuth();

    }

    private void savePasswordSettings(){
        final String password = mPassword.getText().toString();
        firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "updated" , Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        /*//Check if the email exists in the database
        if (!mUserSettings.getEmail().equals(password)){

        }
        else {

        }*/
    }

    private void setProfileWidgets(User userSettings){

        User user = userSettings;

        mUserSettings = userSettings;
    }


    private void setupFirebaseAuth(){
        String userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}