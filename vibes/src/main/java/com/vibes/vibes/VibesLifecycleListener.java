package com.vibes.vibes;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

interface EventTracker {
    void trackEvents(List<Event> events);
}

public class VibesLifecycleListener implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    EventTracker eventTracker;
    private boolean isStarted = false;
    private boolean isInBackground = false;
    private static String kMessageUID = "message_uid";
    private static String kPushPaylod = "pushPayload";
    private static String TAG = "VibesLifecycleListener";

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        if (!isStarted) {
            isStarted = true;
            trackEvents(activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (isInBackground) {
            isInBackground = false;
            trackEvents(activity);
        }
    }

    private void trackEvents(Activity activity) {
        // When the user clicks clicks on a push notification 2 events are generated
        // a launch event and a clicktrhu event.
        ArrayList<Event> listOfEvents = new ArrayList<>();
        Event eventLaunch = new Event(TrackedEventType.LAUNCH, new HashMap<String, String>(), new Date());
        listOfEvents.add(eventLaunch);
        Event clickEvent = checkForPushEvent(activity);
        if (clickEvent != null) {
            listOfEvents.add(clickEvent);
        }
        eventTracker.trackEvents(listOfEvents);
    }

    private Event checkForPushEvent(Activity activity) {
        Intent intent = activity.getIntent();
        @SuppressWarnings("unchecked")
        HashMap<String, String> map = (HashMap<String, String>) intent.getSerializableExtra(kPushPaylod);
        if (map != null) {
            Log.d(TAG, "--> LifeCycle --> clickthru event pushed");
            HashMap<String, String> properties = new HashMap<>();
            if (map.containsKey(kMessageUID)) {
                properties.put(kMessageUID, map.get(kMessageUID));
            }
            Event clickEvent = new Event(TrackedEventType.CLICKTHRU, properties, new Date());
            return clickEvent;
        } else {
            Log.d(TAG, "--> LifeCycle --> No clickthru event");
        }
        return null;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onTrimMemory(int level) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            isInBackground = true;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {

    }
}
