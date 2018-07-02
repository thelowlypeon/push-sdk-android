package com.vibes.vibes;

import android.app.Activity;
import android.content.Intent;

import java.util.HashMap;

class StubActivity extends Activity {

    Intent intent;

    StubActivity(Intent intent) {
        this.intent = intent;
    }

    @Override
    public Intent getIntent() {
        return this.intent;
    }
}

class StubIntent extends Intent {

    HashMap<String, String> extras;

    StubIntent(HashMap<String, String> extras) {
        this.extras = extras;
    }
    @Override
    public String getStringExtra(String name) {
        return this.extras.get(name);
    }
}