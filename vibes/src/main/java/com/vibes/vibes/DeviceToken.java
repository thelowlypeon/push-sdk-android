package com.vibes.vibes;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple wrapper to allow us to encode the JSON for sending the device's push token to Vibes.
 */
class DeviceToken {
    /**
     * The push token from FCM to send.
     */
    private String token;

    /**
     * Initialize this object.
     * @param token the push token from FCM to send
     */
    public DeviceToken(String token) {
        this.token = token;
    }

    /**
     * Encodes this device's push token.
     * @return a String of JSON
     */
    public String encode() {
        try {
            JSONObject device = new JSONObject();
            device.put("push_token", token);

            JSONObject jObject = new JSONObject();
            jObject.put("device", device);

            return jObject.toString();
        } catch (JSONException exception) {
            return "{}";
        }
    }
}