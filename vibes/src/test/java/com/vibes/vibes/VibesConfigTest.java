package com.vibes.vibes;

import androidx.test.platform.app.InstrumentationRegistry;

import com.vibes.vibes.logging.CombinedLogger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.vibes.vibes.VibesConfig.DEFAULT_API_URL;
import static com.vibes.vibes.VibesConfig.DEFAULT_TACKING_URL;
import static com.vibes.vibes.VibesConfig.EUROPE_API_URL;
import static com.vibes.vibes.VibesConfig.EUROPE_TACKING_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class VibesConfigTest extends TestConfig {
    @Before
    public void setUp() throws Exception {
    }

    @Test(expected = IllegalStateException.class)
    public void testBuilderNoRequiredParams() {
        VibesConfig.Builder builder = new VibesConfig.Builder();

        builder.build();
    }

    @Test
    public void testBuilderHasRequiredParams() {
        VibesConfig.Builder builder = new VibesConfig.Builder();
        builder.setAppId("App ID");
        VibesConfig config = builder.build();

        assertEquals("App ID", config.getAppId());
        assertEquals(DEFAULT_API_URL, config.getApiUrl());
        assertEquals(DEFAULT_TACKING_URL, config.getTrackingUrl());
        assertEquals(config.getLogger().getClass(), CombinedLogger.class);
        CombinedLogger logger = (CombinedLogger) config.getLogger();
        assertEquals(logger.getCustomLogger().getClass(), InactiveLogger.class);
        assertNull(config.getAdvertisingId());

        VibesConfig.Builder europeBuilder = new VibesConfig.Builder();
        europeBuilder.setAppId("App ID");
        europeBuilder.setApiUrl(EUROPE_API_URL);
        VibesConfig europeConfig = europeBuilder.build();
        assertEquals(EUROPE_API_URL, europeConfig.getApiUrl());
        assertEquals(EUROPE_TACKING_URL, europeConfig.getTrackingUrl());
    }


    @Test
    public void testNoAppIdInitNoException() {
        Vibes.initialize(InstrumentationRegistry.getInstrumentation().getContext());
    }
}