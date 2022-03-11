package com.example.carpool.register;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carpool.R;
import com.example.carpool.utils.FirebaseMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;


@SuppressWarnings("FieldCanBeLocal")
public class RegisterStepThreeFragment extends Fragment {

    private static final int NUMBER_PHONE = 10;

    private static final int ZERO_FIELD = 0;


    //widgets
    private View mView;
    private Button mNextButton3;
    private ImageView mBackButton3;
    private ImageView mRegistrationPicture;
    private ImageView mRestartRegistration;
    private RadioGroup mGenderGroup;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private EditText mFullName;
    private EditText mMobileNumber;
    private EditText mDob;
    private EditText mWork;
    private EditText mEducation;
    private EditText mBio;
    private String gender;
    private String imgURL;
    private Calendar mCalendar;
    private DatePickerDialog.OnDateSetListener date;


    //Profile picture vars
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //Interface variables
    private OnButtonClickListener mOnButtonClickListener;

    public interface OnButtonClickListener{
        void onButtonClicked(View view);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_register_three, container, false);

        //Firebase setup
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(getActivity());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //instantiate objects
        mNextButton3 = (Button) mView.findViewById(R.id.nextBtn3);
        mDob = (EditText) mView.findViewById(R.id.dobStepThreeEditText);
        mFullName = (EditText) mView.findViewById(R.id.full_name);
        mMobileNumber = (EditText) mView.findViewById(R.id.mobileStepThreeEditText);
        mGenderGroup = (RadioGroup) mView.findViewById(R.id.genderToggle);
        maleRadioButton = (RadioButton) mView.findViewById(R.id.femaleButton);
        femaleRadioButton = (RadioButton) mView.findViewById(R.id.maleButton);
        mRegistrationPicture= (ImageView) mView.findViewById(R.id.registrationPicture);
        mWork = (EditText) mView.findViewById(R.id.workEditTextStepThree);
        mEducation = (EditText) mView.findViewById(R.id.educationEditTextStepThree);
        mBio = (EditText) mView.findViewById(R.id.bioEditTextStepThree);

        mCalendar = Calendar.getInstance();
        date = (view, year, month, dayOfMonth) -> {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        mNextButton3.setOnClickListener(v -> {
            if(maleRadioButton.isChecked() || femaleRadioButton.isChecked()) {
                if (mDob.getText().length() > ZERO_FIELD && mFullName.getText().length() > ZERO_FIELD && mMobileNumber.getText().length() > ZERO_FIELD &&
                        mWork.getText().length() > ZERO_FIELD && mEducation.getText().length() > ZERO_FIELD && mBio.getText().length() > ZERO_FIELD ){
                    if (mMobileNumber.getText().length() == NUMBER_PHONE) {
                        mOnButtonClickListener.onButtonClicked(v);
                    } else {
                        Toast.makeText(mView.getContext(), "Invalid phone number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mView.getContext(), "All fields must be filled in", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mView.getContext(), "Please select gender", Toast.LENGTH_SHORT).show();
            }
        });

        mBackButton3 = (ImageView) mView.findViewById(R.id.loginBackArrowStep);
        mBackButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnButtonClickListener.onButtonClicked(v);
            }
        });

        mRestartRegistration = (ImageView) mView.findViewById(R.id.restartRegistrationBtn);
        mRestartRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnButtonClickListener.onButtonClicked(v);
            }
        });

        mRegistrationPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("STEP3", "onClick: adding profile photo");
                chooseImage();
            }
        });

        mDob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 0);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), date, mCalendar
                    .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });

        return mView;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                if (getActivity() != null) {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                    mRegistrationPicture.setImageBitmap(bitmap);
                    uploadImage();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(){
        if (filePath != null){
            final StorageReference ref = storageReference.child("profile/"+UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(taskSnapshot ->
                    ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        imgURL = uri.toString();
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to upload", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mOnButtonClickListener = (OnButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(((Activity) context).getLocalClassName()
                    + " must implement OnButtonClickListener");
        }
    }

    private void updateLabel() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.UK);

        mDob.setText(simpleDateFormat.format(mCalendar.getTime()));
    }


    public String getFullName() {
        return mFullName.getText().toString().trim();
    }

    public long getMobileNumber() {
        return Long.parseLong(mMobileNumber.getText().toString().trim());
    }

    public String getDob() {
        return mDob.getText().toString().trim();
    }

    public String getGender() {
        int whichIndex = mGenderGroup.getCheckedRadioButtonId();
        if (whichIndex == R.id.femaleButton) {
            return "Female";
        } else if (whichIndex == R.id.maleButton) {
            return "Male";
        }
        return "Not specified";
    }

    public String getRegistrationPicture() {
        return imgURL;
    }

    public String getWork() {
        return mWork.getText().toString().trim();
    }

    public String getEducation() {
        return mEducation.getText().toString().trim();
    }

    public String getBio() {
        return mBio.getText().toString().trim();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}
