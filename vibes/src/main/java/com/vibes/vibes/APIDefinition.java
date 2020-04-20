package com.vibes.vibes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import static com.vibes.vibes.HTTPMethod.DELETE;
import static com.vibes.vibes.HTTPMethod.GET;
import static com.vibes.vibes.HTTPMethod.PATCH;
import static com.vibes.vibes.HTTPMethod.POST;
import static com.vibes.vibes.HTTPMethod.PUT;

/**
 * A definition of HTTP Resources for the Vibes API.
 */
class APIDefinition {
    private static final String TAG = "APIDefinition";
    public static String advertisingId = "";
    public static String applicationVersion = "";
    public static Double appLatitude = null;
    public static Double appLongitude = null;

    /**
     * Setter for appLatitude and appLongitude.
     *
     * @param newLatitude
     * @param newLongitude
     */
    public static void setAppCoordinates(Double newLatitude, Double newLongitude) {
        appLatitude = newLatitude;
        appLongitude = newLongitude;
    }

    /**
     * An HTTP resource for registering this device with Vibes.
     *
     * @param appId the app id for this account, provided by Vibes, e.g. "TEST_APP_KEY"
     */
    public static HTTPResource<Credential> registerDevice(String appId) {
        String path = "/" + appId + "/devices";
        return new HTTPResource<Credential>(
                path, POST, getEncodedDevice(), jsonHeaders(), credentialParser());
    }

    /**
     * An HTTP resource for updating this device with Vibes.
     *
     * @param appId            the app id for this account, provided by Vibes, e.g. "TEST_APP_KEY"
     * @param deviceID         the device ID from Vibes for the device
     * @param updateDeviceInfo if true -> `update device Info`, otherwise `renew_token`
     */
    public static HTTPResource<Credential> updateDevice(String appId, String deviceID, Boolean updateDeviceInfo) {
        String path = devicePath(appId, deviceID);
        if (updateDeviceInfo) {
            return new HTTPResource<Credential>(
                    // In case of updating the device, it won't return any credentials.
                    path, PATCH, getEncodedDevice(), jsonHeaders(), emptyCredentialParser());
        } else {
            return new HTTPResource<Credential>(
                    path, PUT, getEncodedDevice(), jsonHeaders(), credentialParser());
        }
    }

    /**
     * An HTTP resource for unregistering this device with Vibes.
     *
     * @param appId    the app id for this account, provided by Vibes, e.g. "TEST_APP_KEY"
     * @param deviceID the device ID from Vibes for the device
     */
    public static HTTPResource<Void> unregisterDevice(String appId, String deviceID) {
        String path = devicePath(appId, deviceID);
        return new HTTPResource<Void>(path, DELETE, null, jsonHeaders(), nullParser());
    }

    /**
     * An HTTP resource for unregistering this device with Vibes.
     *
     * @param appId     the app id for this account, provided by Vibes, e.g. "TEST_APP_KEY"
     * @param deviceID  the device ID from Vibes for the device
     * @param pushToken the Firebase Cloud Messaging token for the device
     */
    public static HTTPResource<Void> registerPush(String appId, String deviceID, String pushToken) {
        String path = devicePath(appId, deviceID) + "/push_registration";
        DeviceToken token = new DeviceToken(pushToken);
        return new HTTPResource<Void>(path, POST, token.encode(), jsonHeaders(), nullParser());
    }

    /**
     * An HTTP resource for unregistering this device with Vibes.
     *
     * @param appId    the app id for this account, provided by Vibes, e.g. "TEST_APP_KEY"
     * @param deviceID the device ID from Vibes for the device
     */
    public static HTTPResource<Void> unregisterPush(String appId, String deviceID) {
        String path = devicePath(appId, deviceID) + "/push_registration";
        return new HTTPResource<Void>(path, DELETE, null, jsonHeaders(), nullParser());
    }

    /**
     * An HTTP resource for tracking events with Vibes.
     *
     * @param appKey     the app key for this account, provided by Vibes, e.g. "TEST_APP_KEY"
     * @param deviceID   the device ID from Vibes for the device
     * @param collection the events to track
     */
    static HTTPResource<Void> trackEvents(String appKey, String deviceID, EventCollection collection) {
        EventCollection.EventCollectionObjectFactory factory = new EventCollection.EventCollectionObjectFactory();
        String type = collection.getEvents().get(0).getType().name().toLowerCase(Locale.ENGLISH);
        String json = null;
        try {
            json = factory.serialize(collection);
        } catch (Exception e) {
            Vibes.getCurrentLogger().log(e);
        }
        String path = devicePath(appKey, deviceID) + "/events";
        HashMap<String, String> headers = jsonHeaders();
        headers.put("X-Event-Type", type);
        return new HTTPResource<Void>(path, POST, json, headers, nullParser());
    }

