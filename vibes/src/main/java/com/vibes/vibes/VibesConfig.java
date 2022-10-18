package com.vibes.vibes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vibes.vibes.logging.ActivityDevLogger;
import com.vibes.vibes.logging.CombinedLogger;
import com.vibes.vibes.logging.LogObject;
import com.vibes.vibes.util.VibesUtil;

import static com.vibes.vibes.Vibes.getCurrentLogger;

/**
 * Model to describe the initialization of {@link Vibes}
 */
public class VibesConfig {

    /**
     * The default URL to use to talk to Vibes, if not overridden by client settings.
     */
    static final String DEFAULT_API_URL = "https://public-api.vibescm.com/mobile_apps";

    /**
     * The default URL to use to talk to EU Vibes environment, if not overridden by client settings.
     */
    static final String EUROPE_API_URL = " https://public-api.vibescmeurope.com/mobile_apps";

    /**
     * The default URL to use to send tracking info to Vibes, if not overridden by client settings.
     */
    static final String DEFAULT_TACKING_URL = "https://tagtracking.vibescm.com/track";

    /**
     * The default URL to use to send tracking info to EU Vibes environment
     */
    static final String EUROPE_TACKING_URL = "https://tagtracking.eu.vibes.com/track";

    /**
     * The default VibesLogger to use, if not overridden by client settings.
     */
    static final VibesLogger DEFAULT_LOGGER = new InactiveLogger();

    /**
     * Logger implementation provided
     */
    private VibesLogger logger;

    /**
     * Base URL for communication with the Vibes API
     */
    private String apiUrl;

    /**
     * Base URL for communication with the Vibes API
     */
    private String trackingUrl;

    /**
     * Application Identifier for the Vibes API
     */
    private String appId;

    /**
     * Advertising ID to correlate user
     */
    @Nullable
    private String advertisingId;

    private VibesConfig() {
    }

    @Nullable
    public String getAdvertisingId() {
        return advertisingId;
    }

    public void setAdvertisingId(@Nullable String advertisingId) {
        this.advertisingId = advertisingId;
    }

    public VibesLogger getLogger() {
        return logger;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public String getAppId() {
        return appId;
    }

    /**
     * Builder to construct the Vibes Configuration
     */
    public static class Builder {
        private String advertisingId;
        private CombinedLogger logger;
        private VibesLogger consoleLogger = new InactiveLogger();
        private VibesLogger customLogger = new InactiveLogger();
        private String appId;
        private String apiUrl = DEFAULT_API_URL;
        private String trackingUrl = DEFAULT_TACKING_URL;

        public Builder setAdvertisingId(String advertisingId) {
            this.advertisingId = advertisingId;
            return this;
        }

        public Builder setLogger(VibesLogger logger) {
            if (logger != null) {
                this.customLogger = logger;
            } else {
                this.customLogger = DEFAULT_LOGGER;
            }
            return this;
        }

        public Builder setAppId(@NonNull String appId) {
            if (VibesUtil.isNullOrEmpty(appId)) {
                String message = "AppId must not be null or empty";
                LogObject logObject = new LogObject(VibesLogger.Level.ERROR, message);
                getCurrentLogger().log(logObject);
                throw new IllegalStateException(message);
            }
            this.appId = appId;
            return this;
        }

        public Builder setApiUrl(String apiUrl) {
            if (apiUrl != null) {
                this.apiUrl = apiUrl;
            } else {
                LogObject logObject = new LogObject(VibesLogger.Level.INFO,
                        "No API URL provided, setting to use the default!");
                getCurrentLogger().log(logObject);
                this.apiUrl = DEFAULT_API_URL;
            }
            return this;
        }

        public Builder setTrackingUrl(String trackingUrl) {
            if (trackingUrl != null) {
                this.trackingUrl = trackingUrl;
            } else {
                LogObject logObject = new LogObject(VibesLogger.Level.INFO,
                        "No Tracking URL provided, setting to use the default!");
                getCurrentLogger().log(logObject);
                this.trackingUrl = DEFAULT_TACKING_URL;
            }
            return this;
        }

        /**
         * Enable developer logging, with a particular {@link com.vibes.vibes.VibesLogger.Level}
         *
         * @param level
         * @return
         */
        public Builder enableDevLogging(VibesLogger.Level level) {
            this.consoleLogger = new ActivityDevLogger(level);
            return this;
        }

        /**
         * Disable developer logging
         *
         * @return
         */
        public Builder disableDevLogging() {
            this.consoleLogger = DEFAULT_LOGGER;
            return this;
        }

        public VibesConfig build() {
            if (appId == null || appId.isEmpty()) {
                throw new IllegalStateException("App ID must not be null or empty");
            }
            VibesConfig config = new VibesConfig();
            config.appId = this.appId;
            config.apiUrl = this.apiUrl;
            config.trackingUrl = this.trackingUrl;
            if (config.apiUrl.contains("vibescmeurope.com") && !config.trackingUrl.contains("eu.vibes.com")) {
                config.trackingUrl = EUROPE_TACKING_URL;
            }
            config.advertisingId = this.advertisingId;
            config.logger = new CombinedLogger(consoleLogger, customLogger);
            LogObject statement = new LogObject(VibesLogger.Level.INFO, VibesLogger.VERSION_INFO_PREFIX + VibesBuild.SDK_VERSION);
            config.logger.log(statement);
            return config;
        }
    }
}
