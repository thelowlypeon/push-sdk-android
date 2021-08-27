package com.vibes.vibes;

import android.app.Activity;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

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

    Map<String, String> extras;

    StubIntent(Map<String, String> extras) {
        this.extras = extras;
    }
    @Override
    public String getStringExtra(String name) {
        return this.extras.get(name);
    }

    @Override
    public HashMap<String, String> getSerializableExtra(String key) {
        if (extras != null && extras.containsKey(key)) {
            return (HashMap<String, String>) extras;
        }
        return null;
    }
}