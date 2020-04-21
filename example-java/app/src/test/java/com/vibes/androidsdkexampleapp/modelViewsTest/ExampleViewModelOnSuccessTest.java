package com.vibes.androidsdkexampleapp.modelViewsTest;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.vibes.androidsdkexampleapp.R;
import com.vibes.androidsdkexampleapp.api.VibesAPIContract;
import com.vibes.androidsdkexampleapp.model.SharedPrefsManager;
import com.vibes.androidsdkexampleapp.modelViews.VibesViewModel;
import com.vibes.vibes.Credential;
import com.vibes.vibes.VibesListener;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by jean-michel.barbieri on 2/25/18
 * Copyright (c) Vibes 2018 . All rights reserved.
 * Last modified 12:33 AM
 */
@RunWith(JUnit4.class)
public class ExampleViewModelOnSuccessTest {

    private class DummyController implements VibesAPIContract {

        @Override
        public void registerDevice(VibesListener<Credential> listener) {
            Credential credential = new Credential("device_id", "auth_token");
            listener.onSuccess(credential);
        }

        @Override
        public void unregisterDevice(VibesListener<Void> listener) {
            listener.onSuccess(null);
        }

        @Override
        public void registerPush(VibesListener<Void> listener, String firebasePushToken) {
            listener.onSuccess(null);
        }

        @Override
        public void unregisterPush(VibesListener<Void> listener) {
            listener.onSuccess(null);
        }
    }

