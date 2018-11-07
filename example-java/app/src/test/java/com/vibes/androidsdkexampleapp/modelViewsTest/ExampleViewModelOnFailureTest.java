package com.vibes.androidsdkexampleapp.modelViewsTest;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import android.content.Context;

import com.vibes.androidsdkexampleapp.api.VibesAPIContract;
import com.vibes.androidsdkexampleapp.model.SharedPrefsManager;
import com.vibes.androidsdkexampleapp.modelViews.VibesViewModel;
import com.vibes.vibes.Credential;
import com.vibes.vibes.VibesListener;

import org.hamcrest.core.AnyOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;


/**
 * Created by jean-michel.barbieri on 2/25/18
 * Copyright (c) Vibes 2018 . All rights reserved.
 * Last modified 12:33 AM
 */
@RunWith(JUnit4.class)
public class ExampleViewModelOnFailureTest {

    private VibesViewModel viewModel;
    private DummyControllerRegisterFails controllerRegisterFails =
            new DummyControllerRegisterFails();
    private DummyControllerUnregisterFails controllerUnregisterFails =
            new DummyControllerUnregisterFails();
    private DummyControllerRegisterPushFails controllerRegisterPushFails =
            new DummyControllerRegisterPushFails();
    private DummyControllerUnregisterPushFails controllerUnregisterPushFails =
            new DummyControllerUnregisterPushFails();

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        viewModel = new VibesViewModel();
    }

    @Test
    public void loadingBarGoesFromVisibleToGoneOnRegisterFails() {
        viewModel.setAPI(controllerRegisterFails);
        List<Boolean> resultBooleans = new ArrayList<>();
        Observer<Boolean> observer = resultBooleans::add;
        viewModel.getDisplayLoadingBarVisible().observeForever(observer);
        viewModel.registerDeviceButtonClicked();
        assertEquals(Arrays.asList(true, false), resultBooleans);
    }

    @Test
    public void loadingBarGoesFromVisibleToGoneOnUnregisterFails() {
        viewModel.setAPI(controllerUnregisterFails);
        List<Boolean> resultBooleans = new ArrayList<>();
        Observer<Boolean> observer = resultBooleans::add;
        viewModel.getDisplayLoadingBarVisible().observeForever(observer);
        viewModel.token = "token";
        viewModel.registerDeviceButtonClicked(); // I will call unregister if the token isn't empty
        assertEquals(Arrays.asList(true, false), resultBooleans);
    }

    @Test
    public void loadingBarGoesFromVisibleToGoneOnRegisterPushFails() {
        viewModel.setAPI(controllerRegisterPushFails);
        List<Boolean> resultBooleans = new ArrayList<>();
        Observer<Boolean> observer = resultBooleans::add;
        viewModel.getDisplayLoadingBarVisible().observeForever(observer);
        SharedPrefsManager manager = mock(SharedPrefsManager.class);
        viewModel.setSharedPrefs(manager);
        when(manager.getToken()).thenReturn("[TOKEN]");
        viewModel.registerPushButtonClicked();
        assertEquals(Arrays.asList(true, false), resultBooleans);
    }

    @Test
    public void loadingBarGoesFromVisibleToGoneOnUnregisterPushFails() {
        viewModel.setAPI(controllerUnregisterPushFails);
        List<Boolean> resultBooleans = new ArrayList<>();
        Observer<Boolean> observer = resultBooleans::add;
        viewModel.getDisplayLoadingBarVisible().observeForever(observer);
        SharedPrefsManager manager = mock(SharedPrefsManager.class);
        viewModel.setSharedPrefs(manager);
        when(manager.getToken()).thenReturn("[TOKEN]");
        viewModel.isRegistered = true;
        viewModel.registerPushButtonClicked();
        assertEquals(Arrays.asList(true, false), resultBooleans);
    }

    private class DummyControllerRegisterFails implements VibesAPIContract {

        @Override
        public void registerDevice(VibesListener<Credential> listener) {
            listener.onFailure("Failure message");
        }

        @Override
        public void unregisterDevice(VibesListener<Void> listener) {
        }

        @Override
        public void registerPush(VibesListener<Void> listener, String firebasePushToken) {
        }

        @Override
        public void unregisterPush(VibesListener<Void> listener) {
        }
    }

    private class DummyControllerUnregisterFails implements VibesAPIContract {

        @Override
        public void registerDevice(VibesListener<Credential> listener) {
            Credential credential = new Credential("device_id", "auth_token");
            listener.onSuccess(credential);
        }

        @Override
        public void unregisterDevice(VibesListener<Void> listener) {
            listener.onFailure("Failure message");
        }

        @Override
        public void registerPush(VibesListener<Void> listener, String firebasePushToken) {
        }

        @Override
        public void unregisterPush(VibesListener<Void> listener) {
        }
    }

    private class DummyControllerRegisterPushFails implements VibesAPIContract {

        @Override
        public void registerDevice(VibesListener<Credential> listener) {
            Credential credential = new Credential("device_id", "auth_token");
            listener.onSuccess(credential);
        }

        @Override
        public void unregisterDevice(VibesListener<Void> listener) {
        }

        @Override
        public void registerPush(VibesListener<Void> listener, String firebasePushToken) {
            listener.onFailure("Failure message");
        }

        @Override
        public void unregisterPush(VibesListener<Void> listener) {
        }
    }

    private class DummyControllerUnregisterPushFails implements VibesAPIContract {

        @Override
        public void registerDevice(VibesListener<Credential> listener) {
            Credential credential = new Credential("device_id", "auth_token");
            listener.onSuccess(credential);
        }

        @Override
        public void unregisterDevice(VibesListener<Void> listener) {
        }

        @Override
        public void registerPush(VibesListener<Void> listener, String firebasePushToken) {
            listener.onSuccess(null);
        }

        @Override
        public void unregisterPush(VibesListener<Void> listener) {
            listener.onFailure("Failure message");
        }
    }
}
