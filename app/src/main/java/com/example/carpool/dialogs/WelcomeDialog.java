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
import com.example.carpool.utils.SectionsStatePageAdapter;

public class WelcomeDialog extends Dialog implements
        View.OnClickListener  {

    private static final String TAG = "WelcomeDialog";
    public Context mContext;
    public Dialog d;
    private TextView cancelDialog;
    private  Button confirmDialog;
    private SectionsStatePageAdapter pageAdapter;
    
    public interface onConfirmPasswordListener{
        public void onConfirmPassword(String password);
    }

    onConfirmPasswordListener mOnConfirmPasswordListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_welcome);
        cancelDialog = (TextView) findViewById(R.id.dialogCancel);
        confirmDialog = (Button) findViewById(R.id.pay_and_book);
        cancelDialog.setOnClickListener(this);
        confirmDialog.setOnClickListener(this);

    }

    public WelcomeDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_and_book:
                dismiss();
                Intent intent1 = new Intent(mContext, HelpFragment.class);
                mContext.startActivity(intent1);
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
