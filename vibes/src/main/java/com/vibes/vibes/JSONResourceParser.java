package com.vibes.vibes;

import org.json.JSONException;

/**
 * A generic interface for parsing an HTTPResource from a JSON string into some object.
 *
 * @param <T> the type of resulting object, e.g. `String` or {@link Credential}.
 */
public interface JSONResourceParser<T> extends ResourceParser<T> {
    /**
     * Parses the passed-in JSON text into an object of type T.
     *
     * @param text the JSON text received from the HTTP response
     */
    T parse(String text) throws JSONException;
}