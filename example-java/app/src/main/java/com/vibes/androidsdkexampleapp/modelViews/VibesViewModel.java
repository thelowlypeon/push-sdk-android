package com.vibes.androidsdkexampleapp.modelViews;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.vibes.androidsdkexampleapp.R;
import com.vibes.androidsdkexampleapp.api.VibesAPIContract;
import com.vibes.androidsdkexampleapp.model.SharedPrefsManager;
import com.vibes.vibes.Credential;
import com.vibes.vibes.VibesListener;

/**
 * Created by jean-michel.barbieri on 2/25/18
 * Copyright (c) Vibes 2018 . All rights reserved.
 * Last modified 12:33 AM
 */
public class VibesViewModel extends ViewModel {
    public String token = "";
    private VibesAPIContract api;
    private MutableLiveData<String> deviceID;
    private MutableLiveData<String> authToken;
    private MutableLiveData<Boolean> displayLoadingBar;
    private MutableLiveData<Integer> deviceRegName;
    private MutableLiveData<Boolean> pushRegEnabled;
    private MutableLiveData<Integer> pushRegnColor;
    private MutableLiveData<Integer> pushRegName;
    private MutableLiveData<Integer> pushRegLabelColor;
    private MutableLiveData<Integer> pushRegLabel;
    public boolean isRegistered = false;
    private SharedPrefsManager sharedPrefs;

    /**
     * Method called when the user clicks on the button 'REGISTER/UNREGISTER DEVICE'
     */
    public void registerDeviceButtonClicked() {
        getDisplayLoadingBarVisible().setValue(true);
        if (token.length() == 0) {
            api.registerDevice(registerDeviceCallback());
        } else {
            api.unregisterDevice(unregisterDeviceCallback());
        }
    }

    /**
     * Method called when the user clicks on the button 'REGISTER/UNREGISTER PUSH'
     */
    public void registerPushButtonClicked() {
        getDisplayLoadingBarVisible().setValue(true);
        if (isRegistered) {
            api.unregisterPush(unregisterPushCallback());
        } else {
            Log.d("My Token ---->", ""+sharedPrefs.getToken());
            api.registerPush(registerPushCallback(), sharedPrefs.getToken());
        }
    }

    /**
     * Callback to register a device with Vibes. This gets passed to the controller to be called
     * upon success or failure. The SDK stores the credentials locally so if multiple calls to
     * register device get triggered, the credentials are grabbed from the local storage. Upon
     * success the device id and auth token are sent back as part of the credential object.
     */
    private VibesListener<Credential> registerDeviceCallback() {
        return new VibesListener<Credential>() {
            @Override
            public void onSuccess(Credential credential) {
                token = "[token]";
                getDeviceIDLabelValue().setValue(credential.getDeviceID());
                getAuthTokenLabelValue().setValue(credential.getAuthToken());
                Log.d("Token --->>", credential.getAuthToken());
                getDeviceRegButtonName().setValue(R.string.btn_unregister_device);
                getPushRegButtonEnabled().setValue(true);
                getPushRegButtonColor().setValue(R.color.vibesButtonColor);
                getDisplayLoadingBarVisible().setValue(false);
            }

            @Override
            public void onFailure(String error) {
                getDisplayLoadingBarVisible().setValue(false);
            }
        };
    }

    /**
     * Callback to unregister a device with Vibes. The callback gets passed to the controller to be
     * called upon success or failure. If the device was not unregistered from push notifications
     * prior to the unregister device request, the device will also be unregistered from push
     * notifications. When the unregister device is successful, the local credentials stored during the
     * device registration are deleted.
     */
    private VibesListener<Void> unregisterDeviceCallback() {
        return new VibesListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                token = "";
                isRegistered = false;
                getDeviceIDLabelValue().setValue("[Not Registered]");
                getAuthTokenLabelValue().setValue("[Not Registered]");
                getDeviceRegButtonName().setValue(R.string.btn_register_device);
                getPushRegButtonEnabled().setValue(false);
                getPushRegButtonColor().setValue(R.color.vibesDisabledButtonColor);
                getPushRegButtonName().setValue(R.string.btn_register_push);
                getPushRegLabelColor().setValue(R.color.red);
                getPushRegLabelValue().setValue(R.string.not_registered);
                getDisplayLoadingBarVisible().setValue(false);
            }

