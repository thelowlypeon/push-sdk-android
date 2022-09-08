package com.vibes.vibes;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class VibesAppTest extends TestConfig {
    private VibesAppInfo app;

    @Before
    public void setUp() throws Exception {
        this.app = new VibesAppInfo("9sd8-kpd0fds", false);
    }

    @Test
    public void getAppId() throws Exception {
        assertThat(app.getAppId(), is("9sd8-kpd0fds"));
    }

    @Test
    public void getInboxEnabledStatus() throws Exception {
        assertThat(app.isInboxEnabled(), is(false));
    }

    @Test
    public void initializeWithJsonObject() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("app_id", "test-app-id");
        jsonObject.put("inbox_enabled", true);
        VibesAppInfo vibesApp = new VibesAppInfo(jsonObject);

        assertThat(vibesApp.getAppId(), is("test-app-id"));
        assertThat(vibesApp.isInboxEnabled(), is(true));
    }
}
