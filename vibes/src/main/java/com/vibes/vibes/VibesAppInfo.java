package com.vibes.vibes;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Value type representing the details of the app
 */
public class VibesAppInfo {
    /**
     * The application id being referenced
     */
    private final String appId;

    /**
     * The inbox status of the application
     */
    private final boolean inboxEnabled;

    /**
     * Initialize the App object
     * @param appId id of the application
     * @param inboxEnabled boolean flag for the inbox status of the application
     */
    public VibesAppInfo(String appId, boolean inboxEnabled) {
        this.appId = appId;
        this.inboxEnabled = inboxEnabled;
    }

    /**
     * Initialize the app object with json object
     * @param jsonObject json object returned from api call
     * @throws JSONException exception from invalid json
     */
    public VibesAppInfo(JSONObject jsonObject) throws JSONException {
        this.appId = jsonObject.getString("app_id");
        this.inboxEnabled = jsonObject.getBoolean("inbox_enabled");
    }

    public String getAppId() {
        return appId;
    }

    public boolean isInboxEnabled() {
        return inboxEnabled;
    }

}
