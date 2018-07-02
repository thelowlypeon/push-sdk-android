package com.vibes.vibes;

/**
 * A generic interface for being notified about the result of a Vibes method call.
 * @param <T> the type that will be returned on success, e.g. {@link Credential}
 */
public interface VibesListener<T> {
    /**
     * Indicates that the method call was successful.
     * @param value a Resource, e.g. a {@link Credential} object.
     */
    public void onSuccess(T value);

    /**
     * Indicates that the method call was not successful.
     * @param errorText the error text returned, if any
     */
    public void onFailure(String errorText);
}