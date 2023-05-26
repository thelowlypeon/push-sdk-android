package com.vibes.vibes.logging;

import com.vibes.vibes.HTTPResource;
import com.vibes.vibes.HTTPResponse;
import com.vibes.vibes.VibesLogger;

import java.util.Collection;
import java.util.Collections;

/**
 * A combined logger that receives and delegates to SDK and Custom logging classes as configured.
 */
public class CombinedLogger implements VibesLogger {
    private VibesLogger consoleLogger;
    private VibesLogger customLogger;

    public CombinedLogger(VibesLogger consoleLogger, VibesLogger customLogger) {
        this.consoleLogger = consoleLogger;
        this.customLogger = customLogger;
    }

    @Override
    public <T> void log(HTTPResource<T> resource) {
        this.consoleLogger.log(resource);
        this.customLogger.log(resource);
    }

    @Override
    public <T> void log(HTTPResponse response) {
        this.consoleLogger.log(response);
        this.customLogger.log(response);
    }

    @Override
    public void log(Exception error) {
        this.consoleLogger.log(error);
        this.customLogger.log(error);
    }

    /**
     * If there is a custom logger, return it's list of logs first, so developer doesn't notice the custom logger in any way.
     * @return
     */
    @Override
    public Collection<String> getLogs() {
        if (customLogger != null) {
            return customLogger.getLogs();
        }
        if (consoleLogger != null) {
            return consoleLogger.getLogs();
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public void log(LogObject logObject) {
        this.consoleLogger.log(logObject);
        this.customLogger.log(logObject);
    }

    public VibesLogger getConsoleLogger() {
        return consoleLogger;
    }

    public VibesLogger getCustomLogger() {
        return customLogger;
    }
}
