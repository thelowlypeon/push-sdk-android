package com.vibes.vibes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
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
    public void fetchEventsByType() throws Exception {
        HashMap<String, String> properties = new HashMap<String, String>();
        Event event1 = new Event(TrackedEventType.INBOX_OPEN, properties, new Date());
        Event event2 = new Event(TrackedEventType.LAUNCH, properties, new Date());
        Event event3 = new Event(TrackedEventType.LAUNCH, properties, new Date());
        eventStorage.persistEvents(Arrays.asList(event1, event2, event3));

        EventCollection collection = eventStorage.storedEvents(TrackedEventType.LAUNCH);
        assertThat(collection.getEvents().size(), is(2));
        assertThat(collection.getEvents().get(0), is(event2));
        assertThat(collection.getEvents().get(1), is(event3));
    }

    @Test
    public void sortFetchedEventsByType() throws Exception {
        Calendar elevenAm = Calendar.getInstance();
        elevenAm.set(Calendar.HOUR_OF_DAY, 11);

        Calendar twelvePm = Calendar.getInstance();
        twelvePm.set(Calendar.HOUR_OF_DAY, 12);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(Calendar.DAY_OF_YEAR, tomorrow.get(Calendar.DAY_OF_YEAR) + 1);

        HashMap<String, String> properties = new HashMap<String, String>();
        Event event1 = new Event(TrackedEventType.INBOX_OPEN, properties, new Date());
        Event event2 = new Event(TrackedEventType.LAUNCH, properties, elevenAm.getTime());
        Event event3 = new Event(TrackedEventType.LAUNCH, properties, twelvePm.getTime());
        Event event4 = new Event(TrackedEventType.LAUNCH, properties, tomorrow.getTime());
        Event event5 = new Event(TrackedEventType.LAUNCH, properties, elevenAm.getTime());
        eventStorage.persistEvents(Arrays.asList(event4, event1, event3, event2, event5));

        EventCollection launches = eventStorage.storedEvents(TrackedEventType.LAUNCH);

        assertThat(event5, is(launches.getEvents().get(1)));
        Event recent = eventStorage.getMostRecent(TrackedEventType.LAUNCH);
        assertThat(recent, is(event4));
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
