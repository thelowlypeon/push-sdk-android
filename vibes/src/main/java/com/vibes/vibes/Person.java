package com.vibes.vibes;

import org.json.JSONException;
import org.json.JSONObject;

public class Person {
    private String externalPersonId;
    private String personKey;
    private String mdn;

    private static final String MDN_KEY = "mdn";
    private static final String EXTERNAL_PERSON_ID_KEY = "external_person_id";
    private static final String PERSON_KEY = "person_key";

    static class PersonObjectFactory implements JSONObjectFactory<Person> {
        @Override
        public String serialize(Person person) throws JSONException {
            JSONObject json = new JSONObject();
            json.put(EXTERNAL_PERSON_ID_KEY, person.getExternalPersonId());
            json.put(PERSON_KEY, person.getPersonKey());
            json.put(MDN_KEY, person.getMdn());
            return json.toString(2);
        }

        @Override
        public Person createInstance(String jsonString) throws JSONException {
            JSONObject json = new JSONObject(jsonString);
            String externalPersonId = json.getString(EXTERNAL_PERSON_ID_KEY);
            String personKey = json.getString(PERSON_KEY);
            String mdn = json.getString(MDN_KEY);
            return new Person(externalPersonId, personKey, mdn);
        }
    }

    public Person(String externalPersonId, String personKey, String mdn) {
        this.externalPersonId = externalPersonId;
        this.personKey = personKey;
        this.mdn = mdn;
    }

    public Person(JSONObject json) throws JSONException {
        this.externalPersonId = json.getString(EXTERNAL_PERSON_ID_KEY);
        this.personKey = json.getString(PERSON_KEY);
        try {
            JSONObject mobilePhoneObject = json.getJSONObject("mobile_phone");
            this.mdn = mobilePhoneObject.getString(MDN_KEY);
        } catch (JSONException e) {
            Vibes.getCurrentLogger().log(e);
            //not found exception
        }
    }

    public void setExternalPersonId(String externalPersonId) {
        this.externalPersonId = externalPersonId;
    }
    public String getExternalPersonId() {
        return externalPersonId;
    }

    public void setPersonKey(String personKey) {
        this.personKey = personKey;
    }
    public String getPersonKey() {
        return personKey;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }
    public String getMdn() {
        return mdn != null ? mdn : "";
    }

    @Override
    public String toString() {
        return "Person {" +
                "externalPersonId='" + getExternalPersonId() + "'" +
                ", personKey='" + getPersonKey() + "'" +
                ", mdnValue='" + getMdn() + "'" +
                "}";
    }
}
