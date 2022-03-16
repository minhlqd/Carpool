package com.example.carpool.register;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@SuppressWarnings("FieldCanBeLocal")
public class RegisterStepTwoFragment extends Fragment {

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //Widgets
    private View mView;
    private EditText mUsernameStepTwoEditText;
    private ImageView mRestartRegistration, backButton2;

    private OnButtonClickListener mOnButtonClickListener;
    private String username;

    public interface OnButtonClickListener{
        void onButtonClicked(View view);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_register_two, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(getActivity());

        mUsernameStepTwoEditText = (EditText) mView.findViewById(R.id.username);

        Button mNextBtn2 = (Button) mView.findViewById(R.id.next_btn_register_two);
        mNextBtn2.setOnClickListener(v -> {
            if(mUsernameStepTwoEditText.getText().length() > 0){
                mOnButtonClickListener.onButtonClicked(v);
                username = mUsernameStepTwoEditText.getText().toString();
            } else {
                Toast.makeText(mView.getContext(), "All fields must be filled in.", Toast.LENGTH_SHORT).show();
            }
        });

        backButton2 = (ImageView) mView.findViewById(R.id.loginBackArrowStep);
        backButton2.setOnClickListener(v ->
                mOnButtonClickListener.onButtonClicked(v)
        );

        mRestartRegistration = (ImageView) mView.findViewById(R.id.restartRegistrationBtn);

        mRestartRegistration.setOnClickListener(v ->
                mOnButtonClickListener.onButtonClicked(v)
        );

        return mView;
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

    public String getUsername(){
        //return mUsernameStepTwoEditText.getText().toString().trim().replaceAll("\\s+","");
        return username;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}
