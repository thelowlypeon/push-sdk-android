package com.vibes.vibes.tracking;

import com.google.gson.annotations.SerializedName;

public class TrackingData {
    @SerializedName("company_key")
    private String companyKey;
    @SerializedName("activity_uid")
    private String activityUid;
    @SerializedName("activity_type")
    private String activityType;
    @SerializedName("person_id")
    private String personId;

    public TrackingData() {

    }

    public TrackingData(String companyKey, String activityUid, String activityType, String personId) {
        this.companyKey = companyKey;
        this.activityUid = activityUid;
        this.activityType = activityType;
        this.personId = personId;
    }

    public String getCompanyKey() {
        return companyKey;
    }

    public void setCompanyKey(String companyKey) {
        this.companyKey = companyKey;
    }

    public String getActivityUid() {
        return activityUid;
    }

    public void setActivityUid(String activityUid) {
        this.activityUid = activityUid;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    @Override
    public String toString() {
        return "TrackingData{" +
                "companyKey='" + companyKey + '\'' +
                ", activityUid='" + activityUid + '\'' +
                ", activityType='" + activityType + '\'' +
                ", personId='" + personId + '\'' +
                '}';
    }
}
