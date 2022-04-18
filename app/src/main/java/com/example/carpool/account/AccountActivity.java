package com.example.carpool.account;

import static com.example.carpool.utils.Utils.checkNotifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.carpool.leaderboard.LeaderboardActivity;
import com.example.carpool.R;
import com.example.carpool.models.Info;
import com.example.carpool.settings.SettingsActivity;
import com.example.carpool.utils.BottomNavigationViewHelper;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.utils.SectionsStatePageAdapter;
import com.example.carpool.utils.UniversalImageLoader;
import com.example.carpool.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;


public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";
    private static final int ACTIVITY_NUMBER = 4;

    private final Context mContext = AccountActivity.this;

    private SectionsStatePageAdapter pageAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;
    private BottomNavigationView bottomNavigationView;

    //activity widgets
    private RelativeLayout mEmailUpdate;
    private RelativeLayout mPasswordUpdate;
    private RelativeLayout mDetailsUpdate;
    private RelativeLayout mCarUpdate;
    private Button mSettingsBtn;
    private Button mHelpBtn;
    private RelativeLayout mInformation;
    private ImageView profilePhoto;
    private ImageView leaderboards;
    private TextView mDisplayUsername;
    private TextView mCompleteRides;
    private TextView mEmail;
    private RatingBar mRatingBar;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(mContext);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        if (mAuth.getCurrentUser() != null){
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        setupFirebaseAuth();
        setupFragments();
        setupBottomNavigationView();
        setupActivityWidgets();

        checkNotifications(mRef, userID, mContext, bottomNavigationView);

        mEmailUpdate.setOnClickListener(v ->
            getSupportFragmentManager().beginTransaction().replace(R.id.account_content, new EmailUpdateFragment()).commit()
        );

        mSettingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, SettingsActivity.class);
            startActivity(intent);
        });


        mHelpBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, HelpFragment.class);
            startActivity(intent);
        });


        leaderboards.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, LeaderboardActivity.class);
            startActivity(intent);
        });

        mPasswordUpdate.setOnClickListener(v ->
            getSupportFragmentManager().beginTransaction().replace(R.id.account_content, new PasswordUpdateFragment()).commit()
        );

        mDetailsUpdate.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.account_content, new DetailsUpdateFragment()).commit();
        });

        mInformation.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ProfileActivity.class);
            startActivity(intent);
        });

        mCarUpdate.setOnClickListener(v -> {
            mRelativeLayout.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.account_content, new CarUpdateFragment()).commit();
        });

        profilePhoto.setOnClickListener(v -> {
                chooseImage();
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profilePhoto.setImageBitmap(bitmap);
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
                        mRef.child("info").child(userID).child("profilePhoto").setValue(uri.toString());
                        Log.d(TAG, "uploadImage: "  + uri);
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to upload", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void setupFragments() {
        pageAdapter = new SectionsStatePageAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(new EmailUpdateFragment(), getString(R.string.edit_email));
        pageAdapter.addFragment(new PasswordUpdateFragment(), getString(R.string.edit_password));
        pageAdapter.addFragment(new DetailsUpdateFragment(),  getString(R.string.edit_details));
        pageAdapter.addFragment(new CarUpdateFragment(),  getString(R.string.car_information));
    }

    private void setViewPager(int fragmentNumber) {
        mRelativeLayout.setVisibility(View.GONE);
        mViewPager.setAdapter(pageAdapter);
        Objects.requireNonNull(mViewPager.getAdapter()).notifyDataSetChanged();
        mViewPager.setCurrentItem(fragmentNumber);
    }

    private void setupActivityWidgets(){
        mViewPager = findViewById(R.id.container);
        mRelativeLayout = findViewById(R.id.relative_layout_profile);
        mEmailUpdate = findViewById(R.id.email_update);
        mPasswordUpdate = findViewById(R.id.change_password);
        mDetailsUpdate = findViewById(R.id.update_profile);
        mInformation = findViewById(R.id.information_update);
        mCarUpdate = findViewById(R.id.update_car);
        profilePhoto = findViewById(R.id.profile_image);
        mDisplayUsername = findViewById(R.id.displayUsername);
        mEmail = findViewById(R.id.email_textview);
        mCompleteRides = findViewById(R.id.rides_textview);
        mSettingsBtn = findViewById(R.id.settingsBtn);
        mHelpBtn = findViewById(R.id.helpBtn);
        mRatingBar = findViewById(R.id.ratingBar);
        leaderboards = findViewById(R.id.leaderboards);
    }


    @SuppressLint("SetTextI18n")
    private void setProfileWidgets(User user, Info info){

        UniversalImageLoader.setImage(info.getProfilePhoto(), profilePhoto, null,"");

        mDisplayUsername.setText(user.getUsername());
        mCompleteRides.setText(info.getCompletedRides() + " rides");
        mEmail.setText(user.getEmail());
        mRatingBar.setRating(info.getUserRating());
    }


    private void setupBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }

    private void setupFirebaseAuth(){

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                setProfileWidgets(
                        mFirebaseMethods.getUser(dataSnapshot),
                        mFirebaseMethods.getInfo(dataSnapshot)
                );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mRelativeLayout.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }
}
