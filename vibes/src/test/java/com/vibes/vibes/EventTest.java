package com.vibes.vibes;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;
import java.util.HashMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 18)
public class EventTest {
    private Event event;

    @Before
    public void setUp() throws Exception {
        TrackedEventType type = TrackedEventType.LAUNCH;
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("key", "value");
        Date timestamp = new Date();
        this.event = new Event(type, properties, timestamp);
    }

    @Test
    public void serialize() throws Exception {
        JSONObject json = event.serialize();
        assertThat(json.getString("uuid"), is(event.getUUID()));
        assertThat((String) json.getJSONObject("attributes").get("key"), is("value"));
        assertThat(json.getString("type"), is(event.getType().toString().toLowerCase()));
        assertThat(json.getString("timestamp"), is(ISODateFormatter.toISOString(event.getTimestamp())));
    }

    @Test
    public void createInstance() throws Exception {
        String uuid = "uuid-value";
        String type = "launch";
        Date timestamp = new Date();
        HashMap<String, String> atts = new HashMap<String, String>();
        atts.put("key", "value");

        JSONObject json = new JSONObject();
        json.put("uuid", uuid);
        json.put("type", type);
        json.put("timestamp", ISODateFormatter.toISOString(timestamp));
        JSONObject attributes = new JSONObject(atts);
        json.put("attributes", attributes);

        Event event = Event.createInstance(json.toString());

        assertThat(json.getString("uuid"), is(event.getUUID()));
        assertThat(json.getString("type"), is(event.getType().toString().toLowerCase()));
        assertThat(atts, is(event.getProperties()));
        assertThat(json.getString("timestamp"), is(ISODateFormatter.toISOString(event.getTimestamp())));
    }

    @Test
    public void equality() throws Exception {
        Event event1 = new Event(TrackedEventType.LAUNCH, null, new Date());
        Event event2 = new Event(TrackedEventType.LAUNCH, null, new Date());
        assertThat(event1, is(not(event2)));
        assertThat(event1, is(event1));
    }
}