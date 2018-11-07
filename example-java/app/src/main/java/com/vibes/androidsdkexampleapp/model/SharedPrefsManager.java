package com.vibes.androidsdkexampleapp.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jean-michel.barbieri on 2/25/18
 * Copyright (c) Vibes 2018 . All rights reserved.
 * Last modified 12:33 AM
 */
public class SharedPrefsManager{
    private static final String SHARED_PREF_NAME = "FCMPrefs";
    private static final String KEY_ACCESS_TOKEN = "FBToken";
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static SharedPrefsManager mInstance;

    private SharedPrefsManager(Context context) {
        mContext = context;
    }

    /**
     * Return the unique instance of SharedPrefsManager
     * @param context: Context
     * @return SharedPrefsManager
     */
    public static synchronized SharedPrefsManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefsManager(context);
        }
        return mInstance;
    }

    /**
     * Store the token in the SharedPreferences
     * @param token: String
     */
    public void storeToken(String token) {
        SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
    }

    /**
     * Retrieve the token from the SharedPreferences.
     * @return String
     */
    public String getToken() {
        SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_ACCESS_TOKEN, null);
    }
}
