package com.vibes.vibes;

import androidx.test.core.app.ApplicationProvider;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.vibes.vibes.tracking.Actions;
import com.vibes.vibes.tracking.Product;
import com.vibes.vibes.tracking.ProductTest;
import com.vibes.vibes.tracking.Purchase;
import com.vibes.vibes.tracking.PurchaseTest;
import com.vibes.vibes.tracking.TrackingData;

public class APIDefinitionTest extends TestConfig {

    @Before
    public void setUp() throws Exception {
        VibesConfig config = new VibesConfig.Builder().setAppId("TEST_APP_ID").build();
        Vibes.initialize(ApplicationProvider.getApplicationContext(), config);
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
    public void updateDeviceNoCredentialUpdate() throws Exception {
        HTTPResource<Credential> resource = APIDefinition.updateDevice("key", "device-id", false);

        assertThat(resource.getPath(), is("/key/devices/device-id"));
        assertThat(resource.getMethod().toString(), is("PATCH"));
        assertThat(resource.getHeaders().get("Accept"), is("application/json"));
        assertThat(resource.getHeaders().get("Content-Type"), is("application/json"));
    }

    @Test
    public void updateDeviceWithCredentialUpdate() throws Exception {
        HTTPResource<Credential> resource = APIDefinition.updateDevice("key", "device-id", true);

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

    @Test
    public void fetchVibesAppInfo() throws Exception {
        HTTPResource<VibesAppInfo> resource = APIDefinition.fetchVibesAppInfo("app-id");
        assertThat(resource.getPath(), is("/app-id"));
        assertThat(resource.getMethod().toString(), is("GET"));
        assertThat(resource.getHeaders().get("Accept"), is("application/json"));
        assertThat(resource.getHeaders().get("Content-Type"), is("application/json"));
    }

    @Test
    public void migrationCallback() {
        HTTPResource<Void> resource = APIDefinition.migrationCallback("ius-migration-item-3", "device-id-9032", "90ds-app-id");
        assertThat(resource.getPath(), is("/90ds-app-id/migrations/callbacks"));
        assertThat(resource.getMethod().toString(), is("PUT"));
        assertThat(resource.getRequestBody(), is("{\"migration_item_id\":\"ius-migration-item-3\",\"vibes_device_id\":\"device-id-9032\"}"));
        assertThat(resource.getHeaders().get("Accept"), is("application/json"));
        assertThat(resource.getHeaders().get("Content-Type"), is("application/json"));
    }

    @Test
    public void testTrackProductAction() throws Exception {
        String companyKey = "s23rqdfa";
        String activityUid = UUID.randomUUID().toString();
        String activityType = "Broadcast";
        String personId = UUID.randomUUID().toString();
        String alternativeActivityUid = UUID.randomUUID().toString();

        TrackingData trackingData = new TrackingData();
        trackingData.setActivityUid(activityUid);
        trackingData.setActivityType(activityType);
        trackingData.setPersonId(personId);
        trackingData.setCompanyKey(companyKey);
        Product product = ProductTest.createProduct();

        HTTPResource<Void> resource = APIDefinition.trackProductAction(Actions.ProductAction.CLICK, product, trackingData, null);
        String pathParams = createTrackPath(Actions.ProductAction.CLICK, product, trackingData, trackingData.getActivityUid());
        assertThat(resource.getPath(), is(pathParams));
        assertThat(resource.getMethod().toString(), is("GET"));
        assertThat(resource.getHeaders().get("Accept"), is("application/json"));
        assertThat(resource.getHeaders().get("Content-Type"), is("application/json"));

        HTTPResource<Void> alternateResource = APIDefinition.trackProductAction(Actions.ProductAction.CLICK, product, trackingData, alternativeActivityUid);
        String alternatePathParams = createTrackPath(Actions.ProductAction.CLICK, product, trackingData, alternativeActivityUid);
        assertThat(alternateResource.getPath(), is(alternatePathParams));
    }

    @Test
    public void testTrackPurchaseAction() throws Exception {
        String companyKey = "s23rqdfa";
        String activityUid = UUID.randomUUID().toString();
        String activityType = "Broadcast";
        String personId = UUID.randomUUID().toString();
        String alternativeActivityUid = UUID.randomUUID().toString();

        TrackingData trackingData = new TrackingData();
        trackingData.setActivityUid(activityUid);
        trackingData.setActivityType(activityType);
        trackingData.setPersonId(personId);
        trackingData.setCompanyKey(companyKey);
        Purchase purchase = PurchaseTest.createPurchase();

        HTTPResource<Void> resource = APIDefinition.trackPurchaseAction(Actions.PurchaseAction.PURCHASE, purchase, trackingData, null);
        String pathParams = createTrackPath(Actions.PurchaseAction.PURCHASE, purchase, trackingData, trackingData.getActivityUid());
        assertThat(resource.getPath(), is(pathParams));
        assertThat(resource.getMethod().toString(), is("GET"));
        assertThat(resource.getHeaders().get("Accept"), is("application/json"));
        assertThat(resource.getHeaders().get("Content-Type"), is("application/json"));

        HTTPResource<Void> alternateResource = APIDefinition.trackPurchaseAction(Actions.PurchaseAction.PURCHASE, purchase, trackingData, alternativeActivityUid);
        String alternatePathParams = createTrackPath(Actions.PurchaseAction.PURCHASE, purchase, trackingData, alternativeActivityUid);
        assertThat(alternateResource.getPath(), is(alternatePathParams));
    }

    private static String createTrackPath(Actions.ProductAction action, Product product, TrackingData data, String actualActivityId) throws Exception {
        JSONObject actionPayload = new JSONObject();
        JSONObject payload = product.encode();
        actionPayload.put("action", action.getActionName());
        actionPayload.put("personId", data.getPersonId());
        actionPayload.put("activityId", actualActivityId);
        actionPayload.put("companyId", data.getCompanyKey());
        actionPayload.put("data", payload);

        String path = "[" + actionPayload + "]";
        String encodedPath = "?d=" + URLEncoder.encode(path, StandardCharsets.UTF_8.toString());
        return encodedPath;
    }

    private static String createTrackPath(Actions.PurchaseAction action, Purchase purchase, TrackingData data, String actualActivityId) throws Exception {
        JSONObject actionPayload = new JSONObject();
        JSONObject payload = purchase.encode();
        actionPayload.put("action", action.getActionName());
        actionPayload.put("personId", data.getPersonId());
        actionPayload.put("activityId", actualActivityId);
        actionPayload.put("companyId", data.getCompanyKey());
        actionPayload.put("data", payload);

        String path = "[" + actionPayload + "]";
        return "?d=" + URLEncoder.encode(path, StandardCharsets.UTF_8.toString());
    }

}
