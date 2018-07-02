package com.vibes.vibes;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import static com.vibes.vibes.HTTPMethod.DELETE;
import static com.vibes.vibes.HTTPMethod.POST;
import static com.vibes.vibes.HTTPMethod.PUT;

/**
 * A definition of HTTP Resources for the Vibes API.
 */
class APIDefinition {
    private static final String TAG = "APIDefinition";
    public static String advertisingId = "";
    public static String applicationVersion = "";

    /**
     * An HTTP resource for registering this device with Vibes.
     * @param appId the app id for this account, provided by Vibes, e.g. "TEST_APP_KEY"
     */
    public static HTTPResource<Credential> registerDevice(String appId) {
        String path = "/" + appId + "/devices";

        Device device = new Device(TimeZone.getDefault(), Locale.getDefault(),
                advertisingId, applicationVersion);
        String requestBody = device.encode();

        return new HTTPResource<Credential>(path, POST, requestBody, jsonHeaders(), credentialParser());
    }

    /**
     * An HTTP resource for updating this device with Vibes.
     * @param appId the app id for this account, provided by Vibes, e.g. "TEST_APP_KEY"
     * @param deviceID the device ID from Vibes for the device
     */
    public static HTTPResource<Credential> updateDevice(String appId, String deviceID) {
        String path = devicePath(appId, deviceID);

        Device device = new Device(TimeZone.getDefault(), Locale.getDefault(),
                advertisingId, applicationVersion);
        String requestBody = device.encode();

        return new HTTPResource<Credential>(path, PUT, requestBody, jsonHeaders(), credentialParser());
    }

    /**
     * An HTTP resource for unregistering this device with Vibes.
     * @param appId the app id for this account, provided by Vibes, e.g. "TEST_APP_KEY"
     * @param deviceID the device ID from Vibes for the device
     */
    public static HTTPResource<Void> unregisterDevice(String appId, String deviceID) {
        String path = devicePath(appId, deviceID);
        return new HTTPResource<Void>(path, DELETE, null, jsonHeaders(), nullParser());
    }

    /**
     * An HTTP resource for unregistering this device with Vibes.
     * @param appId the app id for this account, provided by Vibes, e.g. "TEST_APP_KEY"
     * @param deviceID the device ID from Vibes for the device
     * @param pushToken the Firebase Cloud Messaging token for the device
     */
    public static HTTPResource<Void> registerPush(String appId, String deviceID, String pushToken) {
        String path = devicePath(appId, deviceID) + "/push_registration";
        DeviceToken token = new DeviceToken(pushToken);
        return new HTTPResource<Void>(path, POST, token.encode(), jsonHeaders(), nullParser());
    }

    /**
     * An HTTP resource for unregistering this device with Vibes.
     * @param appId the app id for this account, provided by Vibes, e.g. "TEST_APP_KEY"
     * @param deviceID the device ID from Vibes for the device
     */
    public static HTTPResource<Void> unregisterPush(String appId, String deviceID) {
        String path = devicePath(appId, deviceID) + "/push_registration";
        return new HTTPResource<Void>(path, DELETE, null, jsonHeaders(), nullParser());
    }

    /**
     * An HTTP resource for tracking events with Vibes.
     * @param appKey the app key for this account, provided by Vibes, e.g. "TEST_APP_KEY"
     * @param deviceID the device ID from Vibes for the device
     * @param collection the events to track
     */
    static HTTPResource<Void> trackEvents(String appKey, String deviceID, EventCollection collection) {
        EventCollection.EventCollectionObjectFactory factory = new EventCollection.EventCollectionObjectFactory();
        String type = collection.getEvents().get(0).getType().name().toLowerCase(Locale.ENGLISH);
        String json = null;
        try {
            json = factory.serialize(collection);
        } catch (Exception ex) {
            Log.e(TAG, "parsing event collection", ex);
        }
        String path = devicePath(appKey, deviceID) + "/events";
        HashMap<String, String> headers = jsonHeaders();
        headers.put("X-Event-Type", type);
        return new HTTPResource<Void>(path, POST, json, headers, nullParser());
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
}
