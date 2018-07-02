package com.vibes.vibes;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

interface EventTracker {
    void trackEvents(List<Event> events);
}

public class VibesLifecycleListener implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    EventTracker eventTracker;
    private boolean isStarted = false;
    private boolean isInBackground = false;
    private static String kMessageUID = "message_uid";
    private static final String TAG = "VibesLifecycleListener";

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        trackPushEvent(activity);
        if (!isStarted) {
            isStarted = true;
            trackLaunchEvent();
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        trackPushEvent(activity);
        if (isInBackground) {
            isInBackground = false;
            trackLaunchEvent();
        }
    }

    private void trackLaunchEvent() {
        Event eventLaunch = new Event(TrackedEventType.LAUNCH, new HashMap<String, String>(), new Date());
        eventTracker.trackEvents(Collections.singletonList(eventLaunch));
    }

    private void trackPushEvent(Activity activity) {
        // This function is mutable. If a clickthru event has already been logged, it will not be
        // logged a second time.
        Intent intent = activity.getIntent();
        @SuppressWarnings("unchecked")
        Map<String, String> map =
                (Map<String, String>) intent.getSerializableExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA);
        if (map != null) {
            HashMap<String, String> properties = new HashMap<>();
            if (map.containsKey(kMessageUID)) {
                properties.put(kMessageUID, map.get(kMessageUID));
            }
            Event clickEvent = new Event(TrackedEventType.CLICKTHRU, properties, new Date());
            eventTracker.trackEvents(Collections.singletonList(clickEvent));
            intent.removeExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA);
        }
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
