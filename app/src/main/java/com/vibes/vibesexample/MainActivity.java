package com.vibes.vibesexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vibes.vibes.Credential;
import com.vibes.vibes.Vibes;
import com.vibes.vibes.VibesListener;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Hello from thread:" + Thread.currentThread().getId());
        Vibes.initialize(this, "TEST_APP_KEY");
        Vibes.getInstance().registerDevice(new VibesListener<Credential>() {
            @Override
            public void onSuccess(Credential value) {
                Log.d(TAG, "register success: " + value.toString() + " on thread: " + Thread.currentThread().getId());
            }

            @Override
            public void onFailure(String errorText) {
                Log.d(TAG, "register failure: " + errorText + " on thread: " + Thread.currentThread().getId());
            }
        });
    }
}