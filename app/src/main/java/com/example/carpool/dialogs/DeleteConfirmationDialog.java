package com.example.carpool.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.carpool.R;
import com.example.carpool.rides.RidesActivity;
import com.example.carpool.utils.FirebaseMethods;


public class DeleteConfirmationDialog extends Dialog implements
        View.OnClickListener  {

    private static final String TAG = "ViewRideCreatedDialog";
    public Context context;
    public Dialog d;

    //Firebase
    private FirebaseMethods mFirebaseMethods;

    // variables
    private TextView mCancelDialogBtn;
    private Button mDeleteRideBtn;

    private String rideID;
    private String driverID;


    public interface onConfirmPasswordListener{
        public void onConfirmPassword(String password);
    }

    onConfirmPasswordListener mOnConfirmPasswordListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_delete_confirmation);

        setupWidgets();
        setupFirebase();

        mCancelDialogBtn.setOnClickListener(this);
        mDeleteRideBtn.setOnClickListener(this);
    }

    public DeleteConfirmationDialog(Context a, String rideID, String driverID) {
        super(a);
        this.context = a;
        this.rideID = rideID;
        this.driverID = driverID;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_and_book:
                mFirebaseMethods.deleteRide(driverID, rideID, context);
                break;
            case R.id.dialogCancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void setupWidgets(){
        //Setup widgets
        mDeleteRideBtn = (Button) findViewById(R.id.pay_and_book);
        mCancelDialogBtn = (TextView) findViewById(R.id.dialogCancel);
    }

    private void setupFirebase() {

        mFirebaseMethods = new FirebaseMethods(context);
    }

}
