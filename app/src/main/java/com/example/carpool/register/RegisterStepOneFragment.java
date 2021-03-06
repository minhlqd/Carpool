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
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.carpool.R;
import com.example.carpool.utils.FirebaseMethods;
import com.example.carpool.utils.SectionsStatePageAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings("FieldCanBeLocal")
public class RegisterStepOneFragment extends Fragment {

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //Fragment variables
    private SectionsStatePageAdapter pageAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;

    //Interface variables
    private OnButtonClickListener mOnButtonClickListener;

    //widgets
    private FloatingActionButton mNextButtonOne;
    private ImageView mBackButtonOne;
    private EditText mEmail, mPassword;
    private EditText mUsername;

    private Activity activity;

    public interface OnButtonClickListener{
        void onButtonClicked(View view);
    }

    public static RegisterStepOneFragment newInstance(int page, String title) {
        RegisterStepOneFragment contactsFragment = new RegisterStepOneFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        args.putString("title", title);
        contactsFragment.setArguments(args);
        return contactsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         final View mView = inflater.inflate(R.layout.fragment_register_one, container, false);
        //Firebase setup
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(getActivity());

        //instantiate objects
        mViewPager = mView.findViewById(R.id.container);
        mRelativeLayout = mView.findViewById(R.id.removeableLayout);

        mNextButtonOne = mView.findViewById(R.id.next_btn_one);
        mEmail = mView.findViewById(R.id.email);
        mPassword = mView.findViewById(R.id.password);
        mUsername = mView.findViewById(R.id.username);

        mNextButtonOne.setOnClickListener(v -> {
            if(mEmail.getText().length() > 0  && mPassword.getText().length() > 0
                    && mUsername.getText().length() > 0) {
                if (isValidEmailAddress(mEmail.getText().toString())){
                    if (mPassword.getText().length() >= 8) {
                        mOnButtonClickListener.onButtonClicked(v);
                    } else {
                        Toast.makeText(mView.getContext(), "Please use stronger password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mView.getContext(), "Invalid email address.", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(mView.getContext(), "All fields must be filled in.", Toast.LENGTH_SHORT).show();
            }
        });

        mBackButtonOne = mView.findViewById(R.id.loginBackArrow);
        mBackButtonOne.setOnClickListener(v -> mOnButtonClickListener.onButtonClicked(v));

        return mView;
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
        try {
            mOnButtonClickListener = (OnButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException((activity).getLocalClassName()
                    + " must implement OnButtonClickListener");
        }
    }

    public String getEmail(){
        return mEmail.getText().toString().trim();
    }

    public String getPassword(){
        return mPassword.getText().toString().trim();
    }

    public String getUsername() {
        return mUsername.getText().toString().trim();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}


