package com.vibes.vibes;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * A local storage class that is backed by SharedPreferences.
 **/
class SharedPreferencesStorageAdapter implements LocalStorageAdapter {
    private static final String IDENTIFIER = "VibesStorage";
    private static final String DEFAULT_VALUE = "THIS IS THE DEFAULT VALUE";

    private SharedPreferences preferences;

    /**
     * Initialize this object
     * @param context an application Context
     */
    public SharedPreferencesStorageAdapter(Context context) {
        this.preferences = context.getSharedPreferences(IDENTIFIER, MODE_PRIVATE);
    }

    /**
     * Gets a String value from SharedPreferences.
     * @param key the key of the value to find
     */
    @Override
    public String get(String key) {
        String result = preferences.getString(key, DEFAULT_VALUE);
        if (result.equals(DEFAULT_VALUE)) {
            return null;
        }
        return result;
    }

    /**
     * Puts a String value in SharedPreferences.
     * @param key the key of the value to store
     * @param value the String value to actually store
     */
    @Override
    public void put(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    /**
     * Removes a String value from SharedPreferences.
     * @param key the key of the value to remove
     */
    @Override
    public void remove(String key) {
        preferences.edit().remove(key).apply();
    }
}