package com.vibes.vibes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PersistentEventStorageTest {
    PersistentEventStorage eventStorage;

    @Before
    public void setup() throws Exception {
        LocalStorage storage = new LocalStorage(new StubLocalStorageAdapter());
        this.eventStorage = new PersistentEventStorage(storage);
    }

    @Test
    public void storedEventsWhenEmpty() throws Exception {
        EventCollection collection = eventStorage.storedEvents();
        assertThat(collection.getEvents().size(), is(0));
    }

    @Test
    public void storedEventWhenNotEmpty() throws Exception {
        HashMap<String, String> properties = new HashMap<String, String>();
        Event event = new Event(TrackedEventType.LAUNCH, properties, new Date());
        eventStorage.persistEvents(Arrays.asList(event));

        EventCollection collection = eventStorage.storedEvents();
        assertThat(collection.getEvents().size(), is(1));
        assertThat(collection.getEvents().get(0), is(event));
    }

    @Test
    public void persistEvent() throws Exception {
        HashMap<String, String> properties = new HashMap<String, String>();
        Event event1 = new Event(TrackedEventType.LAUNCH, properties, new Date());
        Event event2 = new Event(TrackedEventType.LAUNCH, properties, new Date());
        eventStorage.persistEvents(Arrays.asList(event1, event2));

        EventCollection collection = eventStorage.storedEvents();
        assertThat(collection.getEvents().size(), is(2));
        assertThat(collection.getEvents().get(0), is(event1));
        assertThat(collection.getEvents().get(1), is(event2));
    }

    @Test
    public void removeEvent() throws Exception {
        HashMap<String, String> properties = new HashMap<String, String>();
        Event event1 = new Event(TrackedEventType.LAUNCH, properties, new Date());
        Event event2 = new Event(TrackedEventType.LAUNCH, properties, new Date());
        this.eventStorage.persistEvents(Arrays.asList(event1, event2));

        assertThat(this.eventStorage.storedEvents().getEvents().size(), is(2));

        this.eventStorage.removeEvent(event2);

        assertThat(this.eventStorage.storedEvents().getEvents().size(), is(1));
        assertThat(this.eventStorage.storedEvents().getEvents().get(0), is(event1));
    }

    @Test
    public void removeEvents() throws Exception {
        HashMap<String, String> properties = new HashMap<String, String>();
        Event event1 = new Event(TrackedEventType.LAUNCH, properties, new Date());
        Event event2 = new Event(TrackedEventType.LAUNCH, properties, new Date());
        this.eventStorage.persistEvents(Arrays.asList(event1, event2));

        EventCollection collection = eventStorage.storedEvents();
        assertThat(this.eventStorage.storedEvents().getEvents().size(), is(2));

        this.eventStorage.removeEvents(collection.getEvents());
        assertThat(this.eventStorage.storedEvents().getEvents().size(), is(0));
    }
}
