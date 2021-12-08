package com.symplified.phonecallverificationsdk;

import static android.Manifest.permission.ANSWER_PHONE_CALLS;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_PHONE_STATE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.textfield.TextInputLayout;
import com.symplified.phonecallverificationsdk.interfaces.FlashCallHandler;
import com.symplified.phonecallverificationsdk.layouts.KalsymCallVerificationLayout;

import org.json.JSONObject;

import java.io.Serializable;

public class VerificationActivity extends AppCompatActivity implements Serializable{

    private PhoneCallReceiver phoneCallReceiver;
    private KalsymCallVerificationLayout kalsymCallVerificationLayout;
    private Button mVerifyButton;
    private EditText mPhoneNumberEditText;
    private TextInputLayout mPhoneNumberLayout;
    private static final String BASE_URL = "http://18.217.223.180:2089";
    private FlashCallHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        Class<?> nextClass = getIntent().getExtras().containsKey("next") ? (Class<?>) getIntent().getSerializableExtra("next") : null;
//        phoneCallReceiver = getIntent().getExtras().containsKey("receiver") ? (PhoneCallReceiver) getIntent().getSerializableExtra("receiver") : null;
        phoneCallReceiver = new PhoneCallReceiver(nextClass, mHandler);
        kalsymCallVerificationLayout = findViewById(R.id.kalsym_call_verification_layout);
        mVerifyButton = kalsymCallVerificationLayout.getmButton();
        mPhoneNumberEditText = kalsymCallVerificationLayout.getmEditText();
        mPhoneNumberLayout = kalsymCallVerificationLayout.getmEditTextLayout();
        mVerifyButton.setOnClickListener(view -> {
            Toast.makeText(this, ""+kalsymCallVerificationLayout.getPhoneNumber(), Toast.LENGTH_SHORT).show();
            registerReceiver(phoneCallReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));
            kalsymCallVerificationLayout.createConfirmationDialog(
                    (dialog, i) -> {
                        receiveVerificationCall(kalsymCallVerificationLayout.getPhoneNumber());
                    }
            );
//            receiveVerificationCall(kalsymCallVerificationLayout.getPhoneNumber());
        });


        if(
                ContextCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, INTERNET) != PackageManager.PERMISSION_GRANTED
        )
        {
            Toast.makeText(this, "permissions are not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(VerificationActivity.this, new String[] { READ_PHONE_STATE, READ_CALL_LOG, ANSWER_PHONE_CALLS, INTERNET }, 101);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            unregisterReceiver(phoneCallReceiver);
        }catch (IllegalArgumentException e){
            Log.e(VerificationActivity.class.getName(), "onPause: ", e.getCause() );
        }
    }

    public void receiveVerificationCall(String number){
        kalsymCallVerificationLayout.showProgress();
        String verifyEndpointUrl = "/verifyNumber";
        AndroidNetworking.get(BASE_URL+verifyEndpointUrl)
                .addQueryParameter("phoneNumber", number)
                .addQueryParameter("refId", number)
                .setTag("Call")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        kalsymCallVerificationLayout.hideProgress();
//                        try {
////                            Toast.makeText(MainActivity.this, ""+response.getString("data"), Toast.LENGTH_SHORT).show();
//                        } catch (JSONException e) {
//                            Toast.makeText(MainActivity.this, "Empty", Toast.LENGTH_SHORT).show();
//                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        kalsymCallVerificationLayout.hideProgress();
                        Toast.makeText(VerificationActivity.this, "An Error has occurred", Toast.LENGTH_SHORT).show();
                        mPhoneNumberLayout.setErrorEnabled(true);
                        mPhoneNumberLayout.setError("Failed, Please try again !");
                        Log.e(VerificationActivity.class.getName(), "onError: ", anError);
                    }
                });
    }
}