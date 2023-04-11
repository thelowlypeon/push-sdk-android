package com.vibes.vibes;

/**
 * A generic interface for parsing an HTTPResource from a string into some object.
 * @param <T> the type of resulting object, e.g. `String` or {@link Credential}.
 */
public interface ResourceParser<T> {
    /**
     * Parses the passed-in text into an object of type T.
     * @param text the text received from the HTTP response
     */
    T parse(String text) throws Exception;
}
