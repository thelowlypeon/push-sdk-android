package com.vibes.vibes;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;

import java.util.Collections;
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
    private static final String TAG = "VibesLifecycleListener";

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
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
        if (isInBackground) {
            isInBackground = false;
            trackLaunchEvent();
        }
    }

    private void trackLaunchEvent() {
        Event eventLaunch = new Event(TrackedEventType.LAUNCH, new HashMap<String, String>(), new Date());
        eventTracker.trackEvents(Collections.singletonList(eventLaunch));
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
