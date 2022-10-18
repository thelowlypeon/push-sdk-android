package com.vibes.vibes;

import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResourceClientTest extends TestConfig {
    private static final String TAG = ResourceClient.class.getSimpleName();
    String error = null;
    Credential value;
    private MockWebServer server;
    private ResourceClient client;
    private final CountDownLatch lock = new CountDownLatch(1);

    @Before
    public void setUp() throws Exception {
        this.server = new MockWebServer();
        this.server.useHttps(sslSocketFactory(), false);
        this.server.start();
        HttpUrl baseUrl = server.url("/mobile_apps_v2");
        this.client = new ResourceClient(baseUrl.toString(), new MonitorLogger(VibesLogger.Level.INFO), sslSocketFactory());
        VibesConfig config = new VibesConfig.Builder().setAppId("id").build();
        Vibes.initialize(ApplicationProvider.getApplicationContext(), config);
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

    private SSLSocketFactory sslSocketFactory() {
        try {
            FileInputStream stream = new FileInputStream(System.getProperty("user.dir") + "/testkeystore.jks");
            char[] serverKeyStorePassword = "vibes2020".toCharArray();
            KeyStore serverKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            serverKeyStore.load(stream, serverKeyStorePassword);

            String kmfAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(kmfAlgorithm);
            kmf.init(serverKeyStore, serverKeyStorePassword);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(kmfAlgorithm);
            trustManagerFactory.init(serverKeyStore);

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(kmf.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            return sslContext.getSocketFactory();
        } catch (Exception exception) {
            Log.d(TAG, "sslSocketFactory: ", exception);
            return null;
        }
    }
}
