package com.vibes.vibes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class APIDefinitionTest {

    @Before
    public void setUp() throws Exception {
        VibesConfig config = new VibesConfig.Builder().setAppId("TEST_APP_ID").build();
        Vibes.initialize(RuntimeEnvironment.application.getBaseContext(), config);
    }

    @Test
    public void registerDevice() throws Exception {
        HTTPResource<Credential> resource = APIDefinition.registerDevice("key");

        assertThat(resource.getPath(), is("/key/devices"));
        assertThat(resource.getMethod().toString(), is("POST"));
        assertThat(resource.getHeaders().get("Accept"), is("application/json"));
        assertThat(resource.getHeaders().get("Content-Type"), is("application/json"));
    }

    @Test
    public void updateDevice() throws Exception {
        HTTPResource<Credential> resource = APIDefinition.updateDevice("key", "device-id", false);

        assertThat(resource.getPath(), is("/key/devices/device-id"));
        assertThat(resource.getMethod().toString(), is("PUT"));
        assertThat(resource.getHeaders().get("Accept"), is("application/json"));
        assertThat(resource.getHeaders().get("Content-Type"), is("application/json"));
    }

    @Test
    public void unregisterDevice() throws Exception {
        HTTPResource<Void> resource = APIDefinition.unregisterDevice("key", "device-id");

        assertThat(resource.getPath(), is("/key/devices/device-id"));
        assertThat(resource.getMethod().toString(), is("DELETE"));
        assertThat(resource.getHeaders().get("Accept"), is("application/json"));
        assertThat(resource.getHeaders().get("Content-Type"), is("application/json"));
    }

    @Test
    public void registerPush() throws Exception {
        HTTPResource<Void> resource = APIDefinition.registerPush("key", "device-id", "device-token");

        assertThat(resource.getPath(), is("/key/devices/device-id/push_registration"));
        assertThat(resource.getMethod().toString(), is("POST"));
        assertThat(resource.getHeaders().get("Accept"), is("application/json"));
        assertThat(resource.getHeaders().get("Content-Type"), is("application/json"));
    }

    @Test
    public void unregisterPush() throws Exception {
        HTTPResource<Void> resource = APIDefinition.unregisterPush("key", "device-id");

        assertThat(resource.getPath(), is("/key/devices/device-id/push_registration"));
        assertThat(resource.getMethod().toString(), is("DELETE"));
        assertThat(resource.getHeaders().get("Accept"), is("application/json"));
        assertThat(resource.getHeaders().get("Content-Type"), is("application/json"));
    }

    @Test
    public void associatePerson() throws Exception {
        HTTPResource<Void> resource = APIDefinition.associatePerson("key", "device-id", "external-person-id");

        assertThat(resource.getPath(), is("/key/devices/device-id/assign"));
        assertThat(resource.getMethod().toString(), is("POST"));
        assertThat(resource.getHeaders().get("Accept"), is("application/json"));
        assertThat(resource.getHeaders().get("Content-Type"), is("application/json"));
    }

    @Test
    public void gePerson() throws Exception {
        HTTPResource<Person> resource = APIDefinition.getPerson("key", "device-id");

        assertThat(resource.getPath(), is("/key/devices/device-id/person"));
        assertThat(resource.getMethod().toString(), is("GET"));
        assertThat(resource.getHeaders().get("Accept"), is("application/json"));
        assertThat(resource.getHeaders().get("Content-Type"), is("application/json"));
    }

    @Test
    public void fetchMessages() throws Exception {
        HTTPResource<Collection<InboxMessage>> resource = APIDefinition.fetchInboxMessages("person-key", "app-id");

        assertThat(resource.getPath(), is("/app-id/persons/person-key/messages"));
        assertThat(resource.getMethod().toString(), is("GET"));
        assertThat(resource.getHeaders().get("Accept"), is("application/json"));
        assertThat(resource.getHeaders().get("Content-Type"), is("application/json"));
    }

    @Test
    public void fetchInboxMessage() throws Exception {
        HTTPResource<InboxMessage> resource = APIDefinition.fetchInboxMessage("person-key", "app-id", "message-uid");
        assertThat(resource.getPath(), is("/app-id/persons/person-key/messages/message-uid"));
        assertThat(resource.getMethod().toString(), is("GET"));
        assertThat(resource.getHeaders().get("Accept"), is("application/json"));
        assertThat(resource.getHeaders().get("Content-Type"), is("application/json"));
    }

    @Test
    public void markMessageRead() throws Exception {
        HTTPResource<InboxMessage> resource = APIDefinition.markInboxMessageAsRead("person-key",
                "message-id", "app-id");

        assertThat(resource.getPath(), is("/app-id/persons/person-key/messages/message-id"));
        assertThat(resource.getMethod().toString(), is("PUT"));
        assertThat(resource.getHeaders().get("Accept"), is("application/json"));
        assertThat(resource.getHeaders().get("Content-Type"), is("application/json"));
    }

    @Test
    public void markMessageExpired() throws Exception {
        HTTPResource<InboxMessage> resource = APIDefinition.expireInboxMessage("person-key",
                "message-id", "2019-09-21T10:39:04.118+03:00", "app-id");

        assertThat(resource.getPath(), is("/app-id/persons/person-key/messages/message-id"));
        assertThat(resource.getMethod().toString(), is("PUT"));
        assertThat(resource.getHeaders().get("Accept"), is("application/json"));
        assertThat(resource.getHeaders().get("Content-Type"), is("application/json"));
    }


    @Test
    public void trackEvents() throws Exception {
        Date timestamp = new Date();
        HashMap<String, String> props = new HashMap<String, String>();
        Event event = new Event(TrackedEventType.LAUNCH, props, timestamp);
        ArrayList<Event> events = new ArrayList<>();
        events.add(event);
        EventCollection collection = new EventCollection(events);
        HTTPResource<Void> resource = APIDefinition.trackEvents("key", "device-id", collection);
        EventCollection.EventCollectionObjectFactory factory = new EventCollection.EventCollectionObjectFactory();
        String expectedJson = factory.serialize(collection);

        assertThat(resource.getPath(), is("/key/devices/device-id/events"));
        assertThat(resource.getMethod().toString(), is("POST"));
        assertThat(resource.getHeaders().get("Accept"), is("application/json"));
        assertThat(resource.getHeaders().get("Content-Type"), is("application/json"));
        assertThat(resource.getHeaders().get("X-Event-Type"), is(event.getType().name().toLowerCase()));
        assertThat(resource.getRequestBody(), is(expectedJson));
    }
}