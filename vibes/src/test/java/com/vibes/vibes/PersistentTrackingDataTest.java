package com.vibes.vibes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import com.vibes.vibes.tracking.TrackingData;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class PersistentTrackingDataTest extends TestConfig {
    private static final String personUid = "078ef69b-6d07-de1e-a669-77cfa2d1413x";
    private static final String companyKey = "Vxy1243P";
    private static final String activityUid = "069ef69b-6d07-de1e-a669-77cfa2d1413a";
    private static final String activityType = "Broadcast";
    PersistentTrackingData trackingData;

    @Before
    public void setUp() throws Exception {
        LocalStorage storage = new LocalStorage(new StubLocalStorageAdapter());
        this.trackingData = new PersistentTrackingData(storage);
    }

    @Test
    public void storedTrackingDataWhenEmpty() throws Exception {
        TrackingData storedTrackingData = trackingData.getTrackingData();
        assertThat(storedTrackingData, is(nullValue()));
    }

    @Test
    public void storedTrackingDataWhenNotEmpty() throws Exception {
        Map<String, String> trackingDataMap = new HashMap<String, String>() {
            {
                put("company_key", companyKey);
                put("activity_uid", activityUid);
                put("activity_type", activityType);
                put("person_id", personUid);
            }
        };
        trackingData.persistTrackingData(trackingDataMap.toString());

        TrackingData storedTrackingData = trackingData.getTrackingData();
        assertThat(storedTrackingData.getPersonId(), is(personUid));
        assertThat(storedTrackingData.getCompanyKey(), is(companyKey));
        assertThat(storedTrackingData.getActivityType(), is(activityType));
        assertThat(storedTrackingData.getActivityUid(), is(activityUid));
    }

    @Test
    public void removeTrackingData() throws Exception {
        Map<String, String> trackingDataMap = new HashMap<String, String>() {
            {
                put("company_key", companyKey);
                put("activity_uid", activityUid);
                put("activity_type", activityType);
                put("person_id", personUid);
            }
        };
        trackingData.persistTrackingData(trackingDataMap.toString());
        trackingData.removeTrackingData();

        assertThat(trackingData.getTrackingData(), is(nullValue()));
    }
}
