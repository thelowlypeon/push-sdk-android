package com.vibes.vibes;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class NotificationFactoryTest {

    private NotificationFactory notificationFactory;
    private Context context;

    @Before
    public void setUp() throws Exception {
        this.context = RuntimeEnvironment.application.getBaseContext();
        this.notificationFactory = new NotificationFactory(context, new StubBitmapExtractor(), new StubSoundExtractor());
    }

    private Notification getNotificationFromPayload(HashMap<String, String> payload) {
        PushPayloadParser pushPayloadParser = new PushPayloadParser(payload);

        NotificationCompat.Builder builder = notificationFactory.getBuilder(pushPayloadParser, context);
        return builder.build();
    }


    @Test
    @Config(sdk = Build.VERSION_CODES.O)
    public void testCreateBuilderOreoAndAbove() throws Exception {
        HashMap<String, String> payload = new HashMap<>();

        Notification notification = getNotificationFromPayload(payload);

        assertNotNull(notification.getChannelId());
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.KITKAT)
    public void testCreateBuilderBelowOreo() throws Exception {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("sound", "ding");

        Notification notification = getNotificationFromPayload(payload);

        assertNotNull(notification.sound);
    }

    @Test
    public void testNoContentIntentSet() {
        HashMap<String, String> payload = new HashMap<>();

        Notification notification = getNotificationFromPayload(payload);

        PendingIntent pendingContentIntent = notification.contentIntent;

        Intent contentIntent = shadowOf(pendingContentIntent).getSavedIntent();

        assertEquals(VibesEvent.ACTION_PUSH_OPENED, contentIntent.getAction());
        assertEquals(payload, contentIntent.getSerializableExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA));
    }

    @Test
    public void testNoDeleteIntentSet() {
        HashMap<String, String> payload = new HashMap<>();

        Notification notification = getNotificationFromPayload(payload);

        PendingIntent pendingDeleteIntent = notification.deleteIntent;

        Intent deleteIntent = shadowOf(pendingDeleteIntent).getSavedIntent();

        assertEquals(VibesEvent.ACTION_PUSH_DISMISSED, deleteIntent.getAction());
        assertEquals(payload, deleteIntent.getSerializableExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA));
    }

}
