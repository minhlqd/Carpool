package com.example.carpool.login;

import static android.view.View.GONE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.carpool.home.HomeActivity;
import com.example.carpool.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MinhMX";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext;
    private ProgressBar mProgressBar;
    private CardView mLoadingCardView;
    private EditText mEmail, mPassword;
    private TextView mPleaseWait, mBtn_signup;
    private final String append = "";
    private String username;
    private String email;
    private TextView mForgotPassword;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = LoginActivity.this;

        setupFirebaseAuth();


        mAuth = FirebaseAuth.getInstance();
        mProgressBar = findViewById(R.id.loginRequestProgressBar);
        mPleaseWait = findViewById(R.id.loadingPleaseWait);
        mLoadingCardView = findViewById(R.id.card_view_loading);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mBtn_signup = findViewById(R.id.btn_signup);
        mForgotPassword = findViewById(R.id.forgot_password);


        mProgressBar.setVisibility(GONE);
        mLoadingCardView.setVisibility(GONE);
        mPleaseWait.setVisibility(GONE);

        init();
    }

    private boolean isStringNull(String string) {
        return string.equals("");
    }

    private void init() {
        //Initialize the button for logging in
        Button mBtn_login = findViewById(R.id.btn_login);
        mBtn_login.setOnClickListener(v -> {

            String email = mEmail.getText().toString();
            String password = mPassword.getText().toString();

            if (isStringNull(email) && isStringNull(password)) {
                Toast.makeText(mContext, "You must fill in empty fields", Toast.LENGTH_SHORT).show();
            } else {
                signIn(email, password);
            }
        });

        mBtn_signup.setOnClickListener(v -> {
           Intent intent = new Intent(mContext, RegisterActivity.class);
           startActivity(intent);
        });

        mForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ForgotPasswordActivity.class));
        });

       /* mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });*/
    }

    private void signIn(String email, String password) {
        mProgressBar.setVisibility(View.VISIBLE);
        mPleaseWait.setVisibility(View.VISIBLE);
        mLoadingCardView.setVisibility(View.VISIBLE);


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {


                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (task.isSuccessful()) {
                            try {
                                if (task.getResult().getAdditionalUserInfo() != null) {
                                    boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                }

                            } catch (NullPointerException e) {
                                Log.e(TAG, "instance initializer: NullPointerException " + e.getMessage());
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);

                            mProgressBar.setVisibility(GONE);
                            mPleaseWait.setVisibility(GONE);
                            mLoadingCardView.setVisibility(GONE);

                        }
                    }
                });
    }

    /***
     *  If the user is logged in then navigate to 'HomeActivity' and call finish()
     * @param user
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }



    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}