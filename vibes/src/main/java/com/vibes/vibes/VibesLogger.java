package com.vibes.vibes;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

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
 * A logger that sends the output to the Android Monitor
 */
class MonitorLogger implements VibesLogger {
    /**
     * The tag to use in the android monitor when logging a message.
     */
    private static String TAG = "Vibes";

    /**
     * The current logging level.
     */
    private Level level;

    private Collection<String> logs = Collections.synchronizedList(new ArrayList<String>());
    private SimpleDateFormat dateFormater = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);

    /**
     * Initialize this object.
     * @param level the logging level to use when outputting to the android monitor.
     */
    MonitorLogger(Level level) {
        this.level = level;
    }

    @Override
    public <T> void log(HTTPResource<T> resource) {
        String log = "[" + dateFormater.format(new Date()) + "] Request: " + resource.curlString();
        switch (level) {
            case VERBOSE:
                Log.d(TAG, log);
            case INFO:
                Log.d(TAG, log);
        }
        logs.add(log);
    }

    @Override
    public <T> void log(HTTPResponse response) {
        String log = "[" + dateFormater.format(new Date()) + "] Request: " + response.curlString();
        switch (level) {
            case VERBOSE:
                Log.d(TAG, log);
            case INFO:
                Log.d(TAG, log);
        }
        logs.add(log);
    }

    @Override
    public void log(Exception error) {
        logs.add(error.getLocalizedMessage());
        Log.e(TAG, "--> Error: ", error);
    }

    @Override
    public Collection<String> getLogs() {
        return logs;
    }
}