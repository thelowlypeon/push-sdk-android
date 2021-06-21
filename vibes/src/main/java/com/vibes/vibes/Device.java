package com.vibes.vibes;

import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.TimeZone;

/**
 * A value type representing this device 's details sent to Vibes when registering.
 */
public class Device {

    /**
     * The timezone for this Device.
     */
    private TimeZone timeZone;

    /**
     * The locale for this Device.
     */
    private Locale locale;

    /**
     * The advertising Id.
     */
    private String advertisingId;

    /**
     * The client application version.
     */
    private String applicationVersion;

    /**
     * The longitude from the app.
     */
    private Double longitude = null;

    /**
     * The latitude from the app.
     */
    private Double latitude = null;

    /**
     * Initialize this object.
     *
     * @param timeZone the time zone for this device
     * @param locale the locale for this device
     * @param advertisingId Advertising identifier
     * @param applicationVersion Client application version
     * @param latitude Latitude from app
     * @param longitude Longitude from app
     */
    public Device(TimeZone timeZone, Locale locale, String advertisingId, String applicationVersion,
                  Double latitude, Double longitude) {
        this.timeZone = timeZone;
        this.locale = locale;
        this.advertisingId = advertisingId;
        this.applicationVersion = applicationVersion;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * The manufacturer of this device, e.g. "Google"
     */
    public String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * The model of this device, e.g. "Nexus 9"
     */
    public String getModel() {
        return Build.MODEL;
    }

    /**
     * The brand of this device, e.g. "Android"
     */
    public String getBrand() {
        return Build.BRAND;
    }

    /**
     * The version of this device, e.g. "6.0"
     */
    public String getVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * The time zone identifier, e.g. "America/Chicago"
     */
    public String getTimeZoneIdentifier() {
        return this.timeZone.getID();
    }

    /**
     * The locale identifier, e.g. "en_US"
     */
    public String getLocaleIdentifier() {
        return this.locale.toString();
    }

    /**
     * OS name: always "Android"
     */
    public String getOSName() {
        return "Android";
    }

    /**
     * Getter Application version
     * @return String
     */
    public String getApplicationVersion() {
        return applicationVersion;
    }

    /**
     * Getter Advertising id
     * @return String
     */
    public String getAdvertisingId() {
        return advertisingId;
    }

    /**
     * Getter Longitude
     * @return Double
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Getter Latitude
     * @return Double
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Encodes this Device object as a String of JSON.
     */
    public String encode() {
        try {
            JSONObject device = new JSONObject();
            device.put("os", getOSName());
            device.put("os_version", getVersion());
            device.put("sdk_version", VibesBuild.SDK_VERSION);
            device.put("app_version", getApplicationVersion());
            device.put("hardware_make", getManufacturer());
            device.put("hardware_model", getModel());
            device.put("advertising_id", getAdvertisingId());
            device.put("locale", getLocaleIdentifier());
            device.put("timezone", getTimeZoneIdentifier());

            if (longitude != null && latitude != null) {
                JSONObject location = new JSONObject();
                location.put("latitude", latitude);
                location.put("longitude", longitude);
                device.put("location", location);
            }

            JSONObject jObject = new JSONObject();
            jObject.put("device", device);

            return jObject.toString();
        } catch (JSONException exception) {
            return "{}";
        }
    }
}