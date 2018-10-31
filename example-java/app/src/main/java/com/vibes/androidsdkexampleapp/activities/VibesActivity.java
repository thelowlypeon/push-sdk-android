package com.vibes.androidsdkexampleapp.activities;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vibes.androidsdkexampleapp.R;
import com.vibes.androidsdkexampleapp.api.VibesAPI;
import com.vibes.androidsdkexampleapp.model.SharedPrefsManager;
import com.vibes.androidsdkexampleapp.modelViews.VibesViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import android.arch.lifecycle.ViewModelProviders;

/**
 * Created by jean-michel.barbieri on 2/25/18
 * Copyright (c) Vibes 2018 . All rights reserved.
 * Last modified 12:33 AM
 */
public class VibesActivity extends AppCompatActivity {
    @BindView(R.id.deviceIdView) TextView deviceIdView;
    @BindView(R.id.authTokenView) TextView authTokenView;
    @BindView(R.id.registeredLabelView) TextView registeredLabelView;
    @BindView(R.id.deviceRegBtn) Button deviceRegBtn;
    @BindView(R.id.pushRegBtn) Button pushRegBtn;
    @BindView(R.id.loadingBar) ProgressBar loadingBar;
    private VibesViewModel vibesVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        ButterKnife.bind(this);
        // ViewModel
        vibesVM = ViewModelProviders.of(this).get(VibesViewModel.class);
        vibesVM.setAPI(new VibesAPI(this));
        vibesVM.setSharedPrefs(SharedPrefsManager.getInstance(getApplicationContext()));
        setupSubscribers();
    }

    private void setupSubscribers() {
        final Observer<Boolean> observerDisplayLoadingBar = isVisible ->
            loadingBar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        final Observer<String> observerDeviceIdName = value -> deviceIdView.setText(value);
        final Observer<String> observerAuthToken = token -> authTokenView.setText(token);
        final Observer<Integer> observerDeviceRegName = value -> deviceRegBtn.setText(value);
        final Observer<Boolean> observerPusRegEnabled = value -> pushRegBtn.setEnabled(value);
        final Observer<Integer> observerPushRegColor = color ->
                pushRegBtn.setBackgroundColor(ContextCompat.getColor(this, color));
        final Observer<Integer> observerPushRegText = text -> pushRegBtn.setText(text);
        final Observer<Integer> observerPushRegLabelColor = color ->
                registeredLabelView.setBackgroundColor(ContextCompat.getColor(this, color));
        final Observer<Integer> observerRegisterLabel = text -> registeredLabelView.setText(text);

        vibesVM.getDisplayLoadingBarVisible().observe(this, observerDisplayLoadingBar);
        vibesVM.getDeviceIDLabelValue().observe(this, observerDeviceIdName);
        vibesVM.getAuthTokenLabelValue().observe(this, observerAuthToken);
        vibesVM.getDeviceRegButtonName().observe(this, observerDeviceRegName);
        vibesVM.getPushRegButtonEnabled().observe(this, observerPusRegEnabled);
        vibesVM.getPushRegButtonColor().observe(this, observerPushRegColor);
        vibesVM.getPushRegButtonName().observe(this, observerPushRegText);
        vibesVM.getPushRegLabelColor().observe(this, observerPushRegLabelColor);
        vibesVM.getPushRegLabelValue().observe(this, observerRegisterLabel);
    }

    @OnClick(R.id.deviceRegBtn)
    public void RegDeviceClicked(View view) {
        vibesVM.registerDeviceButtonClicked();
    }

    @OnClick(R.id.pushRegBtn)
    public void RegPushClicked(View view) {
        vibesVM.registerPushButtonClicked();
    }
}
