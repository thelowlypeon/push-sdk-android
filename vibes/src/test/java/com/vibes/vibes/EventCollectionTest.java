package com.vibes.vibes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class EventCollectionTest {
    private EventCollection collection;

    @Before
    public void setUp() throws Exception {
        int count = 5;
        ArrayList<Event> events = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TrackedEventType type = TrackedEventType.LAUNCH;
            HashMap<String, String> properties = new HashMap<String, String>();
            properties.put("key", "value");
            Date timestamp = new Date();
            events.add(new Event(type, properties, timestamp));
        }
        collection = new EventCollection(events);
    }

    @Test
    public void getItems() throws Exception {
        Integer size = collection.getEvents().size();
        assertThat(5, is(equalTo(size)));
    }

    @Test
    public void isEmpty() throws Exception {
        boolean emptiness = collection.isEmpty();
        assertThat(false, is(emptiness));
        collection.getEvents().clear();

        emptiness = collection.isEmpty();
        assertThat(true, is(emptiness));

        collection = new EventCollection(null);
        emptiness = collection.isEmpty();
        assertThat(true, is(emptiness));
    }

    @Test
    public void addItem() throws Exception {
        TrackedEventType type = TrackedEventType.LAUNCH;
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("key", "value");
        Date timestamp = new Date();
        Event ev = new Event(type, properties, timestamp);
        collection.add(ev);

        Integer size = collection.getEvents().size();
        assertThat(6, is(equalTo(size)));
    }

    @Test
    public void addItemWhenEmpty() throws Exception {
        TrackedEventType type = TrackedEventType.LAUNCH;
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("key", "value");
        Date timestamp = new Date();
        Event ev = new Event(type, properties, timestamp);
        collection.getEvents().clear();
        collection.add(ev);

        Integer size = collection.getEvents().size();
        assertThat(1, is(equalTo(size)));
    }

    @Test
    public void addItemWhenNull() throws Exception {
        TrackedEventType type = TrackedEventType.LAUNCH;
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("key", "value");
        Date timestamp = new Date();
        Event ev = new Event(type, properties, timestamp);
        collection = new EventCollection(null);
        collection.add(ev);

    }

    @Test
    public void removeItem() throws Exception {
        Event ev = collection.getEvents().get(0);
        collection.remove(ev);

        Integer size = collection.getEvents().size();
        assertThat(4, is(equalTo(size)));
    }

    @Test
    public void removeItemWhenEmpty() throws Exception {
        Event ev = collection.getEvents().get(0);
        collection.getEvents().clear();
        collection.remove(ev);

        Integer size = collection.getEvents().size();
        assertThat(0, is(equalTo(size)));
    }

    @Test
    public void removeItemWhenNull() throws Exception {
        Event ev = collection.getEvents().get(0);
        collection = new EventCollection(null);
        collection.remove(ev);
    }

    @Test
    public void createInstance() throws Exception {
        EventCollection.EventCollectionObjectFactory factory = new EventCollection.EventCollectionObjectFactory();
        String serialize = factory.serialize(collection);
        final EventCollection instance = factory.createInstance(serialize);
        assertThat(collection.getEvents().size(), is(equalTo(instance.getEvents().size())));
        assertThat(collection.getEvents().get(0).getUUID(), is(instance.getEvents().get(0).getUUID()));
        assertThat(collection.getEvents().get(0).getUUID(), is(instance.getEvents().get(0).getUUID()));
        assertThat(collection.getEvents().get(0).getProperties(), is(instance.getEvents().get(0).getProperties()));
        assertThat(collection.getEvents().get(0).getTimestamp(), is(instance.getEvents().get(0).getTimestamp()));

    }

    @Test
    public void serializeNullEvents() throws Exception {
        EventCollection.EventCollectionObjectFactory factory = new EventCollection.EventCollectionObjectFactory();
        collection = new EventCollection(null);
        String serialize = factory.serialize(collection);
        JSONObject json = new JSONObject(serialize);
        JSONArray eventsJSON = json.getJSONArray("events");
        assertThat(0, is(eventsJSON.length()));

    }

    @Test
    public void serialize() throws Exception {
        EventCollection.EventCollectionObjectFactory factory = new EventCollection.EventCollectionObjectFactory();
        int index1 = 1;
        int index2 = 4;
        Event event1 = collection.getEvents().get(index1);
        Event event2 = collection.getEvents().get(index2);
        String id1 = event1.getUUID();
        String id2 = event2.getUUID();
        TrackedEventType type1 = event1.getType();
        TrackedEventType type2 = event2.getType();

        String serialize = factory.serialize(collection);
        JSONObject json = new JSONObject(serialize);
        JSONArray eventsJSON = json.getJSONArray("events");
        for (int i = 0; i < eventsJSON.length(); i++) {
            JSONObject eventObject = eventsJSON.getJSONObject(i);
            if (i == index1 ) {
                assertEvent(eventObject, event1);
            } else if (i == index2 ) {
                assertEvent(eventObject, event2);
            }
        }


    }

    private void assertEvent(JSONObject json, Event event) throws JSONException {
        assertThat(json.getString("uuid"), is(event.getUUID()));
        assertThat(json.getString("type"), is(event.getType().toString().toLowerCase()));
        assertThat(event.getProperties(), is(event.getProperties()));
        assertThat(json.getString("timestamp"), is(ISODateFormatter.toISOString(event.getTimestamp())));
    }
}