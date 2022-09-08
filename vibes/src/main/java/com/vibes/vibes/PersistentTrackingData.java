package com.vibes.vibes;

import android.util.Log;

import com.google.gson.Gson;
import com.vibes.vibes.tracking.TrackingData;

interface PersistentTrackingDataInterface {

    /**
     * Stores the push tracking data to local storage
     *
     * @param trackingDataString client app data
     */
    void persistTrackingData(String trackingDataString);

    /**
     * Removes existing tracking data from local storage
     */
    void removeTrackingData();

    /**
     * Retrieves the most recent tracking data stored in local storage
     *
     * @return Map<String, String> pushTrackingData
     */
    TrackingData getTrackingData();
}

/**
 * An object to take care of persisting incoming push data received from a notification
 * removes the data and replaces it with the most recent incoming notification data for tracking
 */
public class PersistentTrackingData implements PersistentTrackingDataInterface {

    /**
     * The local storage mechanism we are using for storing push tracking data
     */
    private final LocalStorage localStorage;

    private static final String TAG = "PersistentTrackingData";

    PersistentTrackingData(LocalStorage localStorage) {
        this.localStorage = localStorage;
    }

    @Override
    public void persistTrackingData(String trackingDataString) {
        TrackingData storedPushMap = getTrackingData();
        if (storedPushMap != null) {
            removeTrackingData();
        }
        try {
            this.localStorage.put(LocalObjectKeys.pushData, trackingDataString);
        } catch (Exception e) {
            Log.d(TAG, "Failed to store data: **** " + e.getLocalizedMessage());
        }
    }

    @Override
    public void removeTrackingData() {
        try {
            this.localStorage.remove(LocalObjectKeys.pushData);
        } catch (Exception e) {
            Log.d(TAG, "Failed to remove key: **** " + e.getLocalizedMessage());
        }
    }

    @Override
    public TrackingData getTrackingData() {
        try {
            String storedPushString = this.localStorage.get(LocalObjectKeys.pushData);
            Gson gson = new Gson();
            return gson.fromJson(storedPushString, TrackingData.class);
        } catch (Exception e) {
            Log.d(TAG, "Error fetching tracking data: " + e.getLocalizedMessage());
            return null;
        }
    }
}
