package com.vibes.androidsdkexampleapp.activities;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.vibes.androidsdkexampleapp.R;
import com.vibes.androidsdkexampleapp.fragments.InboxMessagesFragment;
import com.vibes.androidsdkexampleapp.fragments.VibesMainFragment;

/**
 * Created by jean-michel.barbieri on 2/25/18
 * Copyright (c) Vibes 2018 . All rights reserved.
 * Last modified 12:33 AM
 */
public class MainActivity extends AppCompatActivity {
    public static final String INBOX_MESSAGE_KEY = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        loadFragment(new VibesMainFragment());
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.action_home:
                    item.setEnabled(true);
                    fragment = new VibesMainFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.action_inbox_messages:
                    item.setEnabled(true);
                    fragment = new InboxMessagesFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(backStateName);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
