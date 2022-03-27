package com.example.carpool.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carpool.R;
import com.example.carpool.models.Info;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.utils.UniversalImageLoader;
import com.example.carpool.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class DetailsUpdateFragment extends Fragment {

    private static final String TAG = "MinhMX";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //Fragment view
    private View view;
    private CircleImageView mProfilePhoto;
    private EditText mUsername, mFullName, mMobileNumber, mDob;
    private TextView mChangePhoto;
    private Button mSnippetDetailsBtn;

    //vars
    private User mUserSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_details, container, false);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_change);
        mUsername = (EditText) view.findViewById(R.id.usernameEditText);
        mFullName = (EditText) view.findViewById(R.id.fullnameEditText);
        mMobileNumber = (EditText) view.findViewById(R.id.phoneEditText);
        mDob = (EditText) view.findViewById(R.id.dobEditText);
        mChangePhoto = (TextView) view.findViewById(R.id.changeProfilePhoto);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();
        mFirebaseMethods = new FirebaseMethods(getActivity());

        mSnippetDetailsBtn = (Button) view.findViewById(R.id.snippetDetailsBtn);
        mSnippetDetailsBtn.setOnClickListener(v -> saveProfileSettings());

        setupFirebaseAuth();


        //Setup back arrow for navigating back to 'ProfileActivity'
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), AccountActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     * Retrieves the data inside the widgets and saves it to the database.
     */
    private void saveProfileSettings(){
        final String username = mUsername.getText().toString();
        final String fullName = mFullName.getText().toString();
        final String mobileNumber = mMobileNumber.getText().toString();
        final Long dob = Long.parseLong(mDob.getText().toString());

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {




                //If the user is the current one then no changes have been made
                if (!mUserSettings.getUsername().equals(username)){

                    checkIfUsernameExists(username);
                }
                //user changed there username, checking for uniquness
                else {

                }

                //user did not change there username and email

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /***
     * checks if @param username already exisits in the database
     * @param username
     */
    private void checkIfUsernameExists(final String username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("user")
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    //add the username
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(getActivity(), "Username changed", Toast.LENGTH_SHORT).show();

                }
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()){
                        Toast.makeText(getActivity(), "Username already exists", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setProfileWidgets(User user, Info info){


        mUserSettings = user;

        UniversalImageLoader.setImage(info.getProfilePhoto(), mProfilePhoto, null,"");

        mUsername.setText(user.getUsername());
        mFullName.setText(user.getFullName());
        mMobileNumber.setText(String.valueOf(info.getMobileNumber()));
        mDob.setText(String.valueOf(info.getDateOfBird()));

        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ShareActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
//                getActivity().startActivity(intent);
//                getActivity().finish();
            }
        });
    }



    private void setupFirebaseAuth(){

        userID = mAuth.getCurrentUser().getUid();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve user information from the database
                setProfileWidgets(mFirebaseMethods.getUser(dataSnapshot),
                        mFirebaseMethods.getInfo(dataSnapshot));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /***
     *  Setup the firebase object
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}
