package com.vibes.androidsdkexampleapp.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

//import com.vibes.androidsdkexampleapp.BuildConfig;
import com.vibes.androidsdkexampleapp.model.SharedPrefsManager;
import com.vibes.vibes.Vibes;


/**
 * Created by jean-michel.barbieri on 2/25/18
 * Copyright (c) Vibes 2018 . All rights reserved.
 * Last modified 12:33 AM
 */
public class FMS extends FirebaseMessagingService {
    private static final String TAG = "c.v.aex.FMS";
    /**
     * @see FirebaseMessagingService#onMessageReceived(RemoteMessage)
     */

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Vibes.getInstance().handleNotification(getApplicationContext(), message.getData());
    }

    @Override
    public void onNewToken(String pushToken) {
        if(BuildConfig.DEBUG){
            Log.i(TAG,"Firebase token refreshed");
        }
        super.onNewToken(pushToken);
        SharedPrefsManager sharedPrefsManager = SharedPrefsManager.getInstance(this);
        sharedPrefsManager.storeToken(pushToken);

    }

}

