package com.vibes.vibes;

import java.util.Collection;

/**
 * An interface for logging the Vibes SDK's HTTP-related logs
 */
public interface VibesLogger {
    /**
     * The logging level types used for a VibesLogger
     */

    enum Level {
        VERBOSE, INFO, ERROR
    }

    /** Logs an HTTP request.
     *
     * @param resource the HTTP resource to log
     */
    <T> void log(HTTPResource<T> resource);

    /**
     * Logs an HTTP response with its accompanying data.
     *
     * - parameters:
     * @param response the HTTP response to log
     */
    <T> void log(HTTPResponse response);

    /**
     * Logs an error.
     *
     * @param error the error that occurred
     */
    void log(Exception error);

    /**
     * Get current logs
     * @return Collection<String>
     */
    Collection<String> getLogs();
}

/**
 * Implements the VibesLogger interface but outputs nothing.
 */
class InactiveLogger implements VibesLogger {
    @Override
    public <T> void log(HTTPResource<T> resource) {

    }

    @Override
    public <T> void log(HTTPResponse response) {

    }

    @Override
    public void log(Exception error) {

    }

    @Override
    public Collection<String> getLogs() {
        return null;
    }
}