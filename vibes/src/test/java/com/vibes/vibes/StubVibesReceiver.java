package com.vibes.vibes;

import android.content.Context;

public class StubVibesReceiver extends VibesReceiver {

    public PushPayloadParser pushReceivedModel = null;
    public PushPayloadParser pushOpenedModel = null;
    public PushPayloadParser pushDismissedModel = null;

    @Override
    protected void onPushReceived(Context context, PushPayloadParser pushModel) {
        pushReceivedModel = pushModel;
    }

    @Override
    protected void onPushOpened(Context context, PushPayloadParser pushModel) {
        pushOpenedModel = pushModel;
    }

    @Override
    protected void onPushDismissed(Context context, PushPayloadParser pushModel) {
        pushDismissedModel = pushModel;
    }

}

