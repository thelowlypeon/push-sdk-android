package com.vibes.vibes;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marius.pop on 11/14/17.
 */

public class PushPayloadParser {
    private static final String TAG = "PushPayloadParser";
    private static final String kClientAppData = "client_app_data";
    private static final String kDeepLink = "deep_link";
    private static final String kBody = "body";
    private static final String kTitle = "title";

    private HashMap<String, String> map;

    public PushPayloadParser(Map<String, String> map) {
        this.map = new HashMap<>(map);
    }

    public HashMap<String,String> getMap() {
        return map;
    }

    public String getDeepLink() {
        if (map.containsKey(kClientAppData)) {
            try {
                JSONObject object = new JSONObject(map.get(kClientAppData));
                return object.getString(kDeepLink);
            } catch (JSONException e) {
                Log.d(TAG, "--> Vibes --> Payload cannot be parsed.");
            }
        }
        return "";
    }

    public String getBody() {
        return map.get(kBody);
    }

    public String getTitle() {
        return map.get(kTitle);
    }
}
