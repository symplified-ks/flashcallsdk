package com.symplified.phonecallverificationsdk;

import static android.Manifest.permission.ANSWER_PHONE_CALLS;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telecom.TelecomManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.symplified.phonecallverificationsdk.interfaces.FlashCallHandler;
import com.symplified.phonecallverificationsdk.interfaces.ICallReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class PhoneCallReceiver extends BroadcastReceiver implements Serializable {
    private final Class<?> className;
    private static final String BASE_URL = "http://18.217.223.180:2089";
    private static final int PERMISSION_REQUEST_CODE = 101;
    private MaterialAlertDialogBuilder mBuilder;
    private View successView;
    private AlertDialog mAlertDialog;
    private FlashCallHandler mHandler;
    public PhoneCallReceiver (Class<?> className, FlashCallHandler mHandler){
        this.className = className;
        this.mHandler = mHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mBuilder = new MaterialAlertDialogBuilder(context);
        successView = ((Activity) context).getLayoutInflater().inflate(R.layout.success_dialog, null);
        getRegisteredNumbers(context, intent);
    }


    private boolean endCall(Context context) {
        TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        boolean success = false;
        if (tm != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[] { ANSWER_PHONE_CALLS }, PERMISSION_REQUEST_CODE);
                }
                success = tm.endCall();
            }
        }
        return success;
    }

    public List<String> getRegisteredNumbers(Context context, Intent intent){
        String getNumbersUrl = "/getNumbers";
        List<String> registeredNumbers = new ArrayList<>();
        AndroidNetworking.get(BASE_URL + getNumbersUrl)
                .addQueryParameter("refId", UUID.randomUUID().toString())
                .setTag("getNumbers")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray numbersJsonArray = response.getJSONArray("data");
                            registeredNumbers.addAll(stripCountryCode(numbersJsonArray, context));
//                            Toast.makeText(context, "phone number length "+ registeredNumbers.size(), Toast.LENGTH_SHORT).show();
                            Log.e("ERR", "onResponse: "+ registeredNumbers.toString());
                            verify(context, intent, registeredNumbers);

                        } catch (JSONException e) {
                            Log.e(PhoneCallReceiver.class.getName(), "onResponse: ", e);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(context, "An error has occurred. Please try again !", Toast.LENGTH_SHORT).show();
                        Log.e(PhoneCallReceiver.class.getName(), "onError: ", anError);
                    }
                });
        return registeredNumbers;
    }

    private void verify(Context context, Intent intent, List<String> registeredNumbers) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.createInstance(context);
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

//        Toast.makeText(context, "registered number length is "+registeredNumbers.size(), Toast.LENGTH_SHORT).show();

//        Toast.makeText(context, state, Toast.LENGTH_SHORT).show();
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Phonenumber.PhoneNumber phoneNumber = null;
            try {
                if(incomingNumber != null){
                    if(incomingNumber.charAt(0) != '+' || incomingNumber.charAt(0) != '0')
                    {
                        incomingNumber = "+" + incomingNumber;
                    }
                    incomingNumber = incomingNumber.replaceFirst("^0+(?!$)", "+");
                    phoneNumber = phoneNumberUtil.parse(incomingNumber, Locale.getDefault().getCountry());
                    Toast.makeText(context, "" + phoneNumber.getNationalNumber(), Toast.LENGTH_LONG).show();
                    Log.e("ERR", "verify: "+ phoneNumber.getNationalNumber());
                }
            } catch (NumberParseException e) {
                e.printStackTrace();
            }

            if (phoneNumber != null
                    && registeredNumbers.contains(String.valueOf(phoneNumber.getNationalNumber()))) {
                boolean result = endCall(context);
                if(result){
                    Toast.makeText(context, "VERIFIED", Toast.LENGTH_SHORT).show();
                    onSuccessDialog(context, result);
                }else {
                    Toast.makeText(context, "Try Again !", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private List<String> stripCountryCode(JSONArray numbersJsonArray, Context context) {
        List<String> registeredNumbers = new ArrayList<>();
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.createInstance(context);
        List<String> numbersWithoutCountryCode = new ArrayList<>();
        try{
            for(int i = 0; i< numbersJsonArray.length(); i++){
                registeredNumbers.add((String )numbersJsonArray.get(i));
            }
            for (String number : registeredNumbers){
                numbersWithoutCountryCode.add(String.valueOf(phoneNumberUtil.parse(number, Locale.getDefault().getCountry()).getNationalNumber()));
            }
        }catch (NumberParseException | JSONException e) {
            e.printStackTrace();
        }
        return numbersWithoutCountryCode;
    }

    private void onSuccessDialog(Context context, boolean result){
        Button button = successView.findViewById(R.id.success_button);
        button.setOnClickListener(view -> {
            Log.e("ERR", "onReceive: " + mAlertDialog.hashCode());
            hideDialog(context, result);
            ((Activity) context).finish();
        });
        mAlertDialog = mBuilder.create();
//        mBuilder.setView(successView);
        mAlertDialog.setView(successView);
        mAlertDialog.show();
        Log.e("ERR", "onSuccessDialog: " + mAlertDialog.hashCode() );
    }

    private void hideDialog(Context context, boolean result){
        mAlertDialog.dismiss();
        Intent intentToHome = new Intent(context, className);
        Intent returnIntent = new Intent();
        if(result){
            returnIntent.putExtra("result", 1);
        }
        else
            returnIntent.putExtra("result", 0);
//        returnIntent.putExtra("result", result);
//        context.startActivity(intentToHome);
        ((Activity) context).setResult(FlashCallApplication.RESULT_REQUEST_CODE, returnIntent);
        ((Activity) context).finish();
    }
}