            @Override
            public void onFailure(String error) {
                getDisplayLoadingBarVisible().setValue(false);
            }
        };
    }

    /**
     * Callback to register push notifications with Vibes. The callback gets passed to the
     * controller to be called upon success or failure. Upon success, the device is ready to receive
     * push notifications.
     */
    private VibesListener<Void> registerPushCallback() {
        return new VibesListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                isRegistered = true;
                Log.d("Register Push", "Register Push successful");
                getPushRegButtonName().setValue(R.string.btn_unregister_push);
                getPushRegLabelColor().setValue(R.color.green);
                getPushRegLabelValue().setValue(R.string.registered);
                getDisplayLoadingBarVisible().setValue(false);
            }

            @Override
            public void onFailure(String errorText) {
                getDisplayLoadingBarVisible().setValue(false);
            }
        };
    }

    /**
     * Callback to unregister push notifications with Vibes. The callback gets passed to the
     * controller to be called upon success or failure. Upon success, the device will no longer
     * receive push notifications.
     */
    private VibesListener<Void> unregisterPushCallback() {
        return new VibesListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                isRegistered = false;
                getPushRegButtonName().setValue(R.string.btn_register_push);
                getPushRegLabelColor().setValue(R.color.red);
                getPushRegLabelValue().setValue(R.string.not_registered);
                getDisplayLoadingBarVisible().setValue(false);
            }

            @Override
            public void onFailure(String errorText) {
                getDisplayLoadingBarVisible().setValue(false);
            }
        };
    }

    /**
     * Getters/Setters
     */
    public void setAPI(VibesAPIContract api) {
        this.api = api;
    }

    public void setSharedPrefs(SharedPrefsManager sharedPrefs) {
        this.sharedPrefs = sharedPrefs;
    }

    /**
     * LiveData
     */
    public MutableLiveData<String> getDeviceIDLabelValue() {
        if (deviceID == null) {
            deviceID = new MutableLiveData<>();
        }
        return deviceID;
    }

    public MutableLiveData<String> getAuthTokenLabelValue() {
        if (authToken == null) {
            authToken = new MutableLiveData<>();
        }
        return authToken;
    }

    public MutableLiveData<Boolean> getDisplayLoadingBarVisible() {
        if (displayLoadingBar == null) {
            displayLoadingBar = new MutableLiveData<>();
        }
        return displayLoadingBar;
    }

    public MutableLiveData<Integer> getDeviceRegButtonName() {
        if (deviceRegName == null) {
            deviceRegName = new MutableLiveData<>();
        }
        return deviceRegName;
    }

    public MutableLiveData<Boolean> getPushRegButtonEnabled() {
        if (pushRegEnabled == null) {
            pushRegEnabled = new MutableLiveData<>();
        }
        return pushRegEnabled;
    }

    public MutableLiveData<Integer> getPushRegButtonColor() {
        if (pushRegnColor == null) {
            pushRegnColor = new MutableLiveData<>();
        }
        return pushRegnColor;
    }

    public MutableLiveData<Integer> getPushRegButtonName() {
        if (pushRegName == null) {
            pushRegName = new MutableLiveData<>();
        }
        return pushRegName;
    }

    public MutableLiveData<Integer> getPushRegLabelColor() {
        if (pushRegLabelColor == null) {
            pushRegLabelColor = new MutableLiveData<>();
        }
        return pushRegLabelColor;
    }

    public MutableLiveData<Integer> getPushRegLabelValue() {
        if (pushRegLabel == null) {
            pushRegLabel = new MutableLiveData<>();
        }
        return pushRegLabel;
    }
}