package com.vibes.vibes;


import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.HashMap;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class VibesReceiverTest {

    private StubVibesReceiver stubVibesReceiver;
    private HashMap<String, String> payload;
    private Context context;

    @Before
    public void setUp() throws Exception {
        stubVibesReceiver = new StubVibesReceiver();
        context = RuntimeEnvironment.application.getBaseContext();

        payload = new HashMap<>();
        payload.put("sound", "ding");
    }

    @Test
    public void testPushOpened() throws Exception {
        Intent intent = new Intent(VibesEvent.ACTION_PUSH_OPENED)
                .putExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA, payload);

        stubVibesReceiver.onReceive(context, intent);
        assertNull(stubVibesReceiver.pushDismissedModel);
        assertNull(stubVibesReceiver.pushReceivedModel);
        assertNotNull(stubVibesReceiver.pushOpenedModel);
    }

    @Test
    public void testPushDismissed() throws Exception {
        Intent intent = new Intent(VibesEvent.ACTION_PUSH_DISMISSED)
                .putExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA, payload);

        stubVibesReceiver.onReceive(context, intent);

        assertNotNull(stubVibesReceiver.pushDismissedModel);
        assertNull(stubVibesReceiver.pushReceivedModel);
        assertNull(stubVibesReceiver.pushOpenedModel);
    }

    @Test
    public void testPushReceived() throws Exception {
        Intent intent = new Intent(VibesEvent.ACTION_PUSH_RECEIVED)
                .putExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA, payload);

        stubVibesReceiver.onReceive(context, intent);

        assertNull(stubVibesReceiver.pushDismissedModel);
        assertNotNull(stubVibesReceiver.pushReceivedModel);
        assertNull(stubVibesReceiver.pushOpenedModel);
    }
}

