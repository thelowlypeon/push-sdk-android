package com.vibes.vibes;

import android.util.Log;

import com.vibes.vibes.logging.LogObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * A logger that sends the output to the Android Monitor
 */
public class MonitorLogger implements VibesLogger {
    /**
     * The tag to use in the android monitor when logging a message.
     */
    private static final String TAG = "Vibes";

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
    public MonitorLogger(Level level) {
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

    @Override
    public void log(LogObject logObject) {

    }
}
