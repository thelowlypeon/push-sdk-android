package com.android.exampleapp.kotlin.api

import com.vibes.vibes.Credential
import com.vibes.vibes.VibesListener

interface VibesAPIContract {
    fun registerDevice(listener: VibesListener<Credential>)
    fun unregisterDevice(listener: VibesListener<Void?>)
    fun registerPush(listener: VibesListener<Void?>, firebasePushToken: String)
    fun unregisterPush(listener: VibesListener<Void?>)
}
