package com.vibes.vibes;

import android.content.Context;
import android.os.Looper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class VibesTest {
    Credential actualCredential;
    String actualErrorText;
    boolean called = false;
    String appId = "an-app-key";
    Context context;
    private VibesConfig vibesConfig;

    @Before
    public void setUp() throws Exception {
        context = RuntimeEnvironment.application.getBaseContext();
        vibesConfig = new VibesConfig.Builder()
                .setAppId(appId)
                .build();
    }

    @Test
    public void registerDeviceSuccess() throws Exception {
        Credential expectedCredential = new Credential("device-id", "auth-token");
        StubResult<Credential> result = StubResult.success(expectedCredential);
        VibesAPIInterface api = new StubAPI(result);

        StubCredentialManager credentialManager = new StubCredentialManager();
        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, new StubPersistentEventStorage(), new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        vibes.registerDevice(new TestVibesListener<Credential>() {
            @Override
            public void onSuccess(Credential value) {
                actualCredential = value;
            }
        });

        waitForRunnables(vibes);
        assertThat(actualCredential, is(expectedCredential));
        assertThat(credentialManager.getCurrent(), is(expectedCredential));
    }

    @Test
    public void registerDeviceWithExistingCredential() throws Exception {
        Credential existingCredential = new Credential("device-id", "auth-token");
        StubCredentialManager credentialManager = new StubCredentialManager(existingCredential);
        VibesAPIInterface api = new StubAPI();

        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, new StubPersistentEventStorage(), new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        vibes.registerDevice(new TestVibesListener<Credential>() {
            @Override
            public void onSuccess(Credential value) {
                actualCredential = value;
            }
        });

        waitForRunnables(vibes);
        assertThat(actualCredential, is(existingCredential));
        assertThat(credentialManager.getCurrent(), is(existingCredential));
    }

    @Test
    public void registerDeviceFailure() throws Exception {
        String expectedErrorText = "Invalid app id";
        StubResult<Credential> result = StubResult.failure(404, expectedErrorText);
        VibesAPIInterface api = new StubAPI(result);

        StubCredentialManager credentialManager = new StubCredentialManager();
        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, new StubPersistentEventStorage(), new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        vibes.registerDevice(new TestVibesListener<Credential>() {
            @Override
            public void onFailure(String errorText) {
                actualErrorText = errorText;
            }
        });

        waitForRunnables(vibes);
        assertThat(actualErrorText, is(expectedErrorText));
        assertThat(credentialManager.getCurrent(), nullValue());
    }

    @Test
    public void updateDeviceWithoutExistingCredential() throws Exception {
        VibesAPIInterface api = new StubAPI();
        StubCredentialManager credentialManager = new StubCredentialManager();
        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, new StubPersistentEventStorage(), new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        vibes.updateDeviceLatLong(0.0, 0.0, new TestVibesListener<Credential>() {
            @Override
            public void onFailure(String errorText) {
                actualErrorText = errorText;
            }
        });

        waitForRunnables(vibes);
        assertThat(actualErrorText, is("No credentials"));
    }

    @Test
    public void updateDeviceSuccess() throws Exception {
        Credential expectedCredential = new Credential("device-id", "auth-token");
        StubResult<Credential> result = StubResult.success(expectedCredential);
        VibesAPIInterface api = new StubAPI(result);

        Credential existingCredential = new Credential("existing-device-id", "existing-auth-token");
        StubCredentialManager credentialManager = new StubCredentialManager(existingCredential);

        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, new StubPersistentEventStorage(), new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);


        vibes.updateDeviceLatLong(0.0, 0.0, new TestVibesListener<Credential>() {
            @Override
            public void onSuccess(Credential value) {
                actualCredential = value;
            }
        });

        waitForRunnables(vibes);
        assertThat(actualCredential, is(expectedCredential));
        assertThat(credentialManager.getCurrent(), is(existingCredential));
    }

    @Test
    public void updateDeviceFailure() throws Exception {
        String expectedErrorText = "Invalid app id";
        StubResult<Credential> result = StubResult.failure(404, expectedErrorText);
        VibesAPIInterface api = new StubAPI(result);

        Credential existingCredential = new Credential("existing-device-id", "existing-auth-token");
        StubCredentialManager credentialManager = new StubCredentialManager(existingCredential);

        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, new StubPersistentEventStorage(), new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        vibes.updateDeviceLatLong(0.0, 0.0, new TestVibesListener<Credential>() {
            @Override
            public void onFailure(String errorText) {
                actualErrorText = errorText;
            }
        });

        waitForRunnables(vibes);
        assertThat(actualErrorText, is(expectedErrorText));
        assertThat(credentialManager.getCurrent(), is(existingCredential));
    }

    @Test
    public void unregisterDeviceWithoutExistingCredential() throws Exception {
        VibesAPIInterface api = new StubAPI();
        StubCredentialManager credentialManager = new StubCredentialManager();
        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, new StubPersistentEventStorage(), new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        vibes.unregisterDevice(new TestVibesListener<Void>() {
            @Override
            public void onFailure(String errorText) {
                actualErrorText = errorText;
            }
        });

        waitForRunnables(vibes);
        assertThat(actualErrorText, is("No credentials"));
    }

    @Test
    public void unregisterDeviceSuccess() throws Exception {
        StubResult<Void> result = StubResult.success(null);
        VibesAPIInterface api = new StubAPI(result);

        Credential existingCredential = new Credential("existing-device-id", "existing-auth-token");
        StubCredentialManager credentialManager = new StubCredentialManager(existingCredential);

        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, new StubPersistentEventStorage(), new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        vibes.unregisterDevice(new TestVibesListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                called = true;
            }
        });

        waitForRunnables(vibes);
        assertThat(called, is(true));
        assertThat(credentialManager.getCurrent(), nullValue());
    }

    @Test
    public void unregisterDeviceFailure() throws Exception {
        String expectedErrorText = "Invalid app id";
        StubResult<Credential> result = StubResult.failure(404, expectedErrorText);
        VibesAPIInterface api = (VibesAPIInterface) new StubAPI(result);

        Credential existingCredential = new Credential("existing-device-id", "existing-auth-token");
        StubCredentialManager credentialManager = new StubCredentialManager(existingCredential);

        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, new StubPersistentEventStorage(), new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        vibes.unregisterDevice(new TestVibesListener<Void>() {
            @Override
            public void onFailure(String errorText) {
                actualErrorText = errorText;
            }
        });

        waitForRunnables(vibes);
        assertThat(actualErrorText, is(expectedErrorText));
    }

    @Test
    public void registerPushWithoutExistingCredential() throws Exception {
        VibesAPIInterface api = new StubAPI();
        StubCredentialManager credentialManager = new StubCredentialManager();
        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, new StubPersistentEventStorage(), new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        vibes.registerPush("a-token", new TestVibesListener<Void>() {
            @Override
            public void onFailure(String errorText) {
                actualErrorText = errorText;
            }
        });

        waitForRunnables(vibes);
        assertThat(actualErrorText, is("No credentials"));
    }

    @Test
    public void registerPushSuccess() throws Exception {
        StubResult<Void> result = StubResult.success(null);
        VibesAPIInterface api = new StubAPI(result);

        Credential existingCredential = new Credential("existing-device-id", "existing-auth-token");
        StubCredentialManager credentialManager = new StubCredentialManager(existingCredential);

        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, new StubPersistentEventStorage(), new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        vibes.registerPush("a-token", new TestVibesListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                called = true;
            }
        });

        waitForRunnables(vibes);
        assertThat(called, is(true));
    }

    @Test
    public void registerPushFailure() throws Exception {
        String expectedErrorText = "Invalid app id";
        StubResult<Credential> result = StubResult.failure(404, expectedErrorText);
        VibesAPIInterface api = (VibesAPIInterface) new StubAPI(result);

        Credential existingCredential = new Credential("existing-device-id", "existing-auth-token");
        StubCredentialManager credentialManager = new StubCredentialManager(existingCredential);

        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, new StubPersistentEventStorage(), new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        vibes.registerPush("a-token", new TestVibesListener<Void>() {
            @Override
            public void onFailure(String errorText) {
                actualErrorText = errorText;
            }
        });

        waitForRunnables(vibes);
        assertThat(actualErrorText, is(expectedErrorText));
    }

    @Test
    public void unregisterPushWithoutExistingCredential() throws Exception {
        VibesAPIInterface api = new StubAPI();
        StubCredentialManager credentialManager = new StubCredentialManager();
        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, new StubPersistentEventStorage(), new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        vibes.unregisterPush(new TestVibesListener<Void>() {
            @Override
            public void onFailure(String errorText) {
                actualErrorText = errorText;
            }
        });

        waitForRunnables(vibes);
        assertThat(actualErrorText, is("No credentials"));
    }

    @Test
    public void unregisterPushSuccess() throws Exception {
        StubResult<Void> result = StubResult.success(null);
        VibesAPIInterface api = new StubAPI(result);

        Credential existingCredential = new Credential("existing-device-id", "existing-auth-token");
        StubCredentialManager credentialManager = new StubCredentialManager(existingCredential);

        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, new StubPersistentEventStorage(), new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        vibes.unregisterPush(new TestVibesListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                called = true;
            }
        });

        waitForRunnables(vibes);
        assertThat(called, is(true));
    }

    @Test
    public void unregisterPushFailure() throws Exception {
        String expectedErrorText = "Invalid app id";
        StubResult<Credential> result = StubResult.failure(404, expectedErrorText);
        VibesAPIInterface api = (VibesAPIInterface) new StubAPI(result);

        Credential existingCredential = new Credential("existing-device-id", "existing-auth-token");
        StubCredentialManager credentialManager = new StubCredentialManager(existingCredential);

        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, new StubPersistentEventStorage(), new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        vibes.unregisterPush(new TestVibesListener<Void>() {
            @Override
            public void onFailure(String errorText) {
                actualErrorText = errorText;
            }
        });

        waitForRunnables(vibes);
        assertThat(actualErrorText, is(expectedErrorText));
    }

    @Test
    public void trackLaunchEventSuccess() throws Exception {
        StubResult<Void> result = StubResult.success(null);
        StubAPI api = new StubAPI(result);

        Credential existingCredential = new Credential("existing-device-id", "existing-auth-token");
        StubCredentialManager credentialManager = new StubCredentialManager(existingCredential);
        StubPersistentEventStorage eventStorage = new StubPersistentEventStorage();

        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, eventStorage, new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        Event event = new Event(TrackedEventType.LAUNCH, new HashMap<String, String>(), new Date());
        vibes.trackEvents(Arrays.asList(event));
        assertThat(eventStorage.storedEvents().getEvents().size(), is(0));
    }

    @Test
    public void trackMultipleEventTypesSuccess() throws Exception {
        ArrayList<StubResult> results = new ArrayList<StubResult>();
        results.add(StubResult.success(null));
        results.add(StubResult.success(null));
        StubAPI api = new StubAPI(results);

        Credential existingCredential = new Credential("existing-device-id", "existing-auth-token");
        StubCredentialManager credentialManager = new StubCredentialManager(existingCredential);
        StubPersistentEventStorage eventStorage = new StubPersistentEventStorage();

        Event event1 = new Event(TrackedEventType.LAUNCH, new HashMap<String, String>(), new Date());
        Event event2 = new Event(TrackedEventType.CLICKTHRU, new HashMap<String, String>(), new Date());
        Event event3 = new Event(TrackedEventType.INBOX_OPEN, new HashMap<String, String>(), new Date());

        eventStorage.persistEvents(Collections.singletonList(event1));
        assertThat(eventStorage.storedEvents().getEvents().size(), is(1));
        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, eventStorage, new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        vibes.trackEvents(Collections.singletonList(event2));
        assertThat(eventStorage.storedEvents().getEvents().size(), is(1));
        assertThat(eventStorage.storedEvents().getEvents().get(0), is(event2));

        vibes.trackEvents(Collections.singletonList(event3));
        assertThat(eventStorage.storedEvents().getEvents().size(), is(1));
        assertThat(eventStorage.storedEvents().getEvents().get(0), is(event3));
    }

    @Test
    public void trackLaunchEventFailure() throws Exception {
        StubResult<Void> result = StubResult.failure(404, "");
        StubAPI api = new StubAPI(result);
        Credential existingCredential = new Credential("existing-device-id", "existing-auth-token");
        StubCredentialManager credentialManager = new StubCredentialManager(existingCredential);
        StubPersistentEventStorage eventStorage = new StubPersistentEventStorage();
        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, eventStorage, new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        Event event = new Event(TrackedEventType.LAUNCH, new HashMap<String, String>(), new Date());
        vibes.trackEvents(Collections.singletonList(event));

        waitForRunnables(vibes);
        assertThat(eventStorage.storedEvents().getEvents().size(), is(1));
        assertThat(eventStorage.storedEvents().getEvents().get(0), is(event));
    }

    @Test
    public void getCurrentLoggerWithLoggerSpecified() {
        VibesLogger customLogger = new MonitorLogger(VibesLogger.Level.INFO);
        VibesConfig config = new VibesConfig.Builder().setAppId(appId).setLogger(customLogger).build();
        Vibes.initialize(RuntimeEnvironment.application.getBaseContext(), config);
        assertThat(Vibes.getCurrentLogger(), is(customLogger));
    }

    @Test
    public void getCurrentLoggerWithoutLoggerSpecified() {
        Vibes.initialize(RuntimeEnvironment.application.getBaseContext(), vibesConfig);
        assertThat(Vibes.getCurrentLogger(), instanceOf(InactiveLogger.class));
    }

    /**
     * A utility function to make sure all Runnables that have been scheduled in Robolectric are
     * processed before continuing.
     */
    private void waitForRunnables(Vibes vibes) {
        VibesWorkerThread workerThread = vibes.getWorkerThread();

        Looper workerLooper = workerThread.getWorkerHandler().getLooper();
        Shadows.shadowOf(workerLooper).getScheduler().advanceToLastPostedRunnable();

        Looper responseLooper = workerThread.getResponseHandler().getLooper();
        Shadows.shadowOf(responseLooper).getScheduler().advanceToLastPostedRunnable();
    }
}
