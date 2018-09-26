package com.vibes.vibes;

import java.util.HashMap;

class StubLocalStorageAdapter implements LocalStorageAdapter {
    private HashMap<String, String> map = new HashMap<String, String>();

    public String get(String key) {
        return map.get(key);
    }

    public void put(String key, String value) {
        map.put(key, value);
    }

    public void remove(String key) {
        map.remove(key);
    }
}