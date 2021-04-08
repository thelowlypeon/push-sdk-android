package com.vibes.vibes;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * The data model for Push Notifications received from Vibes.
 */
public class PushPayloadParser {
    /**
     * List of support sound file formats supported by android to search through.
     */
    private static final String[] SOUND_EXTENSIONS = {".3gp", ".mp4", ".m4a", ".aac", ".ts", ".flac", ".mid", ".xmf", ".mxmf", ".rtttl", ".rtx", ".ota", ".imy", ".mp3", ".mkv", ".wav", ".ogg"};
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
    private static final String kActivityType = "activity_type";
    private static final String kActivityUid = "activity_uid";
    private static final String kMessageUid = "message_uid";
    private static final String kInboxMessageUid = "inbox_message_uid";
    private static final String kVibesAutoRegisterToken = "vibes_auto_register_token";
    private static final String kMigrationItemId = "migration_item_id";

    private static HashMap<String, Integer> priorityMap;
    private static HashMap<String, Integer> importanceMap;

    static {
        priorityMap = new HashMap<>();
        priorityMap.put("normal", NotificationCompat.PRIORITY_DEFAULT);
        priorityMap.put("high", NotificationCompat.PRIORITY_HIGH);
    }

    static {
        importanceMap = new HashMap<>();
        importanceMap.put("normal", NotificationManagerCompat.IMPORTANCE_DEFAULT);
        importanceMap.put("high", NotificationManagerCompat.IMPORTANCE_HIGH);
    }

    private HashMap<String, String> map;
    private JSONObject clientData;
    private String soundNoExt;

    public PushPayloadParser(Map<String, String> map) {
        this.map = new HashMap<>(map);
        this.clientData = getJsonObjectWith(kClientAppData);
        stripSoundExtension();
    }

    public HashMap<String, String> getMap() {
        return map;
    }

    public HashMap<String, String> getEventsMap() {
        HashMap<String, String> eventsMap = new HashMap<>();
        eventsMap.put(kActivityType, getActivityType());
        eventsMap.put(kActivityUid, getActivityUid());
        eventsMap.put(kMessageUid, getMessageUid());
        return eventsMap;
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

    public String getActivityType() {
        return getClientDataValue(kActivityType, "getActivityType()");
    }

    public String getActivityUid() {

        return getClientDataValue(kActivityUid, "getActivityUid()");
    }

    public String getInboxMessageUid() {
        return getClientDataValue(kInboxMessageUid, "getInboxMessageUid()");
    }

    public String getRichPushMediaURL() {
        return getClientDataValue(kRichPushMedia, "getRichPushMediaURL()");
    }

    public String getMessageUid() {
        return map.get(kMessageUid);
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

    public String getVibesCollapseId() {
        return map.get(kVibesCollapseId);
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

    public String getChannel() {
        return map.get(channel);
    }

    public String getSoundNoExt() {
        return soundNoExt;
    }

    private String getCustomClientDataValue(String key) {
        JSONObject customData = getCustomClientData();
        if (customData == null) {
            return null;
        }
        return customData.optString(key);
    }

    public String getVibesAutoRegisterToken() {
        return getCustomClientDataValue(kVibesAutoRegisterToken);
    }

    public String getMigrationItemId() {
        return getCustomClientDataValue(kMigrationItemId);
    }

    public boolean isMigrationPush() {
        if (!isSilentPush()) {
            return false;
        }
        String migrationToken = this.getVibesAutoRegisterToken();
        if (migrationToken == null || migrationToken.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Checks if there is a sound file specified in the payload. If it does exist, check for an extension in the filename that matches
     * android supported file format extensions, and removes the extension from the end of the specified file. The resultant sound file name can
     * be accessed via {@link #getSoundNoExt()}
     */
    private void stripSoundExtension() {
        String fullName = getSound();
        if (fullName == null || fullName.isEmpty()) {
            return;
        }
        int endsIndex = fullName.lastIndexOf('.');
        if (endsIndex < 0) {
            this.soundNoExt = fullName;
            return;
        }
        String extension = fullName.substring(endsIndex);
        this.soundNoExt = fullName;
        for (String ext : SOUND_EXTENSIONS) {
            if (extension.equalsIgnoreCase(ext)) {
                this.soundNoExt = fullName.substring(0, endsIndex);
                return;
            }
        }

    }
}
