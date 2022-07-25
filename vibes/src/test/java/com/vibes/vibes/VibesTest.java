package com.vibes.vibes;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.test.core.app.ApplicationProvider;

import com.vibes.vibes.logging.ActivityDevLogger;
import com.vibes.vibes.logging.CombinedLogger;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.annotation.LooperMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static android.os.Looper.getMainLooper;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@LooperMode(LooperMode.Mode.PAUSED)
public class VibesTest extends TestConfig {
    Credential actualCredential;
    String actualErrorText;
    boolean called = false;
    String appId = "an-app-key";
    Context context;
    private VibesConfig vibesConfig;
    HashMap<String, String> map;
    PushPayloadParser pushPayloadParser;

    @Before
    public void setUp() throws Exception {

        this.map = createPushPayload();
        this.pushPayloadParser = new PushPayloadParser(map);

        context = ApplicationProvider.getApplicationContext();
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

        vibes.updateDevice(false, null, null,  new TestVibesListener<Credential>() {
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


        vibes.updateDevice(false, 0.0, 0.0, new TestVibesListener<Credential>() {
            @Override
            public void onSuccess(Credential value) {
                actualCredential = value;
            }
        });

        waitForRunnables(vibes);
        assertThat(actualCredential, is(expectedCredential));
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

        vibes.updateDevice(false, 0.0, 0.0, new TestVibesListener<Credential>() {
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

            }
        });

        waitForRunnables(vibes);
        assertThat(credentialManager.getCurrent(), nullValue());
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
        vibes.trackEvents(Collections.singletonList(event));
        assertThat(eventStorage.storedEvents().getEvents().size(), is(1));
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

        assertThat(eventStorage.storedEvents().getEvents().size(), is(1));
        assertThat(eventStorage.storedEvents().getSortedEvents().first(), is(event));
    }

    @Test
    public void trackLaunchEventPerSecondLimit() throws Exception {
        ArrayList<StubResult> results = new ArrayList<StubResult>();
        results.add(StubResult.success(null));
        results.add(StubResult.success(null));
        StubAPI api = new StubAPI(results);

        Credential existingCredential = new Credential("existing-device-id", "existing-auth-token");
        StubCredentialManager credentialManager = new StubCredentialManager(existingCredential);
        StubPersistentEventStorage eventStorage = new StubPersistentEventStorage();

        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, eventStorage, new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        Calendar elevenAm = Calendar.getInstance();
        elevenAm.set(Calendar.HOUR_OF_DAY, 11);

        Calendar twelvePm = Calendar.getInstance();
        twelvePm.set(Calendar.HOUR_OF_DAY, 12);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(Calendar.DAY_OF_YEAR, tomorrow.get(Calendar.DAY_OF_YEAR) + 1);


        Event elevenAmEvent1 = new Event(TrackedEventType.LAUNCH, new HashMap<String, String>(), elevenAm.getTime());
        vibes.trackEvents(Collections.singletonList(elevenAmEvent1));

        assertThat(eventStorage.storedEvents().getEvents().size(), is(1));

        Event twelvePmEvent1 = new Event(TrackedEventType.LAUNCH, new HashMap<String, String>(), twelvePm.getTime());
        vibes.trackEvents(Collections.singletonList(twelvePmEvent1));

        Event tomorrowEvent1 = new Event(TrackedEventType.LAUNCH, new HashMap<String, String>(), tomorrow.getTime());
        vibes.trackEvents(Collections.singletonList(tomorrowEvent1));

        waitForRunnables(vibes);

        assertThat(eventStorage.storedEvents().getEvents().size(), is(3));

        Event tomorrowEvent2 = new Event(TrackedEventType.LAUNCH, new HashMap<String, String>(), tomorrow.getTime());
        vibes.trackEvents(Collections.singletonList(tomorrowEvent2));

        waitForRunnables(vibes);
        //this addition request will be rejected and events list size will stay the same
        assertThat(eventStorage.storedEvents().getEvents().size(), is(3));
        assertThat(eventStorage.storedEvents().getSortedEvents().first().getUUID(), not(tomorrowEvent2.getUUID()));

        tomorrow.set(Calendar.SECOND, tomorrow.get(Calendar.SECOND) + 1);
        Event tomorrowPlusSecondEvent = new Event(TrackedEventType.LAUNCH, new HashMap<String, String>(), tomorrow.getTime());
        vibes.trackEvents(Collections.singletonList(tomorrowPlusSecondEvent));

        //new addition will be accepted because it is 1 more second than the most recent record
        assertThat(eventStorage.storedEvents().getEvents().size(), is(1));
        assertThat(eventStorage.storedEvents().getSortedEvents().first().getUUID(), is(tomorrowPlusSecondEvent.getUUID()));
    }

    @Test
    public void getCurrentLoggerWithLoggerSpecified() {
        VibesLogger customLogger = new MonitorLogger(VibesLogger.Level.INFO);
        VibesConfig config = new VibesConfig.Builder().setAppId(appId).setLogger(customLogger).build();
        Vibes.initialize(ApplicationProvider.getApplicationContext(), config);
        assertEquals(Vibes.getCurrentLogger().getClass(), CombinedLogger.class);
        CombinedLogger logger = (CombinedLogger) Vibes.getCurrentLogger();
        assertEquals(logger.getCustomLogger().getClass(), MonitorLogger.class);
    }


    /**
     * A utility function to make sure all Runnables that have been scheduled in Robolectric are
     * processed before continuing.
     */
    private void waitForRunnables(Vibes vibes) {
        VibesWorkerThread workerThread = vibes.getWorkerThread();

        Looper workerLooper = workerThread.getWorkerHandler().getLooper();
        shadowOf(workerLooper).idle();

        Looper responseLooper = workerThread.getResponseHandler().getLooper();
        shadowOf(responseLooper).idle();

        shadowOf(getMainLooper()).idle();
    }

    @Test
    public void validateDefaultState() {
        VibesConfig config = new VibesConfig.Builder().setAppId(appId).build();
        Vibes.initialize(ApplicationProvider.getApplicationContext(), config);
        CombinedLogger logger = (CombinedLogger) Vibes.getCurrentLogger();
        assertEquals(logger.getConsoleLogger().getClass(), InactiveLogger.class);
    }

    @Test
    public void enableLoggingSuccess() {
        VibesConfig config = new VibesConfig.Builder().setAppId(appId).enableDevLogging(VibesLogger.Level.INFO).build();
        Vibes.initialize(ApplicationProvider.getApplicationContext(), config);
        CombinedLogger logger = (CombinedLogger) Vibes.getCurrentLogger();
        assertEquals(logger.getConsoleLogger().getClass(), ActivityDevLogger.class);
        assertEquals(2, logger.getConsoleLogger().getLogs().size());
        String message = logger.getConsoleLogger().getLogs().iterator().next();
        assertTrue(message.contains(VibesLogger.VERSION_INFO_PREFIX));
    }

    @Test
    public void disableLoggingSuccess() {
        VibesConfig config = new VibesConfig.Builder().setAppId(appId).disableDevLogging().build();
        Vibes.initialize(ApplicationProvider.getApplicationContext(), config);
        CombinedLogger logger = (CombinedLogger) Vibes.getCurrentLogger();
        assertEquals(logger.getConsoleLogger().getClass(), InactiveLogger.class);
    }

    @Test
    public void testOnPushMessageOpenedSuccess() {
        StubResult<Void> result = StubResult.failure(404, "");
        StubAPI api = new StubAPI(result);
        Credential existingCredential = new Credential("existing-device-id", "existing-auth-token");
        StubCredentialManager credentialManager = new StubCredentialManager(existingCredential);
        StubPersistentEventStorage eventStorage = new StubPersistentEventStorage();
        StubVibesReceiver receiver = new StubVibesReceiver();
        receiver.eventStorage = eventStorage;
        IntentFilter stubFilter = new IntentFilter(VibesEvent.ACTION_PUSH_OPENED);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, stubFilter);

        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, eventStorage, new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        vibes.onPushMessageOpened(pushPayloadParser, context);
        Intent intent = new Intent(VibesEvent.ACTION_PUSH_OPENED);
        intent.putExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA, map);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        waitForRunnables(vibes);

