package com.symplified.phonecallverification;

import static android.Manifest.permission.ANSWER_PHONE_CALLS;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_PHONE_STATE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.textfield.TextInputLayout;
import com.symplified.phonecallverificationsdk.FlashCallApplication;
import com.symplified.phonecallverificationsdk.PhoneCallReceiver;
import com.symplified.phonecallverificationsdk.interfaces.FlashCallHandler;
import com.symplified.phonecallverificationsdk.layouts.KalsymCallVerificationLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class MainActivity extends AppCompatActivity{

//    private PhoneCallReceiver phoneCallReceiver;
//    private KalsymCallVerificationLayout kalsymCallVerificationLayout;
//    private Button mVerifyButton;
//    private EditText mPhoneNumberEditText;
//    private TextInputLayout mPhoneNumberLayout;
//    private static final String BASE_URL = "http://18.217.223.180:2089";

    private TextView mTestResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTestResult = findViewById(R.id.test_result);
        FlashCallApplication.init(this, HomeActivity.class, "Test");
//        finish();
//        phoneCallReceiver = new PhoneCallReceiver(HomeActivity.class);
//        kalsymCallVerificationLayout = findViewById(R.id.kalsym_call_verification_layout);
//        mVerifyButton = kalsymCallVerificationLayout.getmButton();
//        mPhoneNumberEditText = kalsymCallVerificationLayout.getmEditText();
//        mPhoneNumberLayout = kalsymCallVerificationLayout.getmEditTextLayout();
//        mVerifyButton.setOnClickListener(view -> {
//            Toast.makeText(this, ""+kalsymCallVerificationLayout.getPhoneNumber(), Toast.LENGTH_SHORT).show();
//            registerReceiver(phoneCallReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));
//            kalsymCallVerificationLayout.createConfirmationDialog(
//                    (dialog, i) -> {
//                        receiveVerificationCall(kalsymCallVerificationLayout.getPhoneNumber());
//                    }
//            );
////            receiveVerificationCall(kalsymCallVerificationLayout.getPhoneNumber());
//        });
//
//
//        if(
//                ContextCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
//                || ContextCompat.checkSelfPermission(this, READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
//                || ContextCompat.checkSelfPermission(this, ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED
//                || ContextCompat.checkSelfPermission(this, INTERNET) != PackageManager.PERMISSION_GRANTED
//        )
//        {
//            Toast.makeText(this, "permissions are not granted", Toast.LENGTH_SHORT).show();
//            ActivityCompat.requestPermissions(MainActivity.this, new String[] { READ_PHONE_STATE, READ_CALL_LOG, ANSWER_PHONE_CALLS, INTERNET }, 101);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FlashCallApplication.RESULT_REQUEST_CODE && data != null){
            int result = data.getIntExtra(FlashCallApplication.VERIFICATION_RESULT, -1);
            if(result == FlashCallApplication.RESULT_VERIFIED){

                // if verified
                mTestResult.setText("Verified");

            }else if(result == FlashCallApplication.RESULT_NOT_VERIFIED){

                // if not verified
                mTestResult.setText("Not Verified");

            }
        }
    }

    //
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        try{
//            unregisterReceiver(phoneCallReceiver);
//        }catch (IllegalArgumentException e){
//            Log.e(MainActivity.class.getName(), "onPause: ", e.getCause() );
//        }
//    }
//
//    public void receiveVerificationCall(String number){
//        kalsymCallVerificationLayout.showProgress();
//        String verifyEndpointUrl = "/verifyNumber";
//        AndroidNetworking.get(BASE_URL+verifyEndpointUrl)
//                .addQueryParameter("phoneNumber", number)
//                .addQueryParameter("refId", number)
//                .setTag("Call")
//                .setPriority(Priority.HIGH)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        kalsymCallVerificationLayout.hideProgress();
////                        try {
//////                            Toast.makeText(MainActivity.this, ""+response.getString("data"), Toast.LENGTH_SHORT).show();
////                        } catch (JSONException e) {
////                            Toast.makeText(MainActivity.this, "Empty", Toast.LENGTH_SHORT).show();
////                        }
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        kalsymCallVerificationLayout.hideProgress();
//                        Toast.makeText(MainActivity.this, "An Error has occurred", Toast.LENGTH_SHORT).show();
//                        mPhoneNumberLayout.setErrorEnabled(true);
//                        mPhoneNumberLayout.setError("Failed, Please try again !");
//                        Log.e(MainActivity.class.getName(), "onError: ", anError);
//                    }
//                });
//    }
}