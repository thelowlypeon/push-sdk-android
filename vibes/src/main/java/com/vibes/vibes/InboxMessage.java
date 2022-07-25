package com.vibes.vibes;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InboxMessage implements Serializable {
    public static final String kActivityType = "activityType";
    public static final String kActivityUid = "activityUid";

    public static final String eActivityType = "activity_type";
    public static final String eActivityUid = "activity_uid";
    public static final String eMessageUid = "message_uid";

    @SerializedName("message_uid")
    private String messageUid;
    @SerializedName("subject")
    private String subject;
    @SerializedName("content")
    private String content;
    @SerializedName("detail")
    private String detail;
    @SerializedName("collapse_key")
    private String collapseKey;
    @SerializedName("read")
    private Boolean read;
    @SerializedName("expires_at")
    private Date expirationDate;
    @SerializedName("created_at")
    private Date createdAt;
    @SerializedName("images")
    private HashMap<String, String> images;
    @SerializedName("inbox_custom_data")
    private HashMap<String, Object> inboxCustomData;
    @SerializedName("apprefdata")
    private HashMap<String, Object> appRefData;

    public String getMessageUid() {
        return messageUid;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getDetail() {
        return detail;
    }

    public Boolean getRead() {
        return read;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public String getCollapseKey() {
        return collapseKey;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Map<String, String> getImages() {
        return images;
    }

    @Nullable
    public String getIconImage() {
        if (getImages() == null) {
            return null;
        }
        return getImages().get("icon");
    }

    @Nullable
    public String getMainIcon() {
        if (getImages() == null) {
            return null;
        }
        return getImages().get("main");
    }

    public Map<String, Object> getInboxCustomData() {
        return inboxCustomData;
    }

    public Map<String, Object> getAppRefData() {
        return appRefData;
    }

    public HashMap<String, String> getEventsMap() {
        HashMap<String, String> eventsMap = new HashMap<>();
        eventsMap.put(eActivityType, getApprefDataValue(kActivityType, ""));
        eventsMap.put(eActivityUid, getApprefDataValue(kActivityUid, ""));
        eventsMap.put(eMessageUid, getMessageUid());
        return eventsMap;
    }

    private String getApprefDataValue(String key, String defaultValue) {
        if (appRefData != null && appRefData.containsKey(key)) {
            return (String) appRefData.get(key);
        }
        return defaultValue;
    }

    @Override
    public String toString() {
        return "{" +
                "message_uid='" + getMessageUid() + "'," +
                "subject='" + getSubject() + "'," +
                "content='" + getContent() + "'," +
                "detail='" + getDetail() + "'," +
                "collapse_key='" + getCollapseKey() + "'," +
                "read='" + getRead() + "'," +
                "images='" + getImages() + "'," +
                "inboxCustomData='" + getInboxCustomData() + "'," +
                "apprefdata='" + getAppRefData() + "'" +
                "}";
    }
}