        assertThat(eventStorage.storedEvents().getEvents().size(), is(1));
        assertThat(eventStorage.storedEvents().getSortedEvents().first().getType(), is(TrackedEventType.CLICKTHRU));
    }

    @Test
    public void testOnPushMessageOpenedMapSuccess() {
        StubResult<Void> result = StubResult.failure(404, "");
        StubAPI api = new StubAPI(result);
        Credential existingCredential = new Credential("existing-device-id", "existing-auth-token");
        StubCredentialManager credentialManager = new StubCredentialManager(existingCredential);
        StubPersistentEventStorage eventStorage = new StubPersistentEventStorage();
        StubVibesReceiver receiver = new StubVibesReceiver();
        receiver.eventStorage = eventStorage;
        IntentFilter stubFilter = new IntentFilter(VibesEvent.ACTION_PUSH_OPENED);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, stubFilter);
        Vibes vibes = new Vibes(vibesConfig, api, credentialManager, eventStorage, new StubActivityLifecycleListener(), new NotificationFactory(context));
        Vibes.setInstance(vibes);

        vibes.onPushMessageOpened(map, context);
        Intent intent = new Intent(VibesEvent.ACTION_PUSH_OPENED);
        intent.putExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA, map);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        waitForRunnables(vibes);

        assertThat(eventStorage.storedEvents().getEvents().size(), is(1));
        assertThat(eventStorage.storedEvents().getSortedEvents().first().getType(), is(TrackedEventType.CLICKTHRU));
    }

    public static final HashMap<String, String> createPushPayload() {
        HashMap<String, String> map = new HashMap<>();
        map.put("message_uid", "069ef69b-6d07-de1e-a669-77cfa2d1413a");
        map.put("body", "Smart content goes here.");
        map.put("title", "Title");
        map.put("badge", "10");
        map.put("sound", "notif_sound.mp3");
        map.put("click_action", "VIBES_CHANNEL");
        map.put("priority", "high");
        map.put("vibes_collapse_id", "1223456790");

        JSONObject clientAppData = new JSONObject();
        try {
            clientAppData.put("activity_type", "Broadcast");
            clientAppData.put("activity_id", UUID.randomUUID().toString());
            clientAppData.put("activity_type", "Broadcast");
        } catch (JSONException e) {
            // do nothing
        }
        map.put("client_app_data", clientAppData.toString());
        return map;
    }
}
