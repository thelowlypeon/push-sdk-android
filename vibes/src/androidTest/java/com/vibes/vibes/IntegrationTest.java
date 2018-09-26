package com.vibes.vibes;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class IntegrationTest {
    final CountDownLatch lock = new CountDownLatch(1);
    @Rule
    public UiThreadTestRule uiThreadTestRule = new UiThreadTestRule();
    Credential actualCredential;
    boolean called = false;

    @Before
    public void setup() throws Exception, Throwable {
        final Context appContext = InstrumentationRegistry.getContext();
        final VibesConfig config = new VibesConfig.Builder().setAppId("TEST_APP_KEY").build();
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Vibes.initialize(appContext, config);
            }
        });
    }

    @Test
    public void registerDevice() throws Exception {
        Vibes.getInstance().registerDevice(new VibesListener<Credential>() {
            @Override
            public void onSuccess(Credential value) {
                actualCredential = value;
                lock.countDown();
            }

            @Override
            public void onFailure(String errorText) {
                fail("Expected to receive a credential, but got error: " + errorText);
            }
        });
        lock.await(2, TimeUnit.SECONDS);

        assertThat(actualCredential.getDeviceID(), startsWith("MOCK_"));
        assertThat(actualCredential.getAuthToken(), startsWith("mock_auth_token"));
    }

    @Test
    public void unregisterDevice() throws Exception {
        performInitialDeviceRegistration(Vibes.getInstance());
        Vibes.getInstance().unregisterDevice(new VibesListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                called = true;
                lock.countDown();
            }

            @Override
            public void onFailure(String errorText) {
                fail("Expected to receive success, but got error: " + errorText);
            }
        });
        lock.await(2, TimeUnit.SECONDS);

        assertThat(called, is(true));
    }

    @Test
    public void updateDeviceLatLong() throws Exception {
        performInitialDeviceRegistration(Vibes.getInstance());

        Vibes.getInstance().updateDeviceLatLong(0.0, 0.0, new VibesListener<Credential>() {
            @Override
            public void onSuccess(Credential value) {
                actualCredential = value;
                lock.countDown();
            }

            @Override
            public void onFailure(String errorText) {
                fail("Expected to receive a credential, but got error: " + errorText);
            }
        });
        lock.await(2, TimeUnit.SECONDS);

        assertThat(actualCredential, is(not(nullValue())));
    }

    @Test
    public void registerPush() throws Exception {
        performInitialDeviceRegistration(Vibes.getInstance());

        Vibes.getInstance().registerPush("a-token", new VibesListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                called = true;
            }

            @Override
            public void onFailure(String errorText) {
                fail("failed to register push! " + errorText);
            }
        });
        lock.await(2, TimeUnit.SECONDS);

        assertThat(called, is(true));
    }

    @Test
    public void unregisterPush() throws Exception {
        performInitialDeviceRegistration(Vibes.getInstance());

        Vibes.getInstance().unregisterPush(new VibesListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                called = true;
            }

            @Override
            public void onFailure(String errorText) {
                fail("failed to unregister push! " + errorText);
            }
        });
        lock.await(2, TimeUnit.SECONDS);

        assertThat(called, is(true));
    }

    private void performInitialDeviceRegistration(Vibes vibes) throws Exception {
        final CountDownLatch registrationLock = new CountDownLatch(1);
        vibes.registerDevice(new VibesListener<Credential>() {
            @Override
            public void onSuccess(Credential value) {
                registrationLock.countDown();
            }

            @Override
            public void onFailure(String errorText) {
                fail("failed to register device! " + errorText);
            }
        });
        registrationLock.await(2, TimeUnit.SECONDS);
    }
}
