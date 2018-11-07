package com.vibes.androidsdkexampleapp.notification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vibes.androidsdkexampleapp.activities.DeepLinkActivity;
import com.vibes.androidsdkexampleapp.activities.VibesActivity;
import com.vibes.vibes.PushPayloadParser;
import com.vibes.vibes.Vibes;
import com.vibes.vibes.VibesReceiver;

public class NotificationBroadcastReceiver extends VibesReceiver {

    @Override
    protected void onPushOpened(Context context, PushPayloadParser pushModel) {
        super.onPushOpened(context, pushModel);
        Log.d("OPENED", "Push was opened");
        if (pushModel.getDeepLink() != null) {
            Intent deepLinkIntent = new Intent(context, DeepLinkActivity.class);
            deepLinkIntent.putExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA, pushModel.getMap());
            context.startActivity(deepLinkIntent);
        } else {
            Intent mainActivityIntent = new Intent(context, VibesActivity.class);
            mainActivityIntent.putExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA, pushModel.getMap());
            context.startActivity(mainActivityIntent);
        }
    }

    @Override
    protected void onPushDismissed(Context context, PushPayloadParser pushModel) {
        super.onPushDismissed(context, pushModel);
        Log.d("DISMISSED", "Push was dismissed");
    }

    @Override
    protected void onPushReceived(Context context, PushPayloadParser pushModel) {
        super.onPushReceived(context, pushModel);
        Log.d("RECEIVED", "Push was received");
    }

}
