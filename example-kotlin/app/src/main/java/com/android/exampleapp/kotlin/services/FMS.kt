package com.android.exampleapp.kotlin.services


import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vibes.vibes.Vibes

class FMS : FirebaseMessagingService(){
    /**
     * @see FirebaseMessagingService.onMessageReceived
     */
    override fun onMessageReceived(message: RemoteMessage?) {
        Vibes.getInstance().handleNotification(applicationContext, message!!.data)
    }
}
