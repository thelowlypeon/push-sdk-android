package com.vibes.vibes;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class AuthTokenBehaviorTest {

    /**
     * This is unfortunate and maddening: this test cannot be run because you _cannot_ read the
     * "Authorization" request header (it's a "security feature").
     *
     * @see https://stackoverflow.com/a/2865535
     *
     */
    @Test
    @Ignore("Ignored test")
    public void addsAuthorizationHeaderToConnection() throws Exception {
        URL url = new URL("http://example.com");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        String token = "an-auth-token";
        AuthTokenBehavior behavior = new AuthTokenBehavior(token);
        behavior.modifyConnection(connection);

        assertThat(connection.getRequestProperty("Authorization"), is("MobileAppToken " + token));
    }
}
