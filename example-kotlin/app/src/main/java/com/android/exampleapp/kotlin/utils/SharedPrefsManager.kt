package com.android.exampleapp.kotlin.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager constructor(context: Context?) {
    init {
        mContext = context
    }

    /**
     * Store the token in the SharedPreferences
     * @param token: String
     */
    fun storeToken(token: String) {
        val preferences = mContext!!.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(KEY_ACCESS_TOKEN, token)
        editor.apply()
    }

    /**
     * Retrieve the token from the SharedPreferences.
     * @return String
     */
    fun getToken(): String {
        val preferences = mContext!!.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = preferences.getString(KEY_ACCESS_TOKEN, "NO TOKEN")
        print("Testing token "+token)
        return preferences.getString(KEY_ACCESS_TOKEN, "token")
    }

    companion object {
        private val SHARED_PREF_NAME = "FCMPrefs"
        private val KEY_ACCESS_TOKEN = "FBToken"
        @SuppressLint("StaticFieldLeak")
        var mContext: Context? = null
        @SuppressLint("StaticFieldLeak")
        private var mInstance: SharedPrefsManager? = null

        /**
         * Return the unique instance of SharedPrefsManager
         * @param context: Context
         * @return SharedPrefsManager
         */
        @Synchronized
        fun getInstance(context: Context?): SharedPrefsManager {
            if (mInstance == null) {
                mInstance = SharedPrefsManager(context)
            }
            return mInstance as SharedPrefsManager
        }
    }
}
