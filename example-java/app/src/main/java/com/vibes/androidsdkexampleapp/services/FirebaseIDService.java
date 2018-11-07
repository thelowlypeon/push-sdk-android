package com.vibes.androidsdkexampleapp.services;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.vibes.androidsdkexampleapp.model.SharedPrefsManager;

/**
 * Created by jean-michel.barbieri on 2/25/18
 * Copyright (c) Vibes 2018 . All rights reserved.
 * Last modified 12:33 AM
 */
public class FirebaseIDService extends FirebaseInstanceIdService {

    private final static String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedPrefsManager.getInstance(this).storeToken(refreshedToken);
        Log.d(TAG, "--> Token Refreshed: " + refreshedToken);
    }
}
