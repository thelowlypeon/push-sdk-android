package com.vibes.vibes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class VibesLifecycleListenerTest extends TestConfig {
    VibesLifecycleListener listener;
    StubEventTracker tracker;

    @Before
    public void setup() {
        listener = new VibesLifecycleListener();
        tracker = new StubEventTracker();
        listener.eventTracker = tracker;
    }

    @Test
    public void onActivityResumedWithLaunch() throws Exception {
        HashMap<String, String> extras = new HashMap<>();
        StubIntent intent = new StubIntent(extras);
        StubActivity activity = new StubActivity(intent);

        // make sure application is in the background
        listener.onTrimMemory(20);
        listener.onActivityResumed(activity);
        assertThat(tracker.eventsTracked.size(), is(1));
        assertThat(tracker.eventsTracked.get(0).getType(), is(TrackedEventType.LAUNCH));
    }

}