package com.example.carpool.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.carpool.account.HelpFragment;
import com.example.carpool.R;
import com.example.carpool.home.HomeActivity;
import com.example.carpool.utils.SectionsStatePageAdapter;


public class OfferRideCreatedDialog extends Dialog implements
        View.OnClickListener  {

    private static final String TAG = "OfferRideCreatedDialog";
    public Context context;
    public Dialog d;
    private TextView cancelDialog;
    private Button confirmDialog;
    private SectionsStatePageAdapter pageAdapter;

    public interface onConfirmPasswordListener{
        void onConfirmPassword(String password);
    }

    onConfirmPasswordListener mOnConfirmPasswordListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_offer_ride_created);
        cancelDialog = findViewById(R.id.dialogCancel);
        confirmDialog = findViewById(R.id.pay_and_book);
        cancelDialog.setOnClickListener(this);
        confirmDialog.setOnClickListener(v -> {
            //Shows the ride has been created successfully
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
            dismiss();
        });
    }

    public OfferRideCreatedDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_and_book:
                Intent intent1 = new Intent(context, HelpFragment.class);
                context.startActivity(intent1);
                break;
            case R.id.dialogCancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

}
