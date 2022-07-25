package com.vibes.vibes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeviceTokenTest extends TestConfig {
    @Test
    public void encode() throws Exception {
        DeviceToken deviceToken = new DeviceToken("token");
        assertThat(deviceToken.encode(), containsString("push_token"));
    }
}