package com.vibes.vibes;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.vibes.vibes.logging.LogObject;
import com.vibes.vibes.tracking.Actions;
import com.vibes.vibes.tracking.Product;
import com.vibes.vibes.tracking.Purchase;
import com.vibes.vibes.tracking.TrackingData;
import com.vibes.vibes.util.VibesUtil;
import com.vibes.vibes.versioning.GitTag;
import com.vibes.vibes.versioning.GitVersionTracker;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * The entry-point to using the Vibes SDK.
 */
public class Vibes implements EventTracker {
    /**
     * A string literal to use when an operation cannot be completed because credentials are not
     * available.
     */
    public static final String NO_CREDENTIALS = "No credentials";

    /**
     * A string literal to use when an operation cannot be completed because tracking data is not
     * available.
     */
    public static final String NO_TRACKING_DATA = "No tracking data";

    /**
     * A string literal to use when an operation cannot be completed because a person is not
     * available.
     */
    public static final String NO_PERSON_DETAILS = "No Person Details";
    /**
     * A string literal to use when sending a clickthru events from FirebaseMessageCenter.
     */
    public static final String VIBES_REMOTE_MESSAGE_DATA = "vibesRemoteMessageData";
    /**
     * Current Application Identifier for the Vibes API
     */
    static final String CURRENT_APP_ID = "CURRENT_APP_ID";
    /**
     * Standard retry value for every api calls.
     */
    private static final Integer STD_RETRY_VALUE = 2;

    /**
     * Vibes instance tag name
     */
    private static final String TAG = Vibes.class.getSimpleName();

    /**
     * A singleton instance of Vibes, used in production code.
     */
    private static Vibes instance;

    /**
     * Configuration of properties needed by Vibes
     */
    private VibesConfig vibesConfig;

    /**
     * The API to use when communicating with Vibes.
     */
    private VibesAPIInterface api;

    /**
     * The API to use when communicating tracking info with Vibes.
     */
    private VibesAPIInterface trackingApi;

    /**
     * A worker thread to handle backgrounding of network requests and reporting results back on
     * the calling thread (usually the UI thread).
     */
    private VibesWorkerThread workerThread;

    /**
     * An application lifecycle listener. Used to trigger events on app launch and
     * notification clickthru
     */
    private VibesLifecycleListener lifecycleListener;

    /**
     * The Credential Manager to use for handling Credential storage.
     */
    private CredentialManagerInterface credentialManager;

    /**
     * The Person Manager to use for handling Person storage.
     */
    private PersonManagerInterface personManager;

    /**
     * The storage to use when tracking events
     */
    private PersistentEventStorageInterface eventStorage;

    /**
     * The storage to use when tracking recent push data
     */
    private PersistentTrackingDataInterface trackingDataStorage;

    /**
     * The factory to build notifications based on payloads
     */
    private NotificationFactory notificationFactory;


    /**
     * Initialize this object.
     *
     * @param vibesConfig       configuration of Vibes properties
     * @param api               the API to use when communicating with Vibes
     * @param credentialManager the Credential Manager to use for handling Credential storage
     * @param eventStorage      The storage to use when tracking events
     * @param lifecycleListener Application lifecycle listener
     */
    protected Vibes(VibesConfig vibesConfig, VibesAPIInterface api, CredentialManagerInterface credentialManager,
                    PersistentEventStorageInterface eventStorage, VibesLifecycleListener lifecycleListener,
                    NotificationFactory notificationFactory) {
        this(vibesConfig, api, credentialManager, eventStorage, lifecycleListener, notificationFactory, null);
    }

    protected Vibes(VibesConfig vibesConfig, VibesAPIInterface api, CredentialManagerInterface credentialManager,
                    PersistentEventStorageInterface eventStorage, VibesLifecycleListener lifecycleListener,
                    NotificationFactory notificationFactory, PersonManagerInterface personManager) {
        this.vibesConfig = vibesConfig;
        this.api = api;
        this.credentialManager = credentialManager;
        this.personManager = personManager;
        this.eventStorage = eventStorage;
        this.lifecycleListener = lifecycleListener;
        this.notificationFactory = notificationFactory;
        this.lifecycleListener.eventTracker = this;
        this.workerThread = new VibesWorkerThread(new Handler());
        this.workerThread.start();
        this.workerThread.prepareHandler();
    }

    protected Vibes(VibesConfig vibesConfig, VibesAPIInterface api, VibesAPIInterface trackingApi, CredentialManagerInterface credentialManager,
                    PersistentEventStorageInterface eventStorage, PersistentTrackingDataInterface trackingDataStorage, VibesLifecycleListener lifecycleListener,
                    NotificationFactory notificationFactory) {
        this(vibesConfig, api, trackingApi, credentialManager, eventStorage, trackingDataStorage, lifecycleListener, notificationFactory, null);
    }

    protected Vibes(VibesConfig vibesConfig, VibesAPIInterface api, VibesAPIInterface trackingApi, CredentialManagerInterface credentialManager,
                    PersistentEventStorageInterface eventStorage, PersistentTrackingDataInterface trackingDataStorage, VibesLifecycleListener lifecycleListener,
                    NotificationFactory notificationFactory, PersonManagerInterface personManager) {
        this.vibesConfig = vibesConfig;
        this.api = api;
        this.trackingApi = trackingApi;
        this.credentialManager = credentialManager;
        this.personManager = personManager;
        this.eventStorage = eventStorage;
        this.trackingDataStorage = trackingDataStorage;
        this.lifecycleListener = lifecycleListener;
        this.notificationFactory = notificationFactory;
        this.lifecycleListener.eventTracker = this;
        this.workerThread = new VibesWorkerThread(new Handler());
        this.workerThread.start();
        this.workerThread.prepareHandler();
    }

    /**
     * Initialize this object. This is a convenience method that uses SharedPreferences for local
     * storage.
     *
     * @param context     an application context.
     * @param vibesConfig configuration of Vibes properties
     */
    private Vibes(Context context, VibesConfig vibesConfig) {
        this(
                vibesConfig,
                new VibesAPI(new ResourceClient(vibesConfig.getApiUrl(), vibesConfig.getLogger())),
                new VibesAPI(new ResourceClient(vibesConfig.getTrackingUrl(), vibesConfig.getLogger())),
                new CredentialManager(new LocalStorage(new SharedPreferencesStorageAdapter(context))),
                new PersistentEventStorage(new LocalStorage(new SharedPreferencesStorageAdapter(context))),
                new PersistentTrackingData(new LocalStorage(new SharedPreferencesStorageAdapter(context))),
                new VibesLifecycleListener(),
                new NotificationFactory(context),
                new PersonManager(new LocalStorage(new SharedPreferencesStorageAdapter(context)))
        );
        Application app = (Application) context.getApplicationContext();
        app.registerActivityLifecycleCallbacks(this.lifecycleListener);
        app.registerComponentCallbacks(this.lifecycleListener);
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            Vibes.setApplicationVersion(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            // No Package details found
        }
        if (BuildConfig.DEBUG) {
            checkLatestVersion();
        }

    }

