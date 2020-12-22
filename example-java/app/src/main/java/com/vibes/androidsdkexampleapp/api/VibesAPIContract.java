package com.vibes.androidsdkexampleapp.api;

import com.vibes.vibes.Credential;
import com.vibes.vibes.VibesListener;

/**
 * Created by jean-michel.barbieri on 2/25/18
 * Copyright (c) Vibes 2018 . All rights reserved.
 * Last modified 12:33 AM
 */
public interface VibesAPIContract {
    void registerDevice(VibesListener<Credential> listener);
    void unregisterDevice(VibesListener<Void> listener);
    void registerPush(VibesListener<Void> listener, String firebasePushToken);
    void unregisterPush(VibesListener<Void> listener);
}
