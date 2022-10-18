package com.vibes.vibes;

import com.google.gson.Gson;
import com.vibes.vibes.tracking.TrackingData;

public class StubPersistentTrackingData implements PersistentTrackingDataInterface {
    String[] trackingDataArray = new String[1];

    @Override
    public void persistTrackingData(String trackingDataJsonString) {
        trackingDataArray[0] = trackingDataJsonString;
    }

    @Override
    public void removeTrackingData() {
        trackingDataArray[0] = null;
    }

    @Override
    public TrackingData getTrackingData() {
        if (trackingDataArray[0] != null) {
            Gson gson = new Gson();
            return gson.fromJson(trackingDataArray[0], TrackingData.class);
        }
        return null;
    }
}
