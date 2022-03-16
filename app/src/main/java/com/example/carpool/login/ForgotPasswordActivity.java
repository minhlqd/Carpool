package com.example.carpool.login;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carpool.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.email_forgot_password);
        Button send = findViewById(R.id.send);

        send.setOnClickListener(v -> {
            if (email.getText() != null) {
                String txt_email = email.getText().toString();
                FirebaseAuth.getInstance().sendPasswordResetEmail(txt_email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, R.string.check_your_email, Toast.LENGTH_LONG).show();
                                onBackPressed();
                            }
                        });
            }
        });

    }
}