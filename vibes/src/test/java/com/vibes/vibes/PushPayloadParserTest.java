package com.vibes.vibes;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.Build;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class PushPayloadParserTest extends TestConfig {
    Map map;
    PushPayloadParser pushPayloadParser;

    @Before
    public void setUp() throws Exception {
        Map map = new TreeMap();
        map.put("message_uid", "069ef69b-6d07-de1e-a669-77cfa2d1413a");
        map.put("body", "Smart content goes here.");
        map.put("title", "Title");
        map.put("badge", "10");
        map.put("sound", "notif_sound.mp3");
        map.put("click_action", "VIBES_CHANNEL");
        map.put("priority", "high");
        map.put("vibes_collapse_id", "1223456790");
        map.put("client_app_data",
                "{\"deep_link\"=>\"XXXXXXX\",\"media_url\"=>\"https://www.google.com/images/" +
                        "branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\"activity_type" +
                        "\"=>\"Broadcast\", \"activity_uid\"=>\"0b165b2a\", \"inbox_message_uid\"=>\"0bqw123\"}"
        );
        this.map = map;
        this.pushPayloadParser = new PushPayloadParser(map);
    }

    @Test
    public void retrievesDeepLink() throws Exception {
        assertThat(pushPayloadParser.getDeepLink(), is("XXXXXXX"));
    }

    @Test
    public void retrieveActivityType() {
        assertThat(pushPayloadParser.getActivityType(), is("Broadcast"));
    }

    @Test
    public void retrieveActivityUid() {
        assertThat(pushPayloadParser.getActivityUid(), is("0b165b2a"));
    }

    @Test
    public void retrieveInboxMessageUid() {
        assertThat(pushPayloadParser.getInboxMessageUid(), is("0bqw123"));
    }

    @Test
    public void retrieveMessageUid() {
        assertThat(pushPayloadParser.getMessageUid(), is("069ef69b-6d07-de1e-a669-77cfa2d1413a"));
    }

    @Test
    public void retrievesMediaUrl() throws Exception {
        assertThat(pushPayloadParser.getRichPushMediaURL(), is("https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png"));
    }

    @Test
    public void retrievesTitle() throws Exception {
        assertThat(pushPayloadParser.getTitle(), is("Title"));
    }

    @Test
    public void retrievesBody() throws Exception {
        assertThat(pushPayloadParser.getBody(), is("Smart content goes here."));
    }

    @Test
    public void retrievesBadgeNumber() throws Exception {
        assertEquals(new Integer(10), pushPayloadParser.getBadgeNumber());
    }

    @Test
    public void handlesNullBadgeNumber() throws Exception {
        map.remove("badge");
        this.pushPayloadParser = new PushPayloadParser(map);
        assertEquals(null, pushPayloadParser.getBadgeNumber());
    }

    @Test
    public void handlesNonIntegerBadgeNumber() throws Exception {
        map.put("badge", "abc");
        this.pushPayloadParser = new PushPayloadParser(map);
        assertEquals(null, pushPayloadParser.getBadgeNumber());
    }

    @Test
    public void retrievesMinPriority() throws Exception {
        map.put("priority", "normal");
        this.pushPayloadParser = new PushPayloadParser(map);
        assertEquals(Notification.PRIORITY_DEFAULT, (int) pushPayloadParser.getPriority());
    }
    @Test
    public void retrievesHighPriority() throws Exception {
        map.put("priority", "high");
        this.pushPayloadParser = new PushPayloadParser(map);
        assertEquals(Notification.PRIORITY_HIGH, (int) pushPayloadParser.getPriority());
    }

    @Test
    public void retrievesDefaultImportance() throws Exception {
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 26);
        map.put("priority", "normal");
        this.pushPayloadParser = new PushPayloadParser(map);
        assertEquals(NotificationManager.IMPORTANCE_DEFAULT, (int) pushPayloadParser.getPriority());
    }

    @Test
    public void retrievesHighImportance() throws Exception {
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 26);
        map.put("priority", "high");
        this.pushPayloadParser = new PushPayloadParser(map);
        assertEquals(NotificationManager.IMPORTANCE_HIGH, (int) pushPayloadParser.getPriority());
    }

    @Test
    public void retrievesNullWhenImportanceNotRecognized() throws Exception {
        map.put("priority", "random string");
        this.pushPayloadParser = new PushPayloadParser(map);
        assertEquals(null, pushPayloadParser.getPriority());
    }

    @Test
    public void retrievesChannel() throws Exception {
        assertThat(pushPayloadParser.getChannel(), is("VIBES_CHANNEL"));
    }

    @Test
    public void retrievesVibesCollapseId() {
        assertThat(pushPayloadParser.getVibesCollapseId(), is("1223456790"));
    }

    @Test
    public void retrievesNullVibesCollapseId() {
        map.remove("vibes_collapse_id");
        this.pushPayloadParser = new PushPayloadParser(map);
        assertThat(pushPayloadParser.getVibesCollapseId(), is(nullValue()));
    }

    @Test
    public void retrievesSoundNoExt() {
        assertThat(pushPayloadParser.getSoundNoExt(), is("notif_sound"));
    }

    @Test
    public void retrievesSoundNoExtInvExtension() {
        String soundName = "notif_sound.vlc";
        map.put("sound", soundName);
        this.pushPayloadParser = new PushPayloadParser(map);
        assertThat(pushPayloadParser.getSoundNoExt(), is(soundName));
    }

    @Test
    public void testEventsMap() {
        HashMap<String, String> testEventsMap = new HashMap<>();
        testEventsMap.put("message_uid", "069ef69b-6d07-de1e-a669-77cfa2d1413a");
        testEventsMap.put("activity_type", "Broadcast");
        testEventsMap.put("activity_uid", "0b165b2a");
        assertEquals(testEventsMap, pushPayloadParser.getEventsMap());
    }

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        field.set(null, newValue);
    }
}
