package com.example.carpool.account;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carpool.R;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.dialogs.ConfirmPasswordDialog;
import com.example.carpool.models.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmailUpdateFragment extends Fragment implements ConfirmPasswordDialog.onConfirmPasswordListener {

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

    //interfaces
    @Override
    public void onConfirmPassword(String password) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), password);

        assert user != null;
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mAuth.fetchSignInMethodsForEmail(mEmail.getText().toString()).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                try {
                                    if (task1.getResult().getSignInMethods().size() == 1) {
                                        Toast.makeText(getActivity(), "Email in use", Toast.LENGTH_SHORT).show();
                                    } else {
                                        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                                        assert user1 != null;
                                        user1.updateEmail(mEmail.getText().toString())
                                                .addOnCompleteListener(task11 -> {
                                                    if (task11.isSuccessful()) {
                                                        Toast.makeText(getActivity(), "User email address updated.", Toast.LENGTH_SHORT).show();
                                                        mFirebaseMethods.updateEmail(mEmail.getText().toString());
                                                    }
                                                });
                                    }
                                } catch (NullPointerException e){
                                    Log.e(TAG, "onComplete: NullPointerExceptionL " + e.getMessage());
                                }
                            }
                        });

                    } else {
                        Log.d(TAG, "onComplete: re-auth failed");
                    }
                });

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_update_email, container, false);


        //Widget setup
        mEmail = (EditText) view.findViewById(R.id.update_password);
        mChangeEmailButton = (Button) view.findViewById(R.id.snippetEmailBtn);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(getActivity());

        setupFirebaseAuth();

        mChangeEmailButton.setOnClickListener(v -> saveEmailSettings());

        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
    }

    private void saveEmailSettings(){
        final String email = mEmail.getText().toString();

        if (!mUserSettings.getEmail().equals(email)){
            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
            assert getFragmentManager() != null;
            dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
            dialog.setTargetFragment(EmailUpdateFragment.this, 1);
        }
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
