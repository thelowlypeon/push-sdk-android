package com.vibes.vibes;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import java.net.HttpURLConnection;
import java.util.ArrayList;
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
     * A string literal to use when sending a clickthru events from FirebaseMessageCenter.
     */
    public static final String VIBES_REMOTE_MESSAGE_DATA = "vibesRemoteMessageData";
    private static final String TAG = "Vibes";
    /**
     * The default URL to use to talk to Vibes, if not overridden by client settings.
     */
    private static final String DEFAULT_API_URL = "https://public-api.vibescm.com/mobile_apps";
    /**
     * Default no-op logger
     */
    private static final InactiveLogger defaultLogger = new InactiveLogger();
    /**
     * Standard retry value for every api calls.
     */
    private static final Integer STD_RETRY_VALUE = 2;
    /**
     * A singleton instance of Vibes, used in production code.
     */
    private static Vibes instance;
    /**
     * Current logger
     */
    public VibesLogger logger;
    /**
     * The application id provided by Vibes to identify this application.
     */
    private String appId;
    /**
     * The URL for the Vibes API.
     */
    private String apiUrl;
    /**
     * The API to use when communicating with Vibes.
     */
    private VibesAPIInterface api;
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
     * The storage to use when tracking events
     */
    private PersistentEventStorageInterface eventStorage;

    /**
     * Initialize this object.
     *
     * @param appId             a unique application id provided by Vibes to identify this application
     * @param api               the API to use when communicating with Vibes
     * @param credentialManager the Credential Manager to use for handling Credential storage
     * @param eventStorage      The storage to use when tracking events
     * @param lifecycleListener Application lifecycle listener
     */
    protected Vibes(String appId, VibesAPIInterface api, CredentialManagerInterface credentialManager,
                    PersistentEventStorageInterface eventStorage, VibesLifecycleListener lifecycleListener) {
        this.appId = appId;
        this.api = api;
        this.credentialManager = credentialManager;
        this.eventStorage = eventStorage;
        this.lifecycleListener = lifecycleListener;
        this.lifecycleListener.eventTracker = this;
        this.workerThread = new VibesWorkerThread(new Handler());
        this.workerThread.start();
        this.workerThread.prepareHandler();
    }

    /**
     * Initialize this object. This is a convenience method that uses SharedPreferences for local
     * storage.
     *
     * @param context an application context.
     */
    private Vibes(Context context, String appId, String apiUrl, VibesLogger logger) {
        this(
                appId,
                new VibesAPI(new ResourceClient(apiUrl, logger)),
                new CredentialManager(new LocalStorage(new SharedPreferencesStorageAdapter(context))),
                new PersistentEventStorage(new LocalStorage(new SharedPreferencesStorageAdapter(context))),
                new VibesLifecycleListener()
        );
        this.apiUrl = apiUrl;
        this.logger = logger;
        Application app = (Application) context.getApplicationContext();
        app.registerActivityLifecycleCallbacks(this.lifecycleListener);
        app.registerComponentCallbacks(this.lifecycleListener);
    }

    /**
     * Static getter for the current logger.
     *
     * @return VibesLogger
     */
    public static VibesLogger getCurrentLogger() {
        return getInstance().logger;
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
            String url = bundle.getString("vibes_api_url", DEFAULT_API_URL);
            initialize(context, appId, url, defaultLogger);

            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            Vibes.setApplicationVersion(pInfo.versionName);
        } catch (NullPointerException | PackageManager.NameNotFoundException e) {
            Exception customEx = new Exception("vibes_app_id not found in AndroidManifest.xml, you must call initialize manually");
            Vibes.getCurrentLogger().log(customEx);
        }
    }

    /**
     * Initializes the Vibes SDK with the given appId.
     *
     * @param context an application context.
     * @param appId   The application id provided by Vibes to identify this application.
     */
    public static void initialize(Context context, String appId) {
        initialize(context, appId, DEFAULT_API_URL, defaultLogger);
    }

    /**
     * Initializes the Vibes SDK with the given appId.
     *
     * @param context an application context.
     * @param appId   The application id provided by Vibes to identify this application.
     * @param logger  The logger to use for http traffic.
     */
    public static void initialize(Context context, String appId, VibesLogger logger) {
        initialize(context, appId, DEFAULT_API_URL, logger);
    }

    /**
     * Set the advertising id in the static class APIDefinition. In case this method isn't called
     * the default value for the advertising id will be an empty string.
     *
     * @param advertisingId String
     */
    public static void setAdvertisingId(String advertisingId) {
        APIDefinition.advertisingId = advertisingId;
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

    /**
     * Initializes the Vibes SDK with the given appId and API URL.
     *
     * @param context an application context.
     * @param appId   The application key provided by Vibes to identify this application.
     * @param apiUrl  The API to use when communicating with Vibes.
     */
    public static void initialize(Context context, String appId, String apiUrl) {
        instance = new Vibes(context, appId, apiUrl, defaultLogger);
    }

    /**
     * Initializes the Vibes SDK with the given appId, API URL, and custom logger.
     *
     * @param context an application context.
     * @param appId   The application key provided by Vibes to identify this application.
     * @param apiUrl  The API to use when communicating with Vibes.
     * @param logger  Application logger.
     */
    public static void initialize(Context context, String appId, String apiUrl, VibesLogger logger) {
        instance = new Vibes(context, appId, apiUrl, logger);
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
        if (instance.appId == null || instance.apiUrl == null) {
            throw new RuntimeException("Vibes app id or API URL not set");
        }
        return instance;
    }

    /**
     * Reconfigure the backend endpoint and the loggger.
     *
     * @param appId  a unique application id provided by Vibes to identify this application
     * @param apiUrl The URL for the Vibes API.
     * @param logger Application logger.
     */
    public void reconfigureAPI(String appId, String apiUrl, VibesLogger logger, Boolean resetCredentials) {
        this.appId = appId;
        this.apiUrl = apiUrl;
        this.api = new VibesAPI(new ResourceClient(apiUrl, logger));

        if (resetCredentials) {
            credentialManager.setCurrent(null);
        }
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
                workerThread.onSuccess(completion, value);
            }

            @Override
            public void onFailure(int responseCode, String errorText) {
                workerThread.onFailure(completion, errorText);
            }
        };

        HTTPResource<Credential> resource = APIDefinition.registerDevice(appId);
        retryableRequest(null, resource, STD_RETRY_VALUE, listener, completion);
    }

    /**
     * Updates the device latitude and longitude with Vibes.
     */
    public void updateDeviceLatLong(Double latitude, Double longitude, final VibesListener<Credential> completion) {
        APIDefinition.setAppCoordinates(latitude, longitude);
        updateDevice(completion, true);
    }

    /**
     * Updates a device with Vibes.
     *
     * @param completion       a completion for being notified of the result of the update
     * @param updateDeviceInfo a boolean indicating if it's a token update or device info update.
     */
    private void updateDevice(final VibesListener<Credential> completion, final Boolean updateDeviceInfo) {
        Credential credential = credentialManager.getCurrent();
        if (credential == null) {
            completion.onFailure(NO_CREDENTIALS);
            return;
        }

        final ResourceListener<Credential> listener = new ResourceListener<Credential>() {
            @Override
            public void onSuccess(Credential value) {
                if (!updateDeviceInfo) {
                    credentialManager.setCurrent(value);
                }
                workerThread.onSuccess(completion, value);
            }

            @Override
            public void onFailure(int responseCode, String errorText) {
                workerThread.onFailure(completion, errorText);
            }
        };

        HTTPResource<Credential> resource = APIDefinition.updateDevice(appId, credential.getDeviceID(), updateDeviceInfo);
        if (updateDeviceInfo) {
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
                workerThread.onSuccess(completion, value);
            }

            @Override
            public void onFailure(int responseCode, String errorText) {
                workerThread.onFailure(completion, errorText);
            }
        };

        HTTPResource<Void> resource = APIDefinition.unregisterDevice(appId, credential.getDeviceID());
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
                workerThread.onSuccess(completion, value);
            }

            @Override
            public void onFailure(int responseCode, String errorText) {
                workerThread.onFailure(completion, errorText);
            }
        };

        HTTPResource<Void> resource = APIDefinition.registerPush(appId, credential.getDeviceID(), pushToken);
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
                workerThread.onSuccess(completion, value);
            }

            @Override
            public void onFailure(int responseCode, String errorText) {
                workerThread.onFailure(completion, errorText);
            }
        };

        HTTPResource<Void> resource = APIDefinition.unregisterPush(appId, credential.getDeviceID());
        retryableRequest(credential, resource, STD_RETRY_VALUE, listener, completion);
    }

    /**
     * Associates a device with the client's person identifier for Vibes.
     */
    public void associatePerson(String externalPersonId) {
        associatePerson(externalPersonId, new EmptyVibesListener<Void>());
    }

    /**
     * Associates a device with the client's person identifier for Vibes.
     *
     * @param completion a completion for being notified of the result of association
     */
    public void associatePerson(String externalPersonId, final VibesListener<Void> completion) {
        Credential credential = credentialManager.getCurrent();
        if (credential == null) {
            completion.onFailure(NO_CREDENTIALS);
            return;
        }

        final ResourceListener<Void> listener = new ResourceListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                workerThread.onSuccess(completion, value);
            }

            @Override
            public void onFailure(int responseCode, String errorText) {
                workerThread.onFailure(completion, errorText);
            }
        };

        HTTPResource<Void> resource = APIDefinition.associatePerson(appId, credential.getDeviceID(), externalPersonId);
        retryableRequest(credential, resource, STD_RETRY_VALUE, listener, completion);
    }

    /**
     * Tracks an event
     *
     * @param events the list event to track
     */
    public synchronized void trackEvents(List<Event> events) {
        this.eventStorage.persistEvents(events);
        this.uploadStoredEvents();
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
                    uploadStoredEvents();
                }

                @Override
                public void onFailure(int responseCode, String errorText) {
                    Exception e = new Exception("Error uploading stored events: " + responseCode + " - " + errorText);
                    Vibes.getCurrentLogger().log(e);
                    eventStorage.persistEvents(upload);
                }
            };
            HTTPResource<Void> resource = APIDefinition.trackEvents(appId, credential.getDeviceID(), new EventCollection(upload));
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
     * Makes a request and allows a _single_ retry if the first request returns an HTTP 401.
     *
     * @param credential the {@link Credential} to use to make the request
     * @param resource   the {@link HTTPResource} to request
     * @param nbOfRetry  the number of failure before raising an error
     * @param listener   the {@link ResourceListener} to run when the request is complete
     * @param completion the {@link VibesListener} to run as a completion if the request fails
     * @param <T>        the Type of the resource we are requesting, e.g. {@link Credential}
     */
    private <T> void retryableRequest(final Credential credential, final HTTPResource<T> resource, final int nbOfRetry,
                                      final ResourceListener<T> listener, final VibesListener<T> completion) {
        ResourceListener<T> initialListener = new ResourceListener<T>() {
            @Override
            public void onSuccess(T value) {
                workerThread.onSuccess(completion, value);
                listener.onSuccess(value);
            }

            @Override
            public void onFailure(int statusCode, String errorText) {
                if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    updateDevice(new VibesListener<Credential>() {
                        @Override
                        public void onSuccess(Credential value) {
                            workerThread.request(value.getAuthToken(), api, resource, listener);
                        }

                        @Override
                        public void onFailure(String errorText) {
                            workerThread.onFailure(completion, errorText);
                        }
                    }, false);
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
            this.workerThread.request(api, resource, initialListener);
        }
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
