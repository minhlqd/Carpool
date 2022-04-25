package com.example.carpool.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carpool.R;
import com.example.carpool.models.Info;
import com.example.carpool.models.User;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.utils.UniversalImageLoader;
import com.example.carpool.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChangeInformationActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //Fragment view
    private View view;
    private CircleImageView mProfilePhoto;
    private EditText mUsername;
    private EditText mFullName;
    private EditText mMobileNumber;
    private EditText mDob;
    private TextView mChangePhoto;
    private Button mSnippetDetailsBtn;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    //vars
    private User mUserSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_information);
        mProfilePhoto = findViewById(R.id.profile_change);
        mUsername = findViewById(R.id.usernameEditText);
        mFullName = findViewById(R.id.fullnameEditText);
        mMobileNumber = findViewById(R.id.phoneEditText);
        mDob = findViewById(R.id.dobEditText);
        mChangePhoto = findViewById(R.id.changeProfilePhoto);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(this);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        mSnippetDetailsBtn = findViewById(R.id.snippetDetailsBtn);
        mSnippetDetailsBtn.setOnClickListener(v -> saveProfileSettings());

        setupFirebaseAuth();


        //Setup back arrow for navigating back to 'ProfileActivity'
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void saveProfileSettings(){
        final String username = mUsername.getText().toString();
        final String fullName = mFullName.getText().toString();
        final String mobileNumber = mMobileNumber.getText().toString();
        //final String dob = mDob.getText().toString();
        Map<String, Object> updateUser = new HashMap<String,Object>();

        updateUser.put("username", username);
        updateUser.put("fullName", fullName);

        //mRef.child("user").child(userID).updateChildren(updateUser);

        Map<String, Object> updateInfo = new HashMap<String,Object>();

        //updateInfo.put("dateOfBird", dob);
        updateInfo.put("mobileNumber", mobileNumber);

        //mRef.child("info").child(userID).updateChildren(updateInfo);

        /*mRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
        });*/
        onBackPressed();
    }

    private void checkIfUsernameExists(final String username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(Utils.USER)
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    //add the username
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(ChangeInformationActivity.this, "Username changed", Toast.LENGTH_SHORT).show();

                }
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()){
                        Toast.makeText(ChangeInformationActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();

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
                chooseImage();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    Uri filePath;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mProfilePhoto.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(){
        if (filePath != null){
            final StorageReference ref = storageReference.child("profile/"+ UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(taskSnapshot ->
                    ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        mRef.child(Utils.INFO).child(userID).child("profilePhoto").setValue(uri.toString());
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to upload", Toast.LENGTH_SHORT).show();
                    });
        }
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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}