package com.android.exampleapp.kotlin.api

import android.content.Context
import android.util.Log
import com.android.exampleapp.kotlin.notification.MyNotificationFactory
import com.vibes.vibes.Credential
import com.vibes.vibes.Vibes
import com.vibes.vibes.VibesListener

class VibesAPI: VibesAPIContract {

    constructor(context: Context){
        Vibes.getInstance().setNotificationFactory(MyNotificationFactory(context))
    }

    override fun registerDevice(listener: VibesListener<Credential>) {
        Vibes.getInstance().registerDevice(listener)
    }

    override fun unregisterDevice(listener: VibesListener<Void>) {
        Vibes.getInstance().unregisterDevice(listener)
    }

    override fun registerPush(listener: VibesListener<Void>, firebasePushToken: String) {
        Log.d("Controller", "--> Controller Firebase Token: $firebasePushToken")
        Vibes.getInstance().registerPush(firebasePushToken, listener)
    }

    override fun unregisterPush(listener: VibesListener<Void>) {
        Vibes.getInstance().unregisterPush(listener)
    }
}