package com.vibes.vibes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class JSONHelpers {
    /**
     * Converts a JSONObject to a HashMap
     * @param json the json to covert
     * @return the result
     * @throws JSONException
     */
    static HashMap<String, String> jsonTo(JSONObject json) throws JSONException {
        HashMap<String, String> map = new HashMap<>();
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            map.put(key, json.getString(key));
        }
        return map;
    }
}
