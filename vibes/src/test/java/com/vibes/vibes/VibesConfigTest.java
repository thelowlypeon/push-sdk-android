package com.vibes.vibes;

import org.junit.Before;
import org.junit.Test;

import static com.vibes.vibes.VibesConfig.DEFAULT_API_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class VibesConfigTest {
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
        assertEquals(config.getLogger().getClass(), InactiveLogger.class);
        assertNull(config.getAdvertisingId());
    }

}