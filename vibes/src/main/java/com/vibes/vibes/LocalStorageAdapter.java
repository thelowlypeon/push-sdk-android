package com.vibes.vibes;

/**
 * An interface for managing local storage.
 */
public interface LocalStorageAdapter {
    /**
     * Gets a String value from local storage.
     * @param key the key of the value to find
     */
    String get(String key);

    /**
     * Puts a String value in local storage.
     * @param key the key of the value to store
     * @param value the String value to actually store
     */
    void put(String key, String value);

    /**
     * Removes a String value from local storage.
     * @param key the key of the value to remove
     */
    void remove(String key);
}
