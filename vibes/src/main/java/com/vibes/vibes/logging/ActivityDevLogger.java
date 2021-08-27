package com.vibes.vibes.logging;

import android.util.Log;

import com.vibes.vibes.HTTPResource;
import com.vibes.vibes.HTTPResponse;
import com.vibes.vibes.VibesLogger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class ActivityDevLogger implements VibesLogger {

    private static final String TAG = "Vibes";
    private Level level;
    private Collection<String> logs = Collections.synchronizedList(new ArrayList<String>());
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);

    public ActivityDevLogger(Level level) {
        this.level = level;
    }

    @Override
    public <T> void log(HTTPResource<T> resource) {
        if (level == Level.VERBOSE) {
            String log = "[" + dateFormatter.format(new Date()) + "] Request: " + resource.curlString();
            logs.add(log);
            Log.d(TAG, log);
        }
    }

    @Override
    public <T> void log(HTTPResponse response) {
        if (level == Level.VERBOSE) {
            String log = "[" + dateFormatter.format(new Date()) + "] Request: " + response.curlString();
            logs.add(log);
            Log.d(TAG, log);
        }
    }

    @Override
    public void log(Exception error) {
        if (level == Level.ERROR) {
            logs.add(error.getLocalizedMessage());
            Log.e(TAG, error.getLocalizedMessage());
        }
    }

    @Override
    public Collection<String> getLogs() {
        return logs;
    }

    public void log(LogObject logObject) {
        String log = "[" + dateFormatter.format(new Date()) + "]: " + logObject.getmLevel() + ": " + logObject.getMessage();
        if (level.ordinal() <= logObject.getmLevel().ordinal()) {
            writeToConsole(logObject.getmLevel(), log);
            logs.add(log);
        }
    }

    private void writeToConsole(Level level, String message) {
        switch (level) {
            case ERROR:
                Log.e(TAG, message);
                break;
            case INFO:
                Log.i(TAG, message);
                break;
            case WARN:
                Log.w(TAG, message);
            default:
                Log.d(TAG, message);
        }
    }
}
