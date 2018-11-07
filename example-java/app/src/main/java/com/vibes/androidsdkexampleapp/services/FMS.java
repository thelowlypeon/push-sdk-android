package com.vibes.androidsdkexampleapp.services;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.vibes.vibes.Vibes;



/**
 * Created by jean-michel.barbieri on 2/25/18
 * Copyright (c) Vibes 2018 . All rights reserved.
 * Last modified 12:33 AM
 */
public class FMS extends FirebaseMessagingService {

    /**
     * @see FirebaseMessagingService#onMessageReceived(RemoteMessage)
     */
    @Override
    public void onMessageReceived(RemoteMessage message) {
        Vibes.getInstance().handleNotification(getApplicationContext(), message.getData());
    }
}

