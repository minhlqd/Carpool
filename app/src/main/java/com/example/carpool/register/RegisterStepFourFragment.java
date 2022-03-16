package com.example.carpool.register;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carpool.R;
import com.example.carpool.utils.FirebaseMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.UUID;

public class RegisterStepFourFragment extends Fragment {

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //Widgets
    private View mView;
    private FloatingActionButton finish;
    private ImageView mBackButtonFour, mCarPhoto, mRestartRegistration;
    private EditText mLicence, mCar, mSeats, mRegistration;
    private EditText mStartPoint;
    private EditText mDestination;
    private TextInputLayout mLicenceLayout, mCarLayout, mRegistrationLayout, mSeatsLayout;
    private RadioButton mCarToggleTrue, mCarToggleFalse;
    private RadioGroup mCarToggle;
    private Boolean ownVehicle;
    private TextView mQuestionHeading;

    private LinearLayout mDestinationLinearLayout;
    private LinearLayout mVehicle;

    //Profile picture vars
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String imgURL;

    //Interface variables
    private OnButtonClickListener mOnButtonClickListener;

    public interface OnButtonClickListener{
        void onButtonClicked(View view);
    }

    @SuppressLint("NonConstantResourceId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_register_four, container, false);

        //Firebase setup
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(getActivity());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //instantiate objects
        finish = mView.findViewById(R.id.finish);
        mLicence = mView.findViewById(R.id.licenceStepFourEditText);
        mCar = mView.findViewById(R.id.carStepFourEditText);
        mSeats = mView.findViewById(R.id.seatsStepFourEditText);
        mRegistration = mView.findViewById(R.id.registrationStepFourEditText);
        mCarToggle = mView.findViewById(R.id.carToggle);
        mCarToggleFalse = mView.findViewById(R.id.noCarButton);
        mCarToggleTrue = mView.findViewById(R.id.yesCarButton);
        mLicenceLayout = mView.findViewById(R.id.licenceLayout);
        mCarLayout = mView.findViewById(R.id.carLayout);
        mRegistrationLayout = mView.findViewById(R.id.registrationLayout);
        mSeatsLayout = mView.findViewById(R.id.seatsLayout);
        mCarPhoto = mView.findViewById(R.id.uploadCarPicture);
        mStartPoint = mView.findViewById(R.id.start_point);
        mDestination = mView.findViewById(R.id.destination);
        mVehicle = mView.findViewById(R.id.vehicle);
        mDestinationLinearLayout = mView.findViewById(R.id.destinationLinearLayout);
        mQuestionHeading = mView.findViewById(R.id.questionHeading);

        mCarPhoto.setOnClickListener(v -> chooseImage());

        mCarToggle.check(mCarToggleFalse.getId());
        mCarToggle.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId)
            {
                case R.id.yesCarButton:
                    mQuestionHeading.setText(R.string.your_car_information);
                    mVehicle.setVisibility(View.VISIBLE);
                    mDestinationLinearLayout.setVisibility(View.GONE);
                    break;
                case R.id.noCarButton:
                    mQuestionHeading.setText(R.string.your_route);
                    mVehicle.setVisibility(View.GONE);
                    mDestinationLinearLayout.setVisibility(View.VISIBLE);
                    break;
            }
        });

        /*//Make edit fields invisible by default
        mLicence.setVisibility(View.INVISIBLE);
        mCar.setVisibility(View.INVISIBLE);
        mSeats.setVisibility(View.INVISIBLE);
        mRegistration.setVisibility(View.INVISIBLE);
        mLicenceLayout.setVisibility(View.INVISIBLE);
        mCarLayout.setVisibility(View.INVISIBLE);
        mRegistrationLayout.setVisibility(View.INVISIBLE);
        mSeatsLayout.setVisibility(View.INVISIBLE);*/

        finish.setOnClickListener(v -> {
            switch (mCarToggle.getCheckedRadioButtonId())
            {
                case R.id.yesCarButton:
                    if (mLicence.getText().length() > 0 && mCar.getText().length() > 0
                            && mSeats.getText().length() > 0 && mRegistration.getText().length() > 0 ) {
                        mOnButtonClickListener.onButtonClicked(v);
                    }
                    break;

                case R.id.noCarButton:
                    if (mDestination.getText().length() > 0 && mStartPoint.getText().length() > 0) {
                        mOnButtonClickListener.onButtonClicked(v);
                    }
                    break;
            }
        });

        mBackButtonFour = mView.findViewById(R.id.loginBackArrowStep);
        mBackButtonFour.setOnClickListener(v -> mOnButtonClickListener.onButtonClicked(v));

        mRestartRegistration = mView.findViewById(R.id.restartRegistrationBtn);
        mRestartRegistration.setOnClickListener(v -> mOnButtonClickListener.onButtonClicked(v));

        return mView;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            Log.i("STEP3", "onActivityResult: " + filePath);
            try {
                if (getActivity() != null) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                mCarPhoto.setImageBitmap(bitmap);
                uploadImage();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(){
        if (filePath != null){
            final StorageReference ref = storageReference.child("carImages/"+UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        Log.i("STEP3", "onActivityResult: Image uploaded" + uri);
                        imgURL = uri.toString();
                    }))
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed to upload", Toast.LENGTH_SHORT).show();
                            Log.i("STEP3", "onActivityResult: failed to upload" + e.getMessage());
                        }
                    });
        }
    }

    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);
        try {
            mOnButtonClickListener = (OnButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(((Activity) context).getLocalClassName()
                    + " must implement OnButtonClickListener");
        }
    }

    public String getLicence() {
        return mLicence.getText().toString();
    }

    public String getCar() {
        return mCar.getText().toString();
    }

    public int getSeats() {
        if (mSeats.getText().length() > 0){
            return Integer.parseInt(mSeats.getText().toString());
        }
        return 0;
    }

    public String getStartPoint() {
        return mStartPoint.getText().toString();
    }

    public String getDestination(){
        return mDestination.getText().toString();
    }

    public String getRegistration() {
        return mRegistration.getText().toString();
    }

    public Boolean getCarToggle() {
        int whichIndex = mCarToggle.getCheckedRadioButtonId();
        if (whichIndex == R.id.yesCarButton) {
            return true;
        } else if (whichIndex == R.id.noCarButton) {
            return false;
        }
        return false;
    }

    public String getCarPhoto() {
        return imgURL;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}