    /**
     * An HTTP resource for asscoiateing this device with a person for Vibes.
     *
     * @param appId            the app id for this account, provided by Vibes, e.g. "TEST_APP_KEY"
     * @param deviceId         the device ID from Vibes for the device
     * @param externalPersonId the client-defined identifier for the person that owns this handset
     */
    public static HTTPResource<Void> associatePerson(String appId, String deviceId, String externalPersonId) {
        String path = devicePath(appId, deviceId) + "/assign";
        JSONObject json = new JSONObject();
        try {
            json.put("external_person_id", externalPersonId);
        } catch (JSONException e) {
            Vibes.getCurrentLogger().log(e);
            /* will end up posting an empty json and fail */
        }
        return new HTTPResource<Void>(path, POST, json.toString(), jsonHeaders(), nullParser());
    }

    /**
     * An HTTP resource for getting personID given deviceID and appID
     * @param appId
     * @param deviceId
     * @return
     */
    public static HTTPResource<Person> getPerson(String appId, String deviceId) {
        String path = devicePath(appId, deviceId) + "/person";
        return new HTTPResource<>(path, GET, null, jsonHeaders(), personJSONResourceParser());
    }

    /**
     * An HTTP resource for getting messages from inbox server given the person key
     * @param personKey
     * @return
     */
    public static HTTPResource<Collection<InboxMessage>> fetchInboxMessages(String personKey, String appId) {
        String path = personPath(personKey, appId) + "/messages";
        return new HTTPResource<>(path, GET, null, jsonHeaders(), new InboxMessagesParser());
    }

    public static HTTPResource<InboxMessage> fetchInboxMessage(String personKey, String appId, String messageUid) {
        String path = personPath(personKey, appId) + "/messages/" + messageUid;
        return new HTTPResource<>(path, GET, null, jsonHeaders(), inboxMessageJSONResourceParser());
    }

    public static HTTPResource<InboxMessage> markInboxMessageAsRead(String personKey, String messageId, String appId) {
        String path = personPath(personKey, appId) + "/" + "messages/" + messageId;
        JSONObject payload = new JSONObject();
        try {
            payload.put("read", true);
        } catch (JSONException e) {
            Vibes.getCurrentLogger().log(e);
            /* catch json exception here */
        }
        return new HTTPResource<>(
                path, PUT, payload.toString(), jsonHeaders(), inboxMessageJSONResourceParser());
    }

    public static HTTPResource<InboxMessage> expireInboxMessage(String personKey, String messageId, String expirationDate, String appId) {
        String path = personPath(personKey, appId) + "/" + "messages/" + messageId;
        JSONObject payload = new JSONObject();
        try {
            payload.put("expires_at", expirationDate);
        } catch (JSONException e) {
            Vibes.getCurrentLogger().log(e);
            /* catch json exception here */
        }
        return new HTTPResource<>(
                path, PUT, payload.toString(), jsonHeaders(), inboxMessageJSONResourceParser());
    }

    /**
     * A utility method for returning the encoded device needed for a JSON request.
     */
    private static String getEncodedDevice() {
        return new Device(TimeZone.getDefault(), Locale.getDefault(),
                Vibes.getInstance().getVibesConfig().getAdvertisingId(), applicationVersion, appLatitude, appLongitude).encode();
    }

    /**
     * A utility method for returning the request headers needed for a JSON request.
     */
    private static HashMap<String, String> jsonHeaders() {
        HashMap<String, String> headers = new HashMap<>();

        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");

        return headers;
    }

    /**
     * A utility method for returning a parser that can create a {@link Credential} from JSON.
     */
    private static JSONResourceParser<Credential> credentialParser() {
        return new JSONResourceParser<Credential>() {
            @Override
            public Credential parse(String text) throws JSONException {
                JSONObject json = new JSONObject(text);
                return new Credential(json);
            }
        };
    }

    private static JSONResourceParser<Person> personJSONResourceParser() {
        return new JSONResourceParser<Person>() {
            @Override
            public Person parse(String text) throws JSONException {
                JSONObject json = new JSONObject(text);
                return new Person(json);
            }
        };
    }

    private static JSONResourceParser<InboxMessage> inboxMessageJSONResourceParser() {
        return new JSONResourceParser<InboxMessage>() {
            @Override
            public InboxMessage parse(String text) throws JSONException {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                return gson.fromJson(text, InboxMessage.class);
            }
        };
    }

    /**
     * A utility method for returning a parser that can create a {@link Credential} from JSON.
     */
    private static JSONResourceParser<Credential> emptyCredentialParser() {
        return new JSONResourceParser<Credential>() {
            @Override
            public Credential parse(String text) throws JSONException {
                return new Credential("", "");
            }
        };
    }

    /**
     * A utility method for resources that don't return anything.
     */
    private static ResourceParser<Void> nullParser() {
        return new ResourceParser<Void>() {
            @Override
            public Void parse(String text) throws Exception {
                return null;
            }
        };
    }

    private static String devicePath(String appId, String deviceID) {
        return "/" + appId + "/devices/" + deviceID;
    }

    private static String personPath(String personKey, String appId) {
        return "/" + appId + "/persons/" + personKey;
    }

}