    /**
     * Initializes the Vibes SDK; reads the app id from `meta-data` in the client's
     * `AndroidManifest.xml` to set `appId`.
     *
     * @param context an application context
     */
    public static void initialize(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String appId = bundle.getString("vibes_app_id");
            String url = bundle.getString("vibes_api_url");
            VibesConfig vibesConfig = new VibesConfig.Builder()
                    .setAppId(appId)
                    .setApiUrl(url)
                    .build();
            initialize(context, vibesConfig);

        } catch (IllegalStateException | NullPointerException | PackageManager.NameNotFoundException e) {
            // Vibes app id not in manifest, allow for manual initialization.
        }
    }

    /**
     * Initialize the Vibes instance.
     *
     * @param context     an application context.
     * @param vibesConfig configuration of Vibes properties
     */
    public static void initialize(Context context, VibesConfig vibesConfig) {

        instance = new Vibes(context, vibesConfig);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String savedAppId = sharedPreferences.getString(CURRENT_APP_ID, "");
        if (!savedAppId.equals(vibesConfig.getAppId())) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CURRENT_APP_ID, vibesConfig.getAppId()).apply();
            LogObject logObject = new LogObject(VibesLogger.Level.INFO, "AppId has changed to: " + vibesConfig.getAppId());
            getCurrentLogger().log(logObject);
        }
    }

    /**
     * Gets the currently-defined Vibes instance.
     *
     * @return the initialized Vibes object
     */
    public static synchronized Vibes getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Attempt to access Vibes instance before calling initialize");
        }

        return instance;
    }

    /**
     * Sets the currently-defined Vibes instance. For Testing purposes only.
     *
     * @return the initialized Vibes object
     */
    @VisibleForTesting
    static synchronized void setInstance(Vibes instance) {
        Vibes.instance = instance;
    }

    /**
     * Static getter for the current logger.
     *
     * @return VibesLogger
     */
    public static VibesLogger getCurrentLogger() {
        try {
            return getInstance().getVibesConfig().getLogger();
        } catch (Exception ex) {
            return new InactiveLogger();
        }
    }

    /**
     * Set the advertising id in the static class APIDefinition. In case this method isn't called
     * the default value for the advertising id will be an empty string.
     *
     * @param advertisingId String
     */
    public static void setAdvertisingId(String advertisingId) {
        getInstance().getVibesConfig().setAdvertisingId(advertisingId);
    }

    /**
     * Set the application version in the static class APIDefinition. In case this method isn't called
     * the default value for the advertising id will be an empty string.
     *
     * @param applicationVersion String
     */
    public static void setApplicationVersion(String applicationVersion) {
        APIDefinition.applicationVersion = applicationVersion;
    }

    private void checkLatestVersion() {
        final ResourceListener<GitTag> listener = new ResourceListener<GitTag>() {
            @Override
            public void onSuccess(GitTag value) {
                if (VibesBuild.SDK_VERSION.compareTo(value.getName()) < 1) {
                    getCurrentLogger().log(new LogObject(VibesLogger.Level.INFO, "Latest version of the Vibes SDK is " + value.getName() + ". We suggest upgrading"));
                } else {
                    getCurrentLogger().log(new LogObject(VibesLogger.Level.INFO, "Your Vibes SDK v" + VibesBuild.SDK_VERSION + " is up to date"));
                }

            }

            @Override
            public void onFailure(int responseCode, String errorText) {
                LogObject logObject = new LogObject(VibesLogger.Level.ERROR, "Unable to fetch latest version of SDK -->" + errorText);
                getCurrentLogger().log(logObject);
            }
        };

        HTTPResource<GitTag> resource = GitVersionTracker.getCurrentVersion();
        retryableRequest(null, resource, STD_RETRY_VALUE, listener, new EmptyVibesListener<GitTag>(), true);
    }

    /**
     * Returns the worker thread associated with this object. This is used from tests (thus the
     * `protected` ACL).
     */
    protected VibesWorkerThread getWorkerThread() {
        return this.workerThread;
    }

    /**
     * Registers a device with Vibes.
     */
    public void registerDevice() {
        registerDevice(new EmptyVibesListener<Credential>());
    }

    /**
     * Registers a device with Vibes.
     *
     * @param completion a completion for being notified of the result of registration
     */
    public void registerDevice(final VibesListener<Credential> completion) {
        Credential credential = credentialManager.getCurrent();
        if (credential != null) {
            completion.onSuccess(credential);
            return;
        }

        final ResourceListener<Credential> listener = new ResourceListener<Credential>() {
            @Override
            public void onSuccess(Credential value) {
                credentialManager.setCurrent(value);
                LogObject logObject = new LogObject(VibesLogger.Level.INFO, "Device registration successful");
                getCurrentLogger().log(logObject);
                workerThread.onSuccess(completion, value);
            }

            @Override
            public void onFailure(int responseCode, String errorText) {
                LogObject logObject = new LogObject(VibesLogger.Level.ERROR, errorText);
                getCurrentLogger().log(logObject);
                workerThread.onFailure(completion, errorText);
            }
        };

        HTTPResource<Credential> resource = APIDefinition.registerDevice(vibesConfig.getAppId());
        retryableRequest(null, resource, STD_RETRY_VALUE, listener, completion);
    }

    /**
     * Updates client device information with Vibes.
     *
     * @param updateCredential a boolean indicating if it's a token update or device info update. Specify false if not certain
     * @param latitude         client device latitude
     * @param longitude        client device longitude
     * @param completion       a completion for being notified of the result of the update
     */
    public void updateDevice(final boolean updateCredential, Double latitude, Double longitude, final VibesListener<Credential> completion) {
        APIDefinition.setAppCoordinates(latitude, longitude);
        Credential credential = credentialManager.getCurrent();
        if (credential == null) {
            completion.onFailure(NO_CREDENTIALS);
            return;
        }

        final ResourceListener<Credential> listener = new ResourceListener<Credential>() {
            @Override
            public void onSuccess(Credential value) {
                LogObject logObject = null;
                if (updateCredential) {
                    credentialManager.setCurrent(value);
                    logObject = new LogObject(VibesLogger.Level.INFO, "Update device and auth token successful");
                } else {
                    logObject = new LogObject(VibesLogger.Level.INFO, "Update device successful");
                }
                getCurrentLogger().log(logObject);
                workerThread.onSuccess(completion, value);
            }

            @Override
            public void onFailure(int responseCode, String errorText) {
                LogObject logObject = new LogObject(VibesLogger.Level.ERROR, errorText);
                getCurrentLogger().log(logObject);
                workerThread.onFailure(completion, errorText);
            }
        };

        HTTPResource<Credential> resource = APIDefinition.updateDevice(vibesConfig.getAppId(), credential.getDeviceID(), updateCredential);
        if (updateCredential) {
            retryableRequest(credential, resource, STD_RETRY_VALUE, listener, completion);
        } else {
            this.workerThread.request(credential.getAuthToken(), api, resource, listener);
        }
    }

    /**
     * Unregisters a device with Vibes.
     */
    public void unregisterDevice() {
        unregisterDevice(new EmptyVibesListener<Void>());
    }

    /**
     * Unregisters a device with Vibes.
     *
     * @param completion a completion for being notified of the result of unregistering
     */
    public void unregisterDevice(final VibesListener<Void> completion) {
        Credential credential = credentialManager.getCurrent();
        if (credential == null) {
            completion.onFailure(NO_CREDENTIALS);
            return;
        }

        final ResourceListener<Void> listener = new ResourceListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                credentialManager.setCurrent(null);
                LogObject logObject = new LogObject(VibesLogger.Level.INFO, "Device unregistered successfully");
                getCurrentLogger().log(logObject);
                workerThread.onSuccess(completion, value);
            }

            @Override
            public void onFailure(int responseCode, String errorText) {
                credentialManager.setCurrent(null);
                LogObject logObject = new LogObject(VibesLogger.Level.ERROR, errorText);
                getCurrentLogger().log(logObject);
                workerThread.onSuccess(completion, null);
            }
        };

        HTTPResource<Void> resource = APIDefinition.unregisterDevice(vibesConfig.getAppId(), credential.getDeviceID());
        retryableRequest(credential, resource, STD_RETRY_VALUE, listener, completion);
    }

    /**
     * Registers a device for push notifications from Vibes.
     *
     * @param pushToken the Firebase Cloud Messaging token for the device
     */
    public void registerPush(String pushToken) {
        registerPush(pushToken, new EmptyVibesListener<Void>());
    }

    /**
     * Registers a device for push notifications from Vibes.
     *
     * @param pushToken  the Firebase Cloud Messaging token for the device
     * @param completion a completion for being notified of the result of unregistering
     */
    public void registerPush(String pushToken, final VibesListener<Void> completion) {
        Credential credential = credentialManager.getCurrent();
        if (credential == null) {
            completion.onFailure(NO_CREDENTIALS);
            return;
        }

        final ResourceListener<Void> listener = new ResourceListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                LogObject logObject = new LogObject(VibesLogger.Level.INFO, "Push token registration successful");
                getCurrentLogger().log(logObject);
                workerThread.onSuccess(completion, value);
            }

            @Override
            public void onFailure(int responseCode, String errorText) {
                LogObject logObject = new LogObject(VibesLogger.Level.ERROR, errorText);
                getCurrentLogger().log(logObject);
                workerThread.onFailure(completion, errorText);
            }
        };

        HTTPResource<Void> resource = APIDefinition.registerPush(vibesConfig.getAppId(), credential.getDeviceID(), pushToken);
        retryableRequest(credential, resource, STD_RETRY_VALUE, listener, completion);
    }

    /**
     * Unregisters a device for push notifications from Vibes.
     */
    public void unregisterPush() {
        unregisterPush(new EmptyVibesListener<Void>());
    }

    /**
     * Unregisters a device for push notifications from Vibes.
     *
     * @param completion a completion for being notified of the result of unregistering
     */
    public void unregisterPush(final VibesListener<Void> completion) {
        Credential credential = credentialManager.getCurrent();
        if (credential == null) {
            completion.onFailure(NO_CREDENTIALS);
            return;
        }

        final ResourceListener<Void> listener = new ResourceListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                LogObject logObject = new LogObject(VibesLogger.Level.INFO, "Push token unregistered successfully");
                getCurrentLogger().log(logObject);
                workerThread.onSuccess(completion, value);
            }

            @Override
            public void onFailure(int responseCode, String errorText) {
                LogObject logObject = new LogObject(VibesLogger.Level.ERROR, errorText);
                getCurrentLogger().log(logObject);
                workerThread.onFailure(completion, errorText);
            }
        };

        HTTPResource<Void> resource = APIDefinition.unregisterPush(vibesConfig.getAppId(), credential.getDeviceID());
        retryableRequest(credential, resource, STD_RETRY_VALUE, listener, completion);
    }

    /**
     * Associates a device with the client's person identifier for Vibes.
     */
    public void associatePerson(@NonNull String externalPersonId) {
        if (VibesUtil.isNullOrEmpty(externalPersonId)) {
            LogObject logObject = new LogObject(VibesLogger.Level.ERROR,
                    "externalPersonId value must not be null or empty!");
            getCurrentLogger().log(logObject);
        } else {
            associatePerson(externalPersonId, new EmptyVibesListener<Void>());
        }
    }

    /**
     * Associates a device with the client's person identifier for Vibes.
     *
     * @param completion a completion for being notified of the result of association
     */
    public void associatePerson(@NonNull String externalPersonId, final VibesListener<Void> completion) {

        if (VibesUtil.isNullOrEmpty(externalPersonId)) {
            LogObject logObject = new LogObject(VibesLogger.Level.ERROR,
                    "externalPersonId value must not be null or empty!");
            getCurrentLogger().log(logObject);
            return;
        }

        Credential credential = credentialManager.getCurrent();
        if (credential == null) {
            completion.onFailure(NO_CREDENTIALS);
            return;
        }

        final ResourceListener<Void> listener = new ResourceListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                LogObject logObject = new LogObject(VibesLogger.Level.INFO, "Associate person with device successful");
                getCurrentLogger().log(logObject);
                workerThread.onSuccess(completion, value);
            }

            @Override
            public void onFailure(int responseCode, String errorText) {
                LogObject logObject = new LogObject(VibesLogger.Level.ERROR, errorText);
                getCurrentLogger().log(logObject);
                workerThread.onFailure(completion, errorText);
            }
        };

        HTTPResource<Void> resource = APIDefinition.associatePerson(vibesConfig.getAppId(), credential.getDeviceID(), externalPersonId);
        retryableRequest(credential, resource, STD_RETRY_VALUE, listener, completion);
    }

    /**
     * Tracks an event. Following rules apply.
     * 1. If the current event is a LAUNCH event and there are not previous launch events, add to storage, but don't upload.
     * 2. If the current event is a LAUNCH event and is after the most recent LAUNCH event in terms of seconds, then first trigger an upload of the previous batch of events, and then add this one to storage.
     * 3. If the current event is a LAUNCH event and is same in seconds as the most recent LAUNCH event in storage, then ignore the event.
     * 4. For any other form of event, store and upload as usual.
     *
     * @param events the list event to track
     */
    public synchronized void trackEvents(List<Event> events) {

        Event event = events.get(0);
        if (event != null && event.getType().equals(TrackedEventType.LAUNCH)) {
            Event mostRecent = this.eventStorage.getMostRecent(event.getType());
            if (mostRecent == null) {
                this.eventStorage.persistEvents(events);
            } else if (mostRecent != null && !mostRecent.getTimestampSecs().equals(event.getTimestampSecs())) {
                this.uploadStoredEvents();
                this.eventStorage.persistEvents(events);
            }
        } else {
            //upload previously persisted events which may all be of the same type e.g. LAUNCH events
            this.uploadStoredEvents();
            //persist this new type
            this.eventStorage.persistEvents(events);
            //upload new type
            this.uploadStoredEvents();
        }

    }

    /**
     * Uploads the locally stored events to the Vibes API.
     */
    public synchronized void uploadStoredEvents() {
        ArrayList<Event> stored = new ArrayList<>(this.eventStorage.storedEvents().getEvents());
        if (stored.isEmpty()) {
            return;
        }

        final ArrayList<Event> upload = new ArrayList<>();
        TrackedEventType uploadType = stored.get(0).getType();
        for (Event event : stored) {
            if (event.getType() == uploadType) {
                upload.add(event);
                LogObject logObject = new LogObject(VibesLogger.Level.INFO, event.getType() + " event uploaded successfully");
                getCurrentLogger().log(logObject);
                eventStorage.removeEvent(event);
            }
        }

        if (upload.isEmpty()) {
            return;
        }

        Credential credential = credentialManager.getCurrent();
        if (credential != null) {
            final ResourceListener<Void> listener = new ResourceListener<Void>() {
                @Override
                public void onSuccess(Void value) {
                    //do nothing here
                }

                @Override
                public void onFailure(int responseCode, String errorText) {
                    Exception e = new Exception("Error uploading stored events: " + responseCode + " - " + errorText);
                    Vibes.getCurrentLogger().log(e);
                    eventStorage.persistEvents(upload);
                }
            };
            HTTPResource<Void> resource = APIDefinition.trackEvents(getVibesConfig().getAppId(), credential.getDeviceID(), new EventCollection(upload));
            retryableRequest(credential, resource, 0, listener, new EmptyVibesListener<Void>());
        } else {
            Exception e = new Exception("Credentials not found, unable to upload stored events");
            Vibes.getCurrentLogger().log(e);
        }
    }

    public PushPayloadParser createPushPayloadParser(Map<String, String> map) {
        return new PushPayloadParser(map);
    }

    /**
     * Sames as {@link #retryableRequest(Credential, HTTPResource, int, ResourceListener, VibesListener, boolean)} with <code>ignoreBaseUrl</code> set to false.
     */
    private <T> void retryableRequest(final Credential credential, final HTTPResource<T> resource, final int nbOfRetry,
                                      final ResourceListener<T> listener, final VibesListener<T> completion) {
        this.retryableRequest(credential, resource, nbOfRetry, listener, completion, false);
    }

    /**
     * Makes a request and allows a _single_ retry if the first request returns an HTTP 401.
     *
     * @param credential    the {@link Credential} to use to make the request
     * @param resource      the {@link HTTPResource} to request
     * @param nbOfRetry     the number of failure before raising an error
     * @param listener      the {@link ResourceListener} to run when the request is complete
     * @param completion    the {@link VibesListener} to run as a completion if the request fails
     * @param <T>           the Type of the resource we are requesting, e.g. {@link Credential}
     * @param ignoreBaseUrl enables the invocation of a non-vibes URL
     */
    private <T> void retryableRequest(final Credential credential, final HTTPResource<T> resource, final int nbOfRetry,
                                      final ResourceListener<T> listener, final VibesListener<T> completion, boolean ignoreBaseUrl) {
        ResourceListener<T> initialListener = new ResourceListener<T>() {
            @Override
            public void onSuccess(T value) {
                workerThread.onSuccess(completion, value);
                listener.onSuccess(value);
            }

            @Override
            public void onFailure(int statusCode, String errorText) {
                if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    updateDevice(true, null, null, new VibesListener<Credential>() {
                        @Override
                        public void onSuccess(Credential value) {
                            workerThread.request(value.getAuthToken(), api, resource, listener);
                        }

                        @Override
                        public void onFailure(String errorText) {
                            workerThread.onFailure(completion, errorText);
                        }
                    });
                } else if (nbOfRetry > 0 && statusCode == VibesRequestError.TIMEOUT.getCode()) {
                    sleepRandomQuietly(15);
                    retryableRequest(credential, resource, (nbOfRetry - 1), listener, completion);
                } else {
                    listener.onFailure(statusCode, errorText);
                }
            }
        };

        if (credential != null) {
            this.workerThread.request(credential.getAuthToken(), api, resource, initialListener);
        } else {
            this.workerThread.request(api, resource, initialListener, ignoreBaseUrl);
        }
    }

    /**
     * Makes a request to the ecommerce tracking endpoints and retries if there's a failure.
     *
     * @param resource      the {@link HTTPResource} to request
     * @param nbOfRetry     the number of failure before raising an error
     * @param listener      the {@link ResourceListener} to run when the request is complete
     * @param completion    the {@link VibesListener} to run as a completion if the request fails
     * @param <T>           the Type of the resource we are requesting, e.g. {@link Credential}
     * @param ignoreBaseUrl enables the invocation of a non-vibes URL
     */
    private <T> void retryableTrackingRequest(final HTTPResource<T> resource, final int nbOfRetry,
                                              final ResourceListener<T> listener, final VibesListener<T> completion, final boolean ignoreBaseUrl) {
        ResourceListener<T> initialListener = new ResourceListener<T>() {
            @Override
            public void onSuccess(T value) {
                workerThread.onSuccess(completion, value);
                listener.onSuccess(value);
            }

            @Override
            public void onFailure(int statusCode, String errorText) {
                if (nbOfRetry > 0 && statusCode == VibesRequestError.TIMEOUT.getCode()) {
                    sleepRandomQuietly(15);
                    retryableTrackingRequest(resource, (nbOfRetry - 1), listener, completion, ignoreBaseUrl);
                } else {
                    listener.onFailure(statusCode, errorText);
                }
            }
        };
        this.workerThread.request(trackingApi, resource, initialListener, ignoreBaseUrl);
    }

    /**
     * Method used to have a random delay between 2 retryable requests.
     *
     * @param value: maximum nb of sec to wait. This value will be used as a parameter for the
     *               randomisation.
     */
    private void sleepRandomQuietly(int value) {
        try {
            Random rand = new Random();
            int timeToSleep = rand.nextInt(value);
            TimeUnit.SECONDS.sleep(timeToSleep);
        } catch (InterruptedException e) {
            Vibes.getCurrentLogger().log(e);
        }
    }

    /**
     * Set a custom notification factory for customizing the notifications built
     *
     * @param notificationFactory - the factory to be set
     */
    public void setNotificationFactory(NotificationFactory notificationFactory) {
        this.notificationFactory = notificationFactory;
    }

    /**
     * Creates notification and posts it, if the notification is not silent.
     *
     * @param context - used to build the notification and notify the system
     * @param payload - the data payload from a Firebase RemoteMessage
     */
    public void handleNotification(Context context, Map<String, String> payload) {
        PushPayloadParser pushModel = createPushPayloadParser(payload);
        broadcastPushReceived(context, pushModel);
        Notification notification = notificationFactory.build(pushModel, context);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (!pushModel.isSilentPush() && notificationManager != null) {
            int id = pushModel.getVibesCollapseId() != null ? pushModel.getVibesCollapseId().hashCode() : pushModel.hashCode();
            notificationManager.notify(id, notification);
        } else if (pushModel.isMigrationPush()) {
            String pushToken = pushModel.getVibesAutoRegisterToken();
            getCurrentLogger().log(new LogObject(VibesLogger.Level.INFO, "Migration push received. Processing ..."));
            handleRegistration(pushToken, pushModel.getMigrationItemId());
        }
    }

    /**
     * A helper class to register device and push
     *
     * @param token           the firebase token generated on client device
     * @param migrationItemId the migration_item_id to track the migration item
     */
    private void handleRegistration(final String token, final String migrationItemId) {
        this.registerDevice(new VibesListener<Credential>() {
            @Override
            public void onSuccess(Credential value) {
                Vibes.getInstance().registerPush(token);
                migrationCallback(migrationItemId, value.getDeviceID());
                LogObject logObject = new LogObject(VibesLogger.Level.INFO, "Migration request completed successfully");
                getCurrentLogger().log(logObject);
            }

            @Override
            public void onFailure(String errorText) {
                LogObject logObject = new LogObject(VibesLogger.Level.ERROR, "Migration attempt failed in triggering registerDevice(): " + errorText);
                getCurrentLogger().log(logObject);
            }
        });
    }

    private void migrationCallback(String migrationItemId, String deviceId) {
        Credential credential = credentialManager.getCurrent();

        final ResourceListener<Void> listener = new ResourceListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                LogObject logObject = new LogObject(VibesLogger.Level.INFO, "Migration Callback Successful");
                getCurrentLogger().log(logObject);
            }

            @Override
            public void onFailure(int statusCode, String errorText) {
                LogObject logObject = new LogObject(VibesLogger.Level.ERROR, "Couldn't process migration callback: " + errorText);
                getCurrentLogger().log(logObject);
            }
        };

        HTTPResource<Void> resource = APIDefinition.migrationCallback(migrationItemId, deviceId, vibesConfig.getAppId());
        retryableRequest(credential, resource, STD_RETRY_VALUE, listener, new EmptyVibesListener<Void>());
    }

    /**
     * Broadcast that a push notification was received so that BroadcastReceivers can
     * respond to the action.
     *
     * @param context           - Needed to broadcast the event
     * @param pushPayloadParser - the pushModel for the notification received
     */
    private void broadcastPushReceived(Context context, PushPayloadParser pushPayloadParser) {
        String packageName = context.getApplicationInfo().packageName;
        Intent intent = new Intent(VibesEvent.ACTION_PUSH_RECEIVED)
                .addCategory(packageName)
                .setPackage(packageName);
        intent.putExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA, pushPayloadParser.getMap());
        intent.addCategory(context.getPackageName());
        context.sendBroadcast(intent);
    }

    /**
     * @return vibesConfig configuration of Vibes properties
     */
    public VibesConfig getVibesConfig() {
        return vibesConfig;
    }

    /**
     * Get the person associated with the device
     *
     * @param completion a completion for being notified of the result of retrieving the person
     */
    public void getPerson(final VibesListener<Person> completion) {
        Credential credential = credentialManager.getCurrent();
        if (credential == null) {
            completion.onFailure(NO_CREDENTIALS);
            return;
        }
        final ResourceListener<Person> listener = new ResourceListener<Person>() {

            @Override
            public void onSuccess(Person value) {
                LogObject logObject = new LogObject(VibesLogger.Level.INFO, "Successfully retrieved person details");
                getCurrentLogger().log(logObject);
                workerThread.onSuccessful(completion, value);
                personManager.setCurrent(value);
            }

            @Override
            public void onFailure(int statusCode, String errorText) {
                LogObject logObject = new LogObject(VibesLogger.Level.ERROR, errorText);
                getCurrentLogger().log(logObject);
                workerThread.onFailure(completion, errorText);
            }
        };

        HTTPResource<Person> resource = APIDefinition.getPerson(vibesConfig.getAppId(), credential.getDeviceID());
        retryableRequest(credential, resource, STD_RETRY_VALUE, listener, completion);
    }

    /**
     * Fetch Inbox messages associated with the current person
     *
     * @param completion a listener to notify of the result on fetching messages
     */
    public void fetchInboxMessages(final VibesListener<Collection<InboxMessage>> completion) {
        getPerson(new VibesListener<Person>() {
            @Override
            public void onSuccess(Person personValue) {
                Credential credential = credentialManager.getCurrent();
                if (credential == null) {
                    completion.onFailure(NO_CREDENTIALS);
                    return;
                }
                final ResourceListener<Collection<InboxMessage>> listener = new ResourceListener<Collection<InboxMessage>>() {

                    @Override
                    public void onSuccess(Collection<InboxMessage> value) {
                        LogObject logObject = new LogObject(VibesLogger.Level.INFO, "Inbox messages fetched successfully");
                        getCurrentLogger().log(logObject);
                        workerThread.onSuccess(completion, value);
                    }

                    @Override
                    public void onFailure(int statusCode, String errorText) {
                        LogObject logObject = new LogObject(VibesLogger.Level.ERROR, errorText);
                        getCurrentLogger().log(logObject);
                        workerThread.onFailure(completion, errorText);
                    }
                };
                HTTPResource<Collection<InboxMessage>> resource = APIDefinition.fetchInboxMessages(personValue.getPersonKey(), vibesConfig.getAppId());
                retryableRequest(credential, resource, STD_RETRY_VALUE, listener, completion);
            }

            @Override
            public void onFailure(String errorText) {
                LogObject logObject = new LogObject(VibesLogger.Level.ERROR, errorText);
                getCurrentLogger().log(logObject);
                workerThread.onFailure(completion, errorText);
            }
        });
    }

    /**
     * Fetch one Inbox Message by messageUid
     *
     * @param messageUid
     * @param completion
     */
    public void fetchInboxMessage(@NonNull final String messageUid, final VibesListener<InboxMessage> completion) {
        if (VibesUtil.isNullOrEmpty(messageUid)) {
            LogObject logObject = new LogObject(VibesLogger.Level.ERROR,
                    "messageUid value must not be null or empty!");
            getCurrentLogger().log(logObject);
            return;
        }
        getPerson(new VibesListener<Person>() {
            @Override
            public void onSuccess(Person personValue) {
                Credential credential = credentialManager.getCurrent();
                if (credential == null) {
                    completion.onFailure(NO_CREDENTIALS);
                    return;
                }
                final ResourceListener<InboxMessage> listener = new ResourceListener<InboxMessage>() {
                    @Override
                    public void onSuccess(InboxMessage value) {
                        LogObject logObject = new LogObject(VibesLogger.Level.INFO, "Single inbox message fetched successfully");
                        getCurrentLogger().log(logObject);
                        workerThread.onSuccess(completion, value);
                    }

                    @Override
                    public void onFailure(int statusCode, String errorText) {
                        LogObject logObject = new LogObject(VibesLogger.Level.ERROR, errorText);
                        getCurrentLogger().log(logObject);
                        workerThread.onFailure(completion, errorText);
                    }
                };
                HTTPResource<InboxMessage> resource = APIDefinition.fetchInboxMessage(personValue.getPersonKey(), vibesConfig.getAppId(), messageUid);
                retryableRequest(credential, resource, STD_RETRY_VALUE, listener, completion);
            }

            @Override
            public void onFailure(String errorText) {
                LogObject logObject = new LogObject(VibesLogger.Level.ERROR, errorText);
                getCurrentLogger().log(logObject);
                workerThread.onFailure(completion, errorText);
            }
        });
    }

    /**
     * Update the read status of a given message to true
     *
     * @param messageId  - The messageId to filter and update
     * @param completion - a completion for being notified of the result of the update
     */
    public void markInboxMessageAsRead(@NonNull final String messageId, final VibesListener<InboxMessage> completion) {
        if (VibesUtil.isNullOrEmpty(messageId)) {
            LogObject logObject = new LogObject(VibesLogger.Level.ERROR,
                    "messageId value must not be null or empty!");
            getCurrentLogger().log(logObject);
            return;
        }
        final Credential credential = credentialManager.getCurrent();
        final Person person = personManager.getCurrent();
        if (credential == null) {
            completion.onFailure(NO_CREDENTIALS);
            return;
        }
        if (person == null) {
            getPerson(new VibesListener<Person>() {
                @Override
                public void onSuccess(Person value) {
                    markInboxMessageAsReadCall(credential, value, messageId, completion);
                }

                @Override
                public void onFailure(String errorText) {
                    completion.onFailure(errorText);
                }
            });
            return;
        }
        markInboxMessageAsReadCall(credential, person, messageId, completion);
    }

    /**
     * A private method to handle marking the message as read.
     *
     * @param credential
     * @param person
     * @param messageId
     * @param completion
     */
    private void markInboxMessageAsReadCall(Credential credential, Person person, String messageId, final VibesListener<InboxMessage> completion) {
        final ResourceListener<InboxMessage> listener = new ResourceListener<InboxMessage>() {
            @Override
            public void onSuccess(InboxMessage value) {
                LogObject logObject = new LogObject(VibesLogger.Level.INFO, "Inbox message marked as read");
                getCurrentLogger().log(logObject);
                workerThread.onSuccess(completion, value);
            }

            @Override
            public void onFailure(int statusCode, String errorText) {
                LogObject logObject = new LogObject(VibesLogger.Level.ERROR, errorText);
                getCurrentLogger().log(logObject);
                workerThread.onFailure(completion, errorText);
            }
        };

        HTTPResource<InboxMessage> resource = APIDefinition.markInboxMessageAsRead(person.getPersonKey(), messageId, vibesConfig.getAppId());
        retryableRequest(credential, resource, STD_RETRY_VALUE, listener, completion);
    }

    /**
     * Updates the inbox message matching the provided messageId with an expiry matching the provided date.
     *
     * @param messageId
     * @param expirationDate
     * @param completion
     */
    public void expireInboxMessage(@NonNull final String messageId, @NonNull final Date expirationDate, final VibesListener<InboxMessage> completion) {
        if (VibesUtil.isNullOrEmpty(messageId)) {
            LogObject logObject = new LogObject(VibesLogger.Level.ERROR,
                    "messageId value must not be null or empty!");
            getCurrentLogger().log(logObject);
            return;
        }
        final Credential credential = credentialManager.getCurrent();
        final Person person = personManager.getCurrent();
        if (credential == null) {
            completion.onFailure(NO_CREDENTIALS);
            return;
        }
        if (person == null) {
            getPerson(new VibesListener<Person>() {
                @Override
                public void onSuccess(Person value) {
                    expireInboxMessageCall(messageId, expirationDate, completion, value, credential);
                }

                @Override
                public void onFailure(String errorText) {
                    completion.onFailure(errorText);
                }
            });
            return;
        }

        expireInboxMessageCall(messageId, expirationDate, completion, person, credential);
    }

    /**
     * A private method to handle expiration of the message.
     *
     * @param messageId
     * @param expirationDate
     * @param completion
     * @param person
     * @param credential
     */
    private void expireInboxMessageCall(String messageId, Date expirationDate, final VibesListener<InboxMessage> completion, Person person, Credential credential) {
        String date = ISODateFormatter.toISOString(expirationDate);
        final ResourceListener<InboxMessage> listener = new ResourceListener<InboxMessage>() {
            @Override
            public void onSuccess(InboxMessage value) {
                LogObject logObject = new LogObject(VibesLogger.Level.INFO, "Inbox message expired successfully");
                getCurrentLogger().log(logObject);
                workerThread.onSuccess(completion, value);
            }

            @Override
            public void onFailure(int statusCode, String errorText) {
                LogObject logObject = new LogObject(VibesLogger.Level.ERROR, errorText);
                getCurrentLogger().log(logObject);
                workerThread.onFailure(completion, errorText);
            }
        };
        HTTPResource<InboxMessage> resource = APIDefinition.expireInboxMessage(person.getPersonKey(), messageId, date, vibesConfig.getAppId());
        retryableRequest(credential, resource, STD_RETRY_VALUE, listener, completion);
    }

    /**
     * Immediately expires the inbox message matching the provided messageId
     *
     * @param messageId  The messageId to filter and update
     * @param completion a completion for being notified of the result of the update
     */
    public void expireInboxMessage(@NonNull String messageId, final VibesListener<InboxMessage> completion) {
        if (VibesUtil.isNullOrEmpty(messageId)) {
            LogObject logObject = new LogObject(VibesLogger.Level.ERROR,
                    "messageId value must not be null or empty!");
            getCurrentLogger().log(logObject);
            return;
        }
        expireInboxMessage(messageId, new Date(), completion);
    }

    /**
     * Records the fact that an inbox message has been viewed in detail by the user.
     *
     * @param message the viewed message
     */
    public void onInboxMessageOpen(InboxMessage message) {
        Event event = new Event(TrackedEventType.INBOX_OPEN, message.getEventsMap());
        Vibes.getInstance().trackEvents(Collections.singletonList(event));
    }

    /**
     * Records the fact that inbox messages have been viewed as a list by a user.
     */
    public void onInboxMessagesFetched() {
        Event event = new Event(TrackedEventType.INBOX_FETCH);
        Vibes.getInstance().trackEvents(Collections.singletonList(event));
    }

    /**
     * Fetches vibes app details including the inbox_enabled status as well as the app_id
     *
     * @param completion callback for being notified of the result
     */
    public void getAppInfo(final VibesListener<VibesAppInfo> completion) {
        Credential credential = credentialManager.getCurrent();

        final ResourceListener<VibesAppInfo> listener = new ResourceListener<VibesAppInfo>() {
            @Override
            public void onSuccess(VibesAppInfo value) {
                if (value.isInboxEnabled()) {
                    LogObject logObject = new LogObject(VibesLogger.Level.INFO, "App Inbox is enabled");
                    getCurrentLogger().log(logObject);
                } else {
                    LogObject logObject = new LogObject(VibesLogger.Level.WARN, "App Inbox is not enabled");
                    getCurrentLogger().log(logObject);
                }
                workerThread.onSuccess(completion, value);
            }

            @Override
            public void onFailure(int statusCode, String errorText) {
                Log.d(TAG, "onFailure ------>: " + errorText);
                workerThread.onFailure(completion, errorText);
            }
        };
        HTTPResource<VibesAppInfo> resource = APIDefinition.fetchVibesAppInfo(vibesConfig.getAppId());
        retryableRequest(credential, resource, STD_RETRY_VALUE, listener, completion);
    }

    /**
     * private method internally to check the status of the app and log appropriately
     * Called after initialization to cater for cases when the user may not explicitly invoke the
     * public method.
     */
    private void checkInboxStatus() {
        this.getAppInfo(new EmptyVibesListener<VibesAppInfo>());
    }

    /**
     * Records the fact that a push message has been opened by a user.
     *
     * @param map     the viewed message
     * @param context the application context
     */
    public void onPushMessageOpened(HashMap<String, String> map, Context context) {
        //In Android 12 and above we can't guarantee that VibesReceiver will be called, so we record clickthru here before we proceed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Vibes.getCurrentLogger().log(new LogObject(VibesLogger.Level.INFO, "Developer says push message was opened. Reporting a clickthru event"));
            Event event = new Event(TrackedEventType.CLICKTHRU, map);
            Vibes.getInstance().trackEvents(Collections.singletonList(event));
            storeTrackingData(map);
        } else {
            Intent intent = new Intent(VibesEvent.ACTION_PUSH_OPENED);
            intent.putExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA, map);
            context.sendBroadcast(intent);
        }
    }

    /**
     * Records the fact that a push message has been opened by a user.
     *
     * @param payloadParser the viewed message
     * @param context       the application context
     */
    public void onPushMessageOpened(PushPayloadParser payloadParser, Context context) {
        onPushMessageOpened(payloadParser.getMap(), context);
    }

    /**
     * Stores the push tracking data to LocalStorage
     *
     * @param pushPayload the push data to be saved to LocalStorage
     */
    protected synchronized void storeTrackingData(final Map<String, String> pushPayload) {
        String clientData = pushPayload.get(PushPayloadParser.kClientAppData);
        final Gson gson = new Gson();
        final TrackingData trackingData = gson.fromJson(clientData, TrackingData.class);

        getPerson(new VibesListener<Person>() {
            @Override
            public void onSuccess(Person value) {
                String personId = value.getPersonKey();
                trackingData.setPersonId(personId);

                String jsonString = gson.toJson(trackingData, TrackingData.class);
                trackingDataStorage.persistTrackingData(jsonString);
            }

            @Override
            public void onFailure(String errorText) {
                getCurrentLogger().log(new LogObject(VibesLogger.Level.ERROR, "Error fetching person: " + errorText));
                trackingData.setPersonId(null);

                String jsonString = gson.toJson(trackingData, TrackingData.class);
                trackingDataStorage.persistTrackingData(jsonString);
            }
        });
    }

    /**
     * Retrieves the most recent push tracking data received on device as push notification client data
     *
     * @return Tracking data object
     */
    public TrackingData getTrackingData() {
        return trackingDataStorage.getTrackingData();
    }

    /**
     * Tracks the clicking of a particular product, and linking it to a push notification triggered by a particular activity on the Vibes Platform.
     *
     * @param product     represents the details of a product that has been clicked
     * @param activityUid an optional parameter representing an an activityUid linked to this push message
     */
    public void trackClick(@NonNull Product product, @Nullable String activityUid, final VibesListener<Void> completion) {
        trackActionCall(Actions.ProductAction.CLICK, product, activityUid, completion);
    }

    /**
     * Tracks the clicking of a particular product, and linking it to the most recent push notification stored within the SDK.
     *
     * @param product represents the details of a product that has been clicked
     * @see {@link Vibes#trackClick(Product, String, VibesListener)}
     */
    public void trackClick(@NonNull Product product, final VibesListener<Void> completion) {
        trackClick(product, null, completion);
    }

    /**
     * Tracks the adding of a particular product to a cart, and linking it to a push notification triggered by a particular activity on the Vibes Platform.
     *
     * @param product     represents the details of a product that has been clicked
     * @param activityUid an optional parameter representing an an activityUid linked to this push message
     */
    public void trackAdd(@NonNull Product product, @Nullable String activityUid, final VibesListener<Void> completion) {
        trackActionCall(Actions.ProductAction.ADD, product, activityUid, completion);
    }

    /**
     * Tracks the adding of a particular product to a cart, and linking it to the most recent push notification stored on the Vibes Platform.
     *
     * @param product represents the details of a product that has been clicked
     * @see {@link Vibes#trackClick(Product, String, VibesListener)}
     */
    public void trackAdd(@NonNull Product product, final VibesListener<Void> completion) {
        trackActionCall(Actions.ProductAction.ADD, product, null, completion);
    }

    /**
     * Tracks the viewing of details of a particular product, and linking it to a push notification triggered by a particular activity on the Vibes Platform.
     *
     * @param product     represents the details of a product that has been clicked
     * @param activityUid an optional parameter representing an an activityUid linked to this push message
     */
    public void trackDetail(@NonNull Product product, @Nullable String activityUid, final VibesListener<Void> completion) {
        trackActionCall(Actions.ProductAction.DETAIL, product, activityUid, completion);
    }

    /**
     * Tracks the viewing of details of a particular product, and linking it to the most recent push notification stored on the Vibes Platform.
     *
     * @param product represents the details of a product that has been clicked
     * @see {@link Vibes#trackDetail(Product, String, VibesListener)}
     */
    public void trackDetail(@NonNull Product product, final VibesListener<Void> completion) {
        trackActionCall(Actions.ProductAction.DETAIL, product, null, completion);
    }

    /**
     * Tracks the purchasing of a particular product, and linking it to the most recent push notification stored within the SDK.
     *
     * @param purchase represents the details of a purchase that has transacted
     */
    public void trackPurchase(@NonNull Purchase purchase, final VibesListener<Void> completion) {
        trackPurchase(purchase, null, completion);
    }

    /**
     * Tracks the purchasing of a particular product, and linking it to a push notification triggered by a particular activity on the Vibes Platform.
     *
     * @param purchase    represents the details of a purchase that has transacted
     * @param activityUid an optional parameter representing an an activityUid linked to this push message
     */
    public void trackPurchase(@NonNull Purchase purchase, @Nullable String activityUid, final VibesListener<Void> completion) {
        trackPurchaseActionCall(Actions.PurchaseAction.PURCHASE, purchase, activityUid, completion);
    }

    /**
     * A private method to make the ecommerce tracking event related to products.
     *
     * @param action      the type of {@link Actions.ProductAction}
     * @param product     the product being tracked
     * @param activityUid optional activityUid
     * @param completion
     */
    private void trackActionCall(final Actions.ProductAction action, Product product, String activityUid, final VibesListener<Void> completion) {
        TrackingData data = getTrackingData();
        if (data == null) {
            LogObject logObject = new LogObject(VibesLogger.Level.WARN, "No tracking information exists to use in click action call");
            getCurrentLogger().log(logObject);
            completion.onFailure(NO_TRACKING_DATA);
            return;
        }
        getCurrentLogger().log(new LogObject(VibesLogger.Level.INFO, "Attempting to track event -> " + action.getActionName()));

        final ResourceListener<Void> listener = new ResourceListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                LogObject logObject = new LogObject(VibesLogger.Level.INFO, action + " ecommerce tracking successful");
                getCurrentLogger().log(logObject);
                workerThread.onSuccess(completion, value);
            }

            @Override
            public void onFailure(int statusCode, String errorText) {
                LogObject logObject = new LogObject(VibesLogger.Level.ERROR, action + " ecommerce tracking failed. " + errorText);
                getCurrentLogger().log(logObject);
                workerThread.onFailure(completion, errorText);
            }
        };

        HTTPResource<Void> resource = APIDefinition.trackProductAction(action, product, data, activityUid);
        retryableTrackingRequest(resource, STD_RETRY_VALUE, listener, completion, false);
    }

    /**
     * A private method to make the ecommerce tracking event related to purchases.
     *
     * @param action      the type of {@link Actions.PurchaseAction
     * @param purchase    the purchase being tracked
     * @param activityUid optional activityUid
     * @param completion
     */
    private void trackPurchaseActionCall(final Actions.PurchaseAction action, Purchase purchase, String activityUid, final VibesListener<Void> completion) {
        TrackingData data = getTrackingData();
        if (data == null) {
            LogObject logObject = new LogObject(VibesLogger.Level.WARN, "No tracking information exists to use in purchase action call");
            getCurrentLogger().log(logObject);
            completion.onFailure(NO_TRACKING_DATA);
            return;
        }
        getCurrentLogger().log(new LogObject(VibesLogger.Level.INFO, "Attempting to track event -> " + action.getActionName()));

        final ResourceListener<Void> listener = new ResourceListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                LogObject logObject = new LogObject(VibesLogger.Level.INFO, action + " ecommerce tracking successful");
                getCurrentLogger().log(logObject);
                workerThread.onSuccess(completion, value);
            }

            @Override
            public void onFailure(int statusCode, String errorText) {
                LogObject logObject = new LogObject(VibesLogger.Level.ERROR, action + " ecommerce tracking failed. " + errorText);
                getCurrentLogger().log(logObject);
                workerThread.onFailure(completion, errorText);
            }
        };

        HTTPResource<Void> resource = APIDefinition.trackPurchaseAction(action, purchase, data, activityUid);
        retryableTrackingRequest(resource, STD_RETRY_VALUE, listener, completion, false);
    }

    /**
     * An empty VibesListener to allow optionality when calling Vibes functionality. Used when the
     * consumer of the call doesn't care about the result of the call.
     *
     * @param <T> the Type that this Listener is generic over, e.g. {@link Credential}
     */
    private class EmptyVibesListener<T> implements VibesListener<T> {
        @Override
        public void onSuccess(T value) {
        }

        @Override
        public void onFailure(String errorText) {
        }
    }
}