    private VibesViewModel viewModel;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        viewModel = new VibesViewModel();
        viewModel.setAPI(new DummyController());
    }

    @Test
    public void loadingBarGoesFromVisibleToGone() {
        List<Boolean> resultBooleans = new ArrayList<>();
        Observer<Boolean> observer = resultBooleans::add;
        viewModel.getDisplayLoadingBarVisible().observeForever(observer);
        viewModel.registerDeviceButtonClicked();
        assertEquals(Arrays.asList(true, false), resultBooleans);
    }

    @Test
    public void onRegDeviceWeGetCorrectDeviceId() {
        final String[] result = new String[1];
        Observer<String> observer = value -> result[0] = value;
        viewModel.getDeviceIDLabelValue().observeForever(observer);
        viewModel.registerDeviceButtonClicked();
        assertEquals("device_id", result[0]);
    }

    @Test
    public void onRegDeviceWeGetCorrectToken() {
        final String[] result = new String[1];
        Observer<String> observer = value -> result[0] = value;
        viewModel.getAuthTokenLabelValue().observeForever(observer);
        viewModel.registerDeviceButtonClicked();
        assertEquals("auth_token", result[0]);
    }

    @Test
    public void onRegDeviceWeSetRegBtnTextToUnregisterDevice() {
        final Integer[] result = new Integer[1];
        Observer<Integer> observer = value -> result[0] = value;
        viewModel.getDeviceRegButtonName().observeForever(observer);
        viewModel.registerDeviceButtonClicked();
        assertNotNull(result[0]);
        Integer expected = R.string.btn_unregister_device;
        Assert.assertEquals(expected, result[0]);
    }

    @Test
    public void onRegDeviceWeEnablePushRegBtn() {
        final Boolean[] result = new Boolean[1];
        Observer<Boolean> observer = aBoolean -> result[0] = aBoolean;
        viewModel.getPushRegButtonEnabled().observeForever(observer);
        viewModel.registerDeviceButtonClicked();
        assertTrue(result[0]);
    }

    @Test
    public void onRegDeviceWeWeSetVibesBtnColorToVibesButtonCollor() {
        final Integer[] result = new Integer[1];
        Observer<Integer> observer = value -> result[0] = value;
        viewModel.getPushRegButtonColor().observeForever(observer);
        viewModel.registerDeviceButtonClicked();
        assertNotNull(result[0]);
        Integer expected = R.color.vibesButtonColor;
        Assert.assertEquals(expected, result[0]);
    }

    @Test
    public void onUnregDeviceWeSetDevIdToNotRegistered() {
        final String[] result = new String[1];
        Observer<String> observer = value -> result[0] = value;
        viewModel.getDeviceIDLabelValue().observeForever(observer);
        viewModel.token = "[token]";
        viewModel.registerDeviceButtonClicked();
        Assert.assertEquals("[Not Registered]", result[0]);
    }

    @Test
    public void onUnregDeviceWeSetAuthTokenToNotRegistered() {
        final String[] result = new String[1];
        Observer<String> observer = value -> result[0] = value;
        viewModel.getAuthTokenLabelValue().observeForever(observer);
        viewModel.token = "[token]";
        viewModel.registerDeviceButtonClicked();
        Assert.assertEquals("[Not Registered]", result[0]);
    }

    @Test
    public void onUnregDeviceWeSetRegBtnTextToRegisterDevice() {
        final Integer[] result = new Integer[1];
        Observer<Integer> observer = value -> result[0] = value;
        viewModel.getDeviceRegButtonName().observeForever(observer);
        viewModel.token = "[token]";
        viewModel.registerDeviceButtonClicked();
        assertNotNull(result[0]);
        Integer expected = R.string.btn_register_device;
        Assert.assertEquals(expected, result[0]);
    }

    @Test
    public void onUnregDeviceWeDisablePushRegBtn() {
        final Boolean[] result = new Boolean[1];
        Observer<Boolean> observer = aBoolean -> result[0] = aBoolean;
        viewModel.getPushRegButtonEnabled().observeForever(observer);
        viewModel.token = "[token]";
        viewModel.registerDeviceButtonClicked();
        assertFalse(result[0]);
    }

    @Test
    public void onUnregDeviceWeWeSetVibesBtnColorToDisabledVibesButtonCollor() {
        final Integer[] result = new Integer[1];
        Observer<Integer> observer = value -> result[0] = value;
        viewModel.getPushRegButtonColor().observeForever(observer);
        viewModel.token = "[token]";
        viewModel.registerDeviceButtonClicked();
        assertNotNull(result[0]);
        Integer expected = R.color.vibesDisabledButtonColor;
        Assert.assertEquals(expected, result[0]);
    }

    @Test
    public void onUnregDeviceWeWeSetPushRegBtnTextToRegisterPush() {
        final Integer[] result = new Integer[1];
        Observer<Integer> observer = value -> result[0] = value;
        viewModel.getPushRegButtonName().observeForever(observer);
        viewModel.token = "[token]";
        viewModel.registerDeviceButtonClicked();
        assertNotNull(result[0]);
        Integer expected = R.string.btn_register_push;
        Assert.assertEquals(expected, result[0]);
    }

    @Test
    public void onUnregDeviceWeWeSetPushRegLabelColorToRed() {
        final Integer[] result = new Integer[1];
        Observer<Integer> observer = value -> result[0] = value;
        viewModel.getPushRegLabelColor().observeForever(observer);
        viewModel.token = "[token]";
        viewModel.registerDeviceButtonClicked();
        assertNotNull(result[0]);
        Integer expected = R.color.red;
        Assert.assertEquals(expected, result[0]);
    }

    @Test
    public void onUnregDeviceWeWeSetPushRegLabelTextToNotRegistered() {
        final Integer[] result = new Integer[1];
        Observer<Integer> observer = value -> result[0] = value;
        viewModel.getPushRegLabelValue().observeForever(observer);
        viewModel.token = "[token]";
        viewModel.registerDeviceButtonClicked();
        assertNotNull(result[0]);
        Integer expected = R.string.not_registered;
        Assert.assertEquals(expected, result[0]);
    }

    @Test
    public void onPushRegPushRegBtnSetTextToUnregisterPush() {
        final Integer[] result = new Integer[1];
        Observer<Integer> observer = value -> result[0] = value;
        viewModel.getPushRegButtonName().observeForever(observer);
        SharedPrefsManager manager = mock(SharedPrefsManager.class);
        viewModel.setSharedPrefs(manager);
        when(manager.getToken()).thenReturn("[TOKEN]");
        viewModel.registerPushButtonClicked();
        assertNotNull(result[0]);
        Integer expected = R.string.btn_unregister_push;
        Assert.assertEquals(expected, result[0]);
    }

    @Test
    public void onPushRegPushRegBtnSetPushLabelColorToGreen() {
        final Integer[] result = new Integer[1];
        Observer<Integer> observer = value -> result[0] = value;
        viewModel.getPushRegLabelColor().observeForever(observer);
        SharedPrefsManager manager = mock(SharedPrefsManager.class);
        viewModel.setSharedPrefs(manager);
        when(manager.getToken()).thenReturn("[TOKEN]");
        viewModel.registerPushButtonClicked();
        assertNotNull(result[0]);
        Integer expected = R.color.green;
        Assert.assertEquals(expected, result[0]);
    }

    @Test
    public void onPushRegPushRegBtnSetPushLabelTestToRegistered() {
        final Integer[] result = new Integer[1];
        Observer<Integer> observer = value -> result[0] = value;
        viewModel.getPushRegLabelValue().observeForever(observer);
        SharedPrefsManager manager = mock(SharedPrefsManager.class);
        viewModel.setSharedPrefs(manager);
        when(manager.getToken()).thenReturn("[TOKEN]");
        viewModel.registerPushButtonClicked();
        assertNotNull(result[0]);
        Integer expected = R.string.registered;
        Assert.assertEquals(expected, result[0]);
    }

    @Test
    public void onPushUnregPushRegBtnSetTextToRegisterPush() {
        final Integer[] result = new Integer[1];
        Observer<Integer> observer = value -> result[0] = value;
        viewModel.getPushRegButtonName().observeForever(observer);
        SharedPrefsManager manager = mock(SharedPrefsManager.class);
        viewModel.setSharedPrefs(manager);
        viewModel.isRegistered = true;
        when(manager.getToken()).thenReturn("[TOKEN]");
        viewModel.registerPushButtonClicked();
        assertNotNull(result[0]);
        Integer expected = R.string.btn_register_push;
        Assert.assertEquals(expected, result[0]);
    }

    @Test
    public void onPushUnregPushRegBtnSetPushLabelColorToRed() {
        final Integer[] result = new Integer[1];
        Observer<Integer> observer = value -> result[0] = value;
        viewModel.getPushRegLabelColor().observeForever(observer);
        SharedPrefsManager manager = mock(SharedPrefsManager.class);
        viewModel.setSharedPrefs(manager);
        viewModel.isRegistered = true;
        when(manager.getToken()).thenReturn("[TOKEN]");
        viewModel.registerPushButtonClicked();
        assertNotNull(result[0]);
        Integer expected = R.color.red;
        Assert.assertEquals(expected, result[0]);
    }

    @Test
    public void onPushUnregPushRegBtnSetPushLabelTestToUnregistered() {
        final Integer[] result = new Integer[1];
        Observer<Integer> observer = value -> result[0] = value;
        viewModel.getPushRegLabelValue().observeForever(observer);
        SharedPrefsManager manager = mock(SharedPrefsManager.class);
        viewModel.setSharedPrefs(manager);
        viewModel.isRegistered = true;
        when(manager.getToken()).thenReturn("[TOKEN]");
        viewModel.registerPushButtonClicked();
        assertNotNull(result[0]);
        Integer expected = R.string.not_registered;
        Assert.assertEquals(expected, result[0]);
    }
}
