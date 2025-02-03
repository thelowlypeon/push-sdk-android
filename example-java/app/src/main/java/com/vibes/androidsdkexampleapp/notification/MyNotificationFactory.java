package com.vibes.androidsdkexampleapp.notification;

import android.content.Context;
import androidx.core.app.NotificationCompat;

import com.vibes.vibes.NotificationFactory;
import com.vibes.vibes.PushPayloadParser;
import com.vibes.androidsdkexampleapp.R;

public class MyNotificationFactory extends NotificationFactory {
    public MyNotificationFactory(Context context) {
        super(context);
    }

    /**
     * Customize the notification built
     */
    @Override
    public NotificationCompat.Builder getBuilder(PushPayloadParser pushModel, Context context) {
        return super.getBuilder(pushModel, context)
                .setSmallIcon(R.drawable.firebase_icon);
    }

}
