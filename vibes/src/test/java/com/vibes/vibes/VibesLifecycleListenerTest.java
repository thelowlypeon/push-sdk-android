package com.vibes.vibes;

import android.app.Activity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class VibesLifecycleListenerTest {
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
        HashMap<String, String> extras = new HashMap<String, String>();
        StubIntent intent = new StubIntent(extras);
        StubActivity activity = new StubActivity(intent);

        listener.onActivityResumed(activity);
        assertThat(tracker.eventsTracked.size(), is(1));
        assertThat(tracker.eventsTracked.get(0).getType(), is(TrackedEventType.LAUNCH));
    }

    @Test
    public void onActivityResumedWithClickThru() throws Exception {
        HashMap<String, String> extras = new HashMap<>();
        extras.put("pushPayload", "");
        StubIntent intent = new StubIntent(extras);
        StubActivity activity = new StubActivity(intent);

        listener.onActivityResumed(activity);
        assertThat(tracker.eventsTracked.size(), is(2));
        assertThat(tracker.eventsTracked.get(0).getType(), is(TrackedEventType.LAUNCH));
        assertThat(tracker.eventsTracked.get(1).getType(), is(TrackedEventType.CLICKTHRU));
    }
}