package com.example.carpool.register;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterStepOneFragment extends Fragment {

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabse;
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
    private Button mNextButton1;
    private ImageView mbackButton1;
    private EditText mEmail, mPassword;

    private Activity activity;

    public interface OnButtonClickListener{
        void onButtonClicked(View view);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         final View mView = inflater.inflate(R.layout.fragment_register_one, container, false);

        Log.d("MinhMX", "onCreateView: " + true);
        //Firebase setup
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();
        mFirebaseMethods = new FirebaseMethods(getActivity());

        //instantiate objects
        mViewPager = (ViewPager) mView.findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) mView.findViewById(R.id.removeableLayout);

        mNextButton1 = (Button) mView.findViewById(R.id.nextBtn1);
        mEmail = (EditText) mView.findViewById(R.id.emailStepOneEditText);
        mPassword = (EditText) mView.findViewById(R.id.passwordStepOneEditText);

        mNextButton1.setOnClickListener(v -> {
            Log.d("MinhMX", "onCreateView: " + true + "next");
            if(mEmail.getText().length() > 0  && mPassword.getText().length() > 0) {
                if (isValidEmailAddress(mEmail.getText().toString())){
                    if (mPassword.getText().length() >= 6) {
                        Log.d("MinhMX", "onCreateView: " + true + getPassword());
                        //Creates Account with email and password entered
                        mOnButtonClickListener.onButtonClicked(v);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new RegisterStepTwoFragment()).commit();
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

        mbackButton1 = (ImageView) mView.findViewById(R.id.loginBackArrow);
        mbackButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnButtonClickListener.onButtonClicked(v);
            }
        });

        return mView;
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        Log.d("MinhMX", "isValidEmailAddress: " + m.matches());
        return m.matches();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnButtonClickListener = (OnButtonClickListener) context;
            activity = (Activity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(((Activity) context).getLocalClassName()
                    + " must implement OnButtonClickListener");
        }
    }

    public String getEmail(){
        return mEmail.getText().toString().trim();
    }

    public String getPassword(){
        return mPassword.getText().toString().trim();
    }




    /** --------------------------- Firebase ---------------------------- **/

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


