package com.vibes.androidsdkexampleapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.vibes.androidsdkexampleapp.R;
import com.vibes.vibes.PushPayloadParser;
import com.vibes.vibes.Vibes;

import org.json.JSONException;

import java.util.Map;

/**
 * Created by jean-michel.barbieri on 2/25/18
 * Copyright (c) Vibes 2018 . All rights reserved.
 * Last modified 12:33 AM
 */
public class DeepLinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_link);

        HashMap<String, String> pushMap  = (Map<String, String>) getIntent().getSerializableExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA);
        PushPayloadParser payload = new PushPayloadParser(pushMap);
        //this is for tracking which push messages have been opened by the user
        Vibes.getInstance().onPushMessageOpened(payload, this.getApplicationContext());
        try {
            String orderId = payload.getCustomClientData().getString("orderId");
            //fetch the order with the above orderId and then render the view.
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
