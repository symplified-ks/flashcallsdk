package com.symplified.phonecallverificationsdk;

import static android.Manifest.permission.ANSWER_PHONE_CALLS;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class PhoneCallReceiver extends BroadcastReceiver {
    private final Class<?> className;
    public PhoneCallReceiver (Class<?> className){
        this.className = className;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        List<String> registeredNumbers = new ArrayList<>();
        registeredNumbers.add("+17173667237");
        registeredNumbers.add("+12019890471");
//        Toast.makeText(context, state, Toast.LENGTH_SHORT).show();
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
//            Toast.makeText(context, "" + incomingNumber, Toast.LENGTH_LONG).show();
            if (incomingNumber != null
                    && registeredNumbers.contains(incomingNumber)) {
                boolean result = endCall(context);
                if(result){
                    Toast.makeText(context, "VERIFIED", Toast.LENGTH_SHORT).show();

                    Intent intentToHome = new Intent(context, className);
                    context.startActivity(intentToHome);

                    ((Activity) context).finish();
                }
            }
        }
    }

    private boolean endCall(Context context) {
        TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        boolean success = false;
        if (tm != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[] { ANSWER_PHONE_CALLS }, 101);
                }
                success = tm.endCall();
            }
        }
        return success;
    }
}
