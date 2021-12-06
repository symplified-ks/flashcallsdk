package com.symplified.phonecallverification;

import static android.Manifest.permission.ANSWER_PHONE_CALLS;
import static android.Manifest.permission.READ_CALL_LOG;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.symplified.phonecallverificationsdk.PhoneCallReceiver;

public class MainActivity extends AppCompatActivity {

    private PhoneCallReceiver phoneCallReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneCallReceiver = new PhoneCallReceiver(HomeActivity.class);
        if(
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this, ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "permissions are not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.READ_PHONE_STATE, READ_CALL_LOG, ANSWER_PHONE_CALLS }, 101);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(phoneCallReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(phoneCallReceiver);
    }
}