package com.symplified.phonecallverificationsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.symplified.phonecallverificationsdk.interfaces.FlashCallHandler;

import java.io.Serializable;

public class FlashCallApplication {
    private Context mContext;
    private Class<?> mNextActivity;
    private String mToken;
    private FlashCallHandler mFlashCallHandler;
    public static final int RESULT_REQUEST_CODE = 9;
    public static final int RESULT_VERIFIED = 1;
    public static final int RESULT_NOT_VERIFIED = 0;
    public static final String VERIFICATION_RESULT = "result";
//    private PhoneCallReceiver mReceiver;

    private FlashCallApplication(Context mContext, Class<?> mNextActivity, String mToken) {
        this.mContext = mContext;
        this.mNextActivity = mNextActivity;
        this.mToken = mToken;
//        this.mFlashCallHandler = flashCallHandler;
//        this.mReceiver = new PhoneCallReceiver(mNextActivity);
    }

    public static void init(Context mContext, Class<?> mNextActivity, String mToken){
        FlashCallApplication app = new FlashCallApplication(mContext, mNextActivity, mToken);
        Intent intentToVerification = new Intent(mContext, VerificationActivity.class);
        intentToVerification.putExtra("next", mNextActivity);
//        intentToVerification.putExtra("receiver", new PhoneCallReceiver(mNextActivity, flashCallHandler));
//        ((Activity) mContext).startActivity(intentToVerification);
        ((Activity) mContext).startActivityForResult(intentToVerification, RESULT_REQUEST_CODE);
    }

}
