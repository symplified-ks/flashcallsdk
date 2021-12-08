package com.symplified.phonecallverificationsdk.layouts;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.symplified.phonecallverificationsdk.R;

import java.util.Locale;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;

public class KalsymCallVerificationLayout extends RelativeLayout {

    private TextInputLayout mEditTextLayout;
    private EditText mEditText;
    private Button mButton;
    private MaterialAlertDialogBuilder mBuilder;
    private AlertDialog confirmationDialog;
    private Dialog progressDialog;

    public KalsymCallVerificationLayout(Context context) {
        super(context);
        init(context, R.layout.kalsym_call_verification_layout);
    }

    public KalsymCallVerificationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, R.layout.kalsym_call_verification_layout);
    }

    public KalsymCallVerificationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, R.layout.kalsym_call_verification_layout);
    }

    public KalsymCallVerificationLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, R.layout.kalsym_call_verification_layout);
    }

    private void init(Context context, int LAYOUT_ID){
        inflate(context, LAYOUT_ID, this);
        mEditTextLayout = findViewById(R.id.verification_phone_no_layout);
        mEditText = mEditTextLayout.getEditText();
        mButton = findViewById(R.id.verification_verify_button);
        mBuilder = new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog__Center);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence input, int i, int i1, int i2) {
                if(input.length() < 1){
                    mEditTextLayout.setErrorEnabled(true);
                    mEditTextLayout.setError("Please enter a valid number !");
                }
                else
                {
                    mEditTextLayout.setErrorEnabled(false);
                    mEditTextLayout.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        progressDialog = new Dialog(context);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        CircularProgressIndicator progressIndicator = progressDialog.findViewById(R.id.progress);
        progressIndicator.setIndeterminate(true);

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = String.valueOf(PhoneNumberUtil.createInstance(context).getCountryCodeForRegion(telephonyManager.getNetworkCountryIso().toUpperCase()));
        mEditTextLayout.setPrefixText("+" + countryCode);
    }


    public EditText getmEditText() {
        return mEditText;
    }

    public TextInputLayout getmEditTextLayout() {
        return mEditTextLayout;
    }

    public Button getmButton() {
        return mButton;
    }

    public String getPhoneNumber(){
        return mEditTextLayout.getPrefixText() + mEditText.getText().toString();
    }

    public void createConfirmationDialog(DialogInterface.OnClickListener confirmAction){
        mBuilder.setTitle(R.string.confirmation_text);
        mBuilder.setMessage(getPhoneNumber());
        mBuilder.setPositiveButton("Confirm", confirmAction);
        mBuilder.setNegativeButton("Cancel", null);
        confirmationDialog = mBuilder.create();
        confirmationDialog.show();
    }

    public void showProgress(){
        progressDialog.show();
    }

    public void hideProgress(){
        progressDialog.dismiss();
    }
}
