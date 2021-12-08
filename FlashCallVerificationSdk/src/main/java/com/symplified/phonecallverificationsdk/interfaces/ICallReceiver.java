package com.symplified.phonecallverificationsdk.interfaces;

import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;

import java.util.List;

public interface ICallReceiver {
    public boolean endCall(Context context);
    public List<String> getRegisteredNumbers(Context context, Intent intent);
    public void verify(Context context, Intent intent, List<String> registeredNumbers);
    public List<String> stripCountryCode(JSONArray numbersJsonArray, Context context);
}
