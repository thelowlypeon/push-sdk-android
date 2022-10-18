package com.android.exampleapp.kotlin.notification

import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.exampleapp.kotlin.DeepLinkActivity
import com.android.exampleapp.kotlin.MainActivity
import com.vibes.vibes.PushPayloadParser
import com.vibes.vibes.Vibes
import com.vibes.vibes.VibesReceiver

/**
 * Receiver implementation that can be declared within AndroidManifest.xml file to handle receipt
 * and processing of push notifications.
 *
 * @author edem.morny@vibes.com
 */
class NotificationBroadcastReceiver: VibesReceiver() {

    override fun onPushOpened(context: Context?, pushModel: PushPayloadParser?) {
        super.onPushOpened(context, pushModel)
        Log.d("OPENED", "Push was opened")
        if (pushModel?.getDeepLink() != null) {
            val deepLinkIntent = Intent(context, DeepLinkActivity::class.java)
            deepLinkIntent.putExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA, pushModel.getMap())
            context?.startActivity(deepLinkIntent)
        } else {
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            mainActivityIntent.putExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA, pushModel?.getMap())
            context?.startActivity(mainActivityIntent)
        }
    }

    override fun onPushDismissed(context: Context, pushModel: PushPayloadParser) {
        super.onPushDismissed(context, pushModel)
        Log.d("DISMISSED", "Push was dismissed")
    }

    override fun onPushReceived(context: Context, pushModel: PushPayloadParser) {
        super.onPushReceived(context, pushModel)
        Log.d("RECEIVED", "Push was received")
    }
}