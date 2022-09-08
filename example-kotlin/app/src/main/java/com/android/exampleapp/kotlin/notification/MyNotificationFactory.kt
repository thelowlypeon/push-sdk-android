package com.android.exampleapp.kotlin.notification

import android.content.Context
import androidx.core.app.NotificationCompat
import com.vibes.vibes.NotificationFactory
import com.vibes.vibes.PushPayloadParser
import com.android.exampleapp.kotlin.R;

/**
 * A factory that builds notifications received from Vibes Push Service
 *
 * @author edem.morny@vibes.com
 */
class MyNotificationFactory : NotificationFactory {

    constructor(context: Context?) : super(context)

    override fun getBuilder(pushModel:PushPayloadParser, context:Context): NotificationCompat.Builder{
        return super.getBuilder(pushModel, context).setSmallIcon(R.drawable.firebase_icon)
    }
}