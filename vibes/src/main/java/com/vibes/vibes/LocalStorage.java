package com.vibes.vibes;


import org.json.JSONException;

/**
 * A mechanism for adding type-safety around storing objects in LocalStorage.
 * @param <T> The Type of the object being stored, e.g. {@link Credential}
 */
class LocalObject<T> {
    /**
     * A string key to identify this object in local storage.
     */
    private String key;

    /**
     * A factory that knows how to serialize/deserialize objects of Type T into/out of Strings.
     */
    private ObjectFactory<T> factory;

    /**
     * Initialize this object
     * @param key a key to identify this object in local storage.
     * @param factory a factory that knows how to serialize/deserialize objects of Type T into/out
     *                of Strings.
     */
    public LocalObject(String key, JSONObjectFactory<T> factory) {
        this.key = key;
        this.factory = factory;
    }

    /**
     * The key to identify this object in local storage.
     * @return the key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * The factory that knows how to serialize/deserialize objects of Type T into/out of Strings.
     * @return the factory
     */
    public ObjectFactory<T> getFactory() {
        return this.factory;
    }
}

/**
 * An interface for a factory that can serialize and deserialize Objects to Strings for storing in a
 * {@link LocalStorageAdapter}.
 * @param <T> The type of object this factory can handle, e.g. {@link Credential}
 */
interface ObjectFactory<T> {
    /**
     * Serializes an Object to a String
     * @param object the object to serialize
     * @return a serialized String
     * @throws JSONException
     */
    String serialize(T object) throws Exception;

    /**
     * Deserializes a string to an object
     * @param serializedText a serialized String
     * @return an object of type T
     * @throws JSONException
     */
    T createInstance(String serializedText) throws Exception;
}

/**
 * An interface for a factory that can serialize and deserialize Objects to Strings of valid JSON
 * for storing in a {@link LocalStorageAdapter}.
 * @param <T> The type of object this factory can handle, e.g. {@link Credential}
 */
interface JSONObjectFactory<T> extends ObjectFactory<T> {
    /**
     * Serializes an Object to a JSON string.
     * @param object the object to serialize
     * @return a String of valid JSON
     * @throws JSONException
     */
    String serialize(T object) throws JSONException;

    /**
     * Deserializes a string of valid JSON to an object
     * @param jsonString a String of valid JSON
     * @return an object of type T
     * @throws JSONException
     */
    T createInstance(String jsonString) throws JSONException;
}

/**
 * A collection of storage keys used within the library. Keys can be either Strings, or
 * LocalObjects.
 */
class LocalObjectKeys {
    /**
     * The current push token for the app.
     */
    static final String pushToken = "VIBES_PUSH_TOKEN";

    /**
     * The current {@link Credential} in use.
     */
    static final LocalObject<Credential> currentCredential =
            new LocalObject<Credential>("VIBES_CURRENT_CREDENTIAL", new Credential.CredentialObjectFactory());

    /**
     * The stored events awaiting upload to Vibes API.
     */
    static final LocalObject<EventCollection> storedEvents =
            new LocalObject<EventCollection>("VIBES_STORED_EVENTS", new EventCollection.EventCollectionObjectFactory());
}

/**
 * A basic class that allows storing and fetching Strings and LocalObjects via a
 * {@link LocalStorageAdapter}.
 */
class LocalStorage implements LocalStorageAdapter {
    private static final String TAG = "LocalStorageAdapter";

    /**
     * The adapter to use to implement some sort of local storage mechanism.
     */
    private LocalStorageAdapter adapter;

    /**
     * Initialize this object.
     * @param adapter the adapter to use to implement some sort of local storage mechanism
     */
    public LocalStorage(LocalStorageAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Gets a String value from local storage.
     * @param key the key of the value to find
     * @return the found String, or null
     */
    public String get(String key) {
        return adapter.get(key);
    }

    /**
     * Puts a String value in local storage.
     * @param key the key of the value to store
     * @param value the String value to actually store
     */
    public void put(String key, String value) {
        adapter.put(key, value);
    }

    /**
     * Removes a String value from local storage.
     * @param key the key of the value to remove
     */
    public void remove(String key) {
        adapter.remove(key);
    }

    /**
     * Gets an Object from local storage
     * @param item the LocalObject to find
     * @param <T> the Type of the LocalObject
     * @return an instance of type T, or null
     */
    public <T> T get(LocalObject<T> item) {
        try {
            String string = get(item.getKey());
            if (string == null) {
                return null;
            }

            return item.getFactory().createInstance(string);
        } catch (Exception e) {
            Vibes.getCurrentLogger().log(e);
        }
        return null;
    }

    /**
     * Puts an Object in local storage.
     * @param item the LocalObject to find
     * @param object the Object to storage
     * @param <T> the Type of the LocalObject
     */
    public <T> void put(LocalObject<T> item, T object) {
        try {
            put(item.getKey(), item.getFactory().serialize(object));
        } catch (Exception e) {
            Vibes.getCurrentLogger().log(e);
        }
    }

    /**
     * Removes an Object from local storage
     * @param item the LocalObject to remove
     * @param <T> the Type of the LocalObject
     */
    public <T> void remove(LocalObject<T> item) {
        remove(item.getKey());
    }
}