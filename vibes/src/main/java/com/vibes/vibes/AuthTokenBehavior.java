package com.vibes.vibes;

import java.net.URLConnection;

/**
 * An HTTP Resource behavior that handles adding an auth token in the "Authorization" header for
 * authenticated requests to the Vibes API.
 */
class AuthTokenBehavior implements ResourceBehavior {
    /**
     * The authentication token to use, e.g. "mock_auth_token_1234567"
     */
    private String authToken;

    /**
     * Initialize this object
     * @param authToken the authentication token to use for requests to the Vibes API.
     */
    public AuthTokenBehavior(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Modifies the connection by setting the "Authorization" request header.
     * @param connection the URLConnection to modify
     */
    @Override
    public void modifyConnection(URLConnection connection) {
        connection.setRequestProperty("Authorization", "MobileAppToken " + authToken);
    }

    /**
     * Returns the auth token in use.
     */
    public String getAuthToken() {
        return authToken;
    }
}