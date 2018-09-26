package com.vibes.vibes;

/**
 * An interface for what it means to be considered a Vibes API.
 */
interface VibesAPIInterface {
    /**
     * Makes an unauthenticated API request.
     * @param resource the {@link HTTPResource} to request
     * @param listener the {@link ResourceListener} to notify about success or failure of the request
     * @param <T> the Type of the {@link HTTPResource} that is being requested, e.g. {@link Credential}
     */
    <T> void request(HTTPResource<T> resource, ResourceListener<T> listener);

    /**
     * Makes an authenticated API request.
     * @param authToken the authentication token to use for the request
     * @param resource the {@link HTTPResource} to request
     * @param listener the {@link ResourceListener} to notify about success or failure of the request
     * @param <T> the Type of the {@link HTTPResource} that is being requested, e.g. {@link Credential}
     */
    <T> void request(String authToken, HTTPResource<T> resource, ResourceListener<T> listener);
}

/**
 * Handles communication with the Vibes API. Basically a thin wrapper to layer in Vibes-specific
 * functionality around {@link ResourceClient}.
 */
class VibesAPI implements VibesAPIInterface {
    /**
     * The HTTP Resource client to use for making requests.
     */
    private ResourceClientInterface client;

    /**
     * Initialize this object.
     * @param client the HTTP Resource client to use for making requests
     */
    public VibesAPI(ResourceClientInterface client) {
        this.client = client;
    }

    /**
     * Makes an unauthenticated API request.
     * @param resource the {@link HTTPResource} to request
     * @param listener the {@link ResourceListener} to notify about success or failure of the request
     * @param <T> the Type of the {@link HTTPResource} that is being requested, e.g. {@link Credential}
     */
    public <T> void request(HTTPResource<T> resource, ResourceListener<T> listener) {
        client.request(resource, null, listener);
    }

    /**
     * Makes an authenticated API request.
     * @param authToken the authentication token to use for the request
     * @param resource the {@link HTTPResource} to request
     * @param listener the {@link ResourceListener} to notify about success or failure of the request
     * @param <T> the Type of the {@link HTTPResource} that is being requested, e.g. {@link Credential}
     */
    public <T> void request(String authToken, HTTPResource<T> resource, ResourceListener<T> listener) {
        ResourceBehavior behavior = new AuthTokenBehavior(authToken);
        client.request(resource, behavior, listener);
    }
}
