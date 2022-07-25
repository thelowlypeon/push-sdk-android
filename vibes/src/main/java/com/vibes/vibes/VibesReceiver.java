package com.vibes.vibes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vibes.vibes.logging.LogObject;

import java.util.Collections;
import java.util.HashMap;

public class VibesReceiver extends BroadcastReceiver {

    /**
     * Receives Vibes events related to push notifications.
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action == null) return;

        PushPayloadParser pushModel = getPushMessageFromIntent(intent);

        switch (action) {
            case VibesEvent.ACTION_PUSH_RECEIVED:
                onPushReceived(context, pushModel);
                break;
            case VibesEvent.ACTION_PUSH_OPENED:
                onPushOpened(context, pushModel);
                break;
            case VibesEvent.ACTION_PUSH_DISMISSED:
                onPushDismissed(context, pushModel);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private PushPayloadParser getPushMessageFromIntent(Intent intent) {
        HashMap<String, String> map = (HashMap<String, String>) intent.getSerializableExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA);
        return new PushPayloadParser(map);
    }

    protected void onPushReceived(Context context, PushPayloadParser pushModel) {

    }

    protected void onPushOpened(Context context, PushPayloadParser pushModel) {
        Vibes.getCurrentLogger().log(new LogObject(VibesLogger.Level.INFO, "onPushOpened action raised. Reporting a clickthru event"));
        Event event  = new Event(TrackedEventType.CLICKTHRU, pushModel.getEventsMap());
        Vibes.getInstance().trackEvents(Collections.singletonList(event));
    }

    protected void onPushDismissed(Context context, PushPayloadParser pushModel) {

    }

}
