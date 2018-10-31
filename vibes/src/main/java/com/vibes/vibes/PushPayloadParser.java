package com.vibes.vibes;

import android.app.Notification;
import android.app.NotificationManager;

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
    private static final String kNotificationChannel = "notification_channel";
    private static final String kRichPushMedia = "media_url";
    private static final String kDeepLink = "deep_link";
    private static final String kBody = "body";
    private static final String kTitle = "title";
    private static final String kSound = "sound";
    private static final String badgeNumber = "badge";
    private static final String priority = "priority";
    private static final String channel = "click_action";
    private static final String kClientCustomData = "client_custom_data";
    private static final String kSilentPush = "silent_push";
    private static final String kVibesCollapseId = "vibes_collapse_id";

    private static HashMap<String, Integer> priorityMap;
    static {
        priorityMap = new HashMap<>();
        priorityMap.put("normal", Notification.PRIORITY_DEFAULT);
        priorityMap.put("high", Notification.PRIORITY_HIGH);
    }

    private static HashMap<String, Integer> importanceMap;
    static {
        importanceMap = new HashMap<>();
        importanceMap.put("normal", NotificationManager.IMPORTANCE_DEFAULT);
        importanceMap.put("high", NotificationManager.IMPORTANCE_HIGH);
    }

    private HashMap<String, String> map;
    private JSONObject clientData;

    public String getVibesCollapseId() {
        return map.get(kVibesCollapseId);
    }

    public PushPayloadParser(Map<String, String> map) {
        this.map = new HashMap<>(map);
        this.clientData = getJsonObjectWith(kClientAppData);
    }

    public HashMap<String, String> getMap() {
        return map;
    }

    private JSONObject getJsonObjectWith(String name) {
        if (map.containsKey(name)) {
            try {
                return new JSONObject(map.get(name));
            } catch (JSONException e) {
                Vibes.getCurrentLogger().log(e);
            }
        }
        return null;
    }

    private String getClientDataValue(String key, String msg) {
        if (clientData != null) {
            try {
                return clientData.getString(key);
            } catch (JSONException e) {
                Vibes.getCurrentLogger().log(e);
            }
        }
        return null;
    }
    
    public String getDeepLink() {
        return getClientDataValue(kDeepLink, "getDeepLink()");
    }

    public String getRichPushMediaURL() {
        return getClientDataValue(kRichPushMedia, "getRichPushMediaURL()");
    }

    public String getNotificationChannel() {
        return map.get(kNotificationChannel);
    }

    public String getBody() {
        return map.get(kBody);
    }

    public String getTitle() {
        return map.get(kTitle);
    }

    public String getSound() {
        return map.get(kSound);
    }

    public JSONObject getCustomClientData() {
        return getJsonObjectWith(kClientCustomData);
    }

    public Boolean isSilentPush() {
        if (map.containsKey(kSilentPush)) {
            return Boolean.valueOf(map.get(kSilentPush));
        }
        return false;
    }

    public Integer getBadgeNumber() {
        try {
            if (map.get(badgeNumber) == null) return null;
            return Integer.parseInt(map.get(badgeNumber));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    public Integer getPriority() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return importanceMap.get(map.get(priority));
        } else {
            return priorityMap.get(map.get(priority));
        }
    }

    public String getChannel () {
        return map.get(channel);
    }
}
