package com.symplified.phonecallverificationsdk.interfaces;

import java.io.Serializable;

public interface FlashCallHandler extends Serializable {
    public void onSuccess();
    public void onFailure();
}
