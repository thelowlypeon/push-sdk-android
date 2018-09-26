package com.vibes.vibes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * A value object to represent the credentials needed to interact with the Vibes API.
 */
public class Credential {
    /**
     * A unique identifier for the device, provided by Vibes when registering.
     */
    private String deviceID;

    /**
     * A unique, transient token for the device. The combination of `deviceID` and `authToken` will
     * be used to authenticate most calls.
     */
    private String authToken;

    /**
     * A factory for serializing/deserializing a Credential to/from a String, so that it can be
     * stored using a {@link LocalStorageAdapter}.
     */
    static class CredentialObjectFactory implements JSONObjectFactory<Credential> {
        /**
         * Serializes a Credential to a JSON string.
         * @param credential the Credential to serialize
         * @return a String of valid JSON
         * @throws JSONException
         */
        public String serialize(Credential credential) throws JSONException {
            JSONObject json = new JSONObject();

            json.put("device_id", credential.getDeviceID());
            json.put("auth_token", credential.getAuthToken());

            return json.toString(2);
        }

        /**
         * Deserializes a string of valid JSON to a Credential object.
         * @param jsonString a String of valid JSON
         * @return a Credential
         * @throws JSONException
         */
        public Credential createInstance(String jsonString) throws JSONException {
            JSONObject json = new JSONObject(jsonString);

            String deviceId = json.getString("device_id");
            String authToken = json.getString("auth_token");

            return new Credential(deviceId, authToken);
        }
    }

    /**
     * Initialize this object from `deviceID` and `authToken`.
     * @param deviceID the unique ID of the device
     * @param authToken a unique but transient token for the device
     */
    public Credential(String deviceID, String authToken) {
        this.deviceID = deviceID;
        this.authToken = authToken;
    }

    /**
     * Initialize this object from a JSONObject.
     * @param json a JSON object containing `deviceID` and `authToken` values
     */
    public Credential(JSONObject json) throws JSONException {
        this.authToken = json.getString("auth_token");

        JSONObject device = json.getJSONObject("device");
        this.deviceID = device.getString("vibes_device_id");
    }

    /**
     * A human-readable representation of the Credential.
     */
    public String toString() {
        return "Credential<deviceID: " + this.deviceID + " authToken: " + this.authToken + ">";
    }

    /**
     * The unique ID for a device.
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     * A unique transient token for a device.
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Checks if an Object is equal to this Credential.
     * @param other an Object, ideally a Credential, to compare against.
     * @return true on equal; false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof Credential)) {
            return false;
        }

        Credential credential = (Credential) other;
        return this.getAuthToken().equals(credential.getAuthToken())
                && this.getDeviceID().equals(credential.getDeviceID());
    }

    /**
     * Generates a unique hash code for this Credential.
     * @return an integer to use when hashing.
     */
    @Override
    public int hashCode() {
        Object[] array = new Object[] { (Object) deviceID, (Object) authToken };
        return Arrays.hashCode(array);
    }
}
