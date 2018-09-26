package com.vibes.vibes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.internal.tls.SslClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class ResourceClientTest {
    private static final String TAG = "ResourceClientTest";
    private MockWebServer server;
    private ResourceClient client;

    private CountDownLatch lock = new CountDownLatch(1);
    String error = null;
    Credential value;

    @Before
    public void setUp() throws Exception {
        this.server = new MockWebServer();
        this.server.useHttps(SslClient.localhost().socketFactory, false);
        this.server.start();

        HttpUrl baseUrl = server.url("/mobile_apps_v2");
        this.client = new ResourceClient(baseUrl.toString(), new MonitorLogger(VibesLogger.Level.INFO), SslClient.localhost().socketFactory);
    }

    @After
    public void tearDown() throws Exception {
        this.server.shutdown();
    }

    @Test
    public void requestWithUnsuccessfulStatusCode() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404).setBody("Not Found"));

        HTTPResource<Credential> resource = APIDefinition.registerDevice("key");
        client.request(resource, new TestResourceListener<Credential>() {
            @Override
            public void onFailure(int responseCode, String errorText) {
                error = errorText;
            }
        });

        lock.await(1000, TimeUnit.MILLISECONDS);
        assertThat(error, is("Not Found"));
    }

    @Test
    public void requestWithUnparseableResponseText() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        HTTPResource<Credential> resource = APIDefinition.registerDevice("key");
        client.request(resource, new TestResourceListener<Credential>() {
            @Override
            public void onFailure(int responseCode, String errorText) {
                error = errorText;
            }
        });

        lock.await(1000, TimeUnit.MILLISECONDS);
        assertThat(error, is("Failed: org.json.JSONException: No value for auth_token"));
    }

    @Test
    public void requestSuccess() throws Exception {
        String json = "{\"auth_token\":\"a-token\",\"device\":{\"vibes_device_id\":\"device-id\"}}";
        server.enqueue(new MockResponse().setResponseCode(200).setBody(json));

        HTTPResource<Credential> resource = APIDefinition.registerDevice("key");
        client.request(resource, new TestResourceListener<Credential>() {
            @Override
            public void onSuccess(Credential foundValue) {
                value = foundValue;
            }
        });

        lock.await(1000, TimeUnit.MILLISECONDS);
        assertThat(value, equalTo(new Credential("device-id", "a-token")));
    }
}