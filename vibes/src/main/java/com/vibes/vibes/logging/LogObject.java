package com.vibes.vibes.logging;

import com.vibes.vibes.VibesLogger;

/**
 * A container for the log message and the required log level to display it in.
 */
public class LogObject {

    private final VibesLogger.Level mLevel;
    private final String message;

    public LogObject(VibesLogger.Level level, String message) {
        this.mLevel = level;
        this.message = message;
    }

    public VibesLogger.Level getmLevel() {
        return mLevel;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return  "[" + mLevel + "] " + message;
    }
}
