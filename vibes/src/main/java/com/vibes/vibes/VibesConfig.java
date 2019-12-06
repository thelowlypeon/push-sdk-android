package com.vibes.vibes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Model to describe the initialization of {@link Vibes}
 */
public class VibesConfig {

    /**
     * The default URL to use to talk to Vibes, if not overridden by client settings.
     */
    static final String DEFAULT_API_URL = "https://public-api.vibescm.com/mobile_apps";

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

    public String getAppId() {
        return appId;
    }

    /**
     * Builder to construct the Vibes Configuration
     */
    public static class Builder {
        private String advertisingId;
        private VibesLogger logger = new InactiveLogger();
        private String appId;
        private String apiUrl = DEFAULT_API_URL;

        public Builder setAdvertisingId(String advertisingId) {
            this.advertisingId = advertisingId;
            return this;
        }

        public Builder setLogger(VibesLogger logger) {
            if (logger != null) {
                this.logger = logger;
            } else {
                this.logger = DEFAULT_LOGGER;
            }
            return this;
        }

        public Builder setAppId(@NonNull String appId) {
            this.appId = appId;
            return this;
        }

        public Builder setApiUrl(String apiUrl) {
            if (apiUrl != null) {
                this.apiUrl = apiUrl;
            } else {
                this.apiUrl = DEFAULT_API_URL;
            }
            return this;
        }

        public VibesConfig build() {
            if (appId == null || appId.isEmpty()) {
                throw new IllegalStateException("App ID must not be null or empty");
            }
            VibesConfig config = new VibesConfig();
            config.appId = this.appId;
            config.apiUrl = this.apiUrl;
            config.advertisingId = this.advertisingId;
            config.logger = this.logger;
            return config;
        }
    }
}
