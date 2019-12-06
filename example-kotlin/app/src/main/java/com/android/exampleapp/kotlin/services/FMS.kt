package com.android.exampleapp.kotlin.services


import android.util.Log
import com.android.exampleapp.kotlin.BuildConfig
import com.android.exampleapp.kotlin.utils.SharedPrefsManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vibes.vibes.Vibes

class FMS : FirebaseMessagingService(){
    private val TAG = "c.v.aex.FMS"
    /**
     * @see FirebaseMessagingService.onMessageReceived
     */
    override fun onMessageReceived(message: RemoteMessage) {
        Vibes.getInstance().handleNotification(applicationContext, message.data)
    }

    override fun onNewToken(pushToken: String) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Firebase token refreshed")
        }
        super.onNewToken(pushToken)
        val sharedPrefsManager: SharedPrefsManager = SharedPrefsManager.getInstance(this)
        sharedPrefsManager.storeToken(pushToken)
    }
}
