package com.vibes.vibes;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.vibes.vibes.tracking.TrackingData;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class StubVibesReceiver extends VibesReceiver {

    public PushPayloadParser pushReceivedModel = null;
    public PushPayloadParser pushOpenedModel = null;
    public PushPayloadParser pushDismissedModel = null;
    public PersistentEventStorageInterface eventStorage = null;
    public PersistentTrackingDataInterface trackingStorage = null;

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

    private PushPayloadParser getPushMessageFromIntent(Intent intent) {
        HashMap<String, String> map = (HashMap<String, String>) intent.getSerializableExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA);
        return new PushPayloadParser(map);
    }

    @Override
    protected void onPushReceived(Context context, PushPayloadParser pushModel) {
        pushReceivedModel = pushModel;
    }

    @Override
    protected void onPushOpened(Context context, PushPayloadParser pushModel) {
        pushOpenedModel = pushModel;
        if (eventStorage != null) {
            Event event = new Event(TrackedEventType.CLICKTHRU, pushModel.getEventsMap());
            eventStorage.persistEvents(Collections.singletonList(event));
        }
        if (trackingStorage != null) {
            final Gson gson = new Gson();
            String clientData = pushModel.getMap().get(PushPayloadParser.kClientAppData);
            final TrackingData trackingData = gson.fromJson(clientData, TrackingData.class);
            trackingData.setPersonId(UUID.randomUUID().toString());
            String jsonString = gson.toJson(trackingData, TrackingData.class);
            trackingStorage.persistTrackingData(jsonString);
        }
    }

    @Override
    protected void onPushDismissed(Context context, PushPayloadParser pushModel) {
        pushDismissedModel = pushModel;
    }

}

