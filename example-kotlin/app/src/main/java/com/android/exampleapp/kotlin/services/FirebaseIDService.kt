package com.android.exampleapp.kotlin.services

import android.util.Log
import com.android.exampleapp.kotlin.utils.SharedPrefsManager
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class FirebaseIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        SharedPrefsManager.getInstance(this).storeToken(refreshedToken)
        Log.d(TAG, "--> Token Refreshed: " + refreshedToken!!)
    }

    companion object {

        private val TAG = "FirebaseIDService"
    }
}