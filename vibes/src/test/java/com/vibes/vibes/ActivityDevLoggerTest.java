package com.vibes.vibes;

import androidx.test.core.app.ApplicationProvider;

import com.vibes.vibes.logging.ActivityDevLogger;
import com.vibes.vibes.logging.CombinedLogger;
import com.vibes.vibes.logging.LogObject;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActivityDevLoggerTest extends TestConfig {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testBuilderWithLogger() {
        VibesConfig config = new VibesConfig.Builder().setAppId("appId").enableDevLogging(VibesLogger.Level.INFO).build();
        Vibes.initialize(ApplicationProvider.getApplicationContext(), config);

        assertEquals(config.getLogger().getClass(), CombinedLogger.class);
        CombinedLogger logger = (CombinedLogger) config.getLogger();
        assertEquals(logger.getConsoleLogger().getClass(), ActivityDevLogger.class);
    }

    @Test
    public void testLogsErrorSuccessful() {
        VibesConfig config = new VibesConfig.Builder().setAppId("appId").enableDevLogging(VibesLogger.Level.ERROR).build();
        Vibes.initialize(ApplicationProvider.getApplicationContext(), config);

        CombinedLogger logger = (CombinedLogger) config.getLogger();
        int currentLogsBefore = logger.getConsoleLogger().getLogs().size();

        LogObject logObject = new LogObject(VibesLogger.Level.ERROR,
                "Testing error message");

        logger.log(logObject);

        int currentLogsAfter = logger.getConsoleLogger().getLogs().size();

        int logDiff = currentLogsAfter - currentLogsBefore;

        assertEquals(1, logDiff);
    }

    @Test
    public void testLogsErrorUnSuccessful() {
        VibesConfig config = new VibesConfig.Builder().setAppId("appId").enableDevLogging(VibesLogger.Level.ERROR).build();
        Vibes.initialize(ApplicationProvider.getApplicationContext(), config);

        CombinedLogger logger = (CombinedLogger) config.getLogger();
        int currentLogsBefore = logger.getConsoleLogger().getLogs().size();

        LogObject logObject = new LogObject(VibesLogger.Level.WARN,
                "Testing warn message");
        LogObject logObject1 = new LogObject(VibesLogger.Level.INFO,
                "Testing info message");
        LogObject logObject2 = new LogObject(VibesLogger.Level.VERBOSE,
                "Testing verbose message");
        logger.log(logObject);
        logger.log(logObject1);
        logger.log(logObject2);

        int currentLogsAfter = logger.getConsoleLogger().getLogs().size();

        int logDiff = currentLogsAfter - currentLogsBefore;

        assertEquals(0, logDiff);
    }

    @Test
    public void testLogsWarnUnSuccessful() {
        VibesConfig config = new VibesConfig.Builder().setAppId("appId").enableDevLogging(VibesLogger.Level.ERROR).build();
        Vibes.initialize(ApplicationProvider.getApplicationContext(), config);

        CombinedLogger logger = (CombinedLogger) config.getLogger();
        int currentLogsBefore = logger.getConsoleLogger().getLogs().size();

        LogObject logObject = new LogObject(VibesLogger.Level.INFO,
                "Testing info message");

        logger.log(logObject);

        int currentLogsAfter = logger.getConsoleLogger().getLogs().size();

        int logDiff = currentLogsAfter - currentLogsBefore;

        assertEquals(0, logDiff);
    }

    @Test
    public void testLogsInfoUnSuccessful() {
        VibesConfig config = new VibesConfig.Builder().setAppId("appId").enableDevLogging(VibesLogger.Level.ERROR).build();
        Vibes.initialize(ApplicationProvider.getApplicationContext(), config);

        CombinedLogger logger = (CombinedLogger) config.getLogger();
        int currentLogsBefore = logger.getConsoleLogger().getLogs().size();

        LogObject logObject = new LogObject(VibesLogger.Level.INFO,
                "Testing error message");
        logger.log(logObject);

        int currentLogsAfter = logger.getConsoleLogger().getLogs().size();

        int logDiff = currentLogsAfter - currentLogsBefore;

        assertEquals(0, logDiff);
    }

    @Test
    public void testLogsVerboseUnSuccessful() {
        VibesConfig config = new VibesConfig.Builder().setAppId("appId").enableDevLogging(VibesLogger.Level.ERROR).build();
        Vibes.initialize(ApplicationProvider.getApplicationContext(), config);

        CombinedLogger logger = (CombinedLogger) config.getLogger();
        int currentLogsBefore = logger.getConsoleLogger().getLogs().size();

        LogObject logObject = new LogObject(VibesLogger.Level.VERBOSE,
                "Testing error message");

        logger.log(logObject);

        int currentLogsAfter = logger.getConsoleLogger().getLogs().size();

        int logDiff = currentLogsAfter - currentLogsBefore;

        assertEquals(0, logDiff);
    }

    @Test
    public void testLogsWarnSuccessful() {
        VibesConfig config = new VibesConfig.Builder().setAppId("appId").enableDevLogging(VibesLogger.Level.WARN).build();
        Vibes.initialize(ApplicationProvider.getApplicationContext(), config);

        CombinedLogger logger = (CombinedLogger) config.getLogger();
        int currentLogsBefore = logger.getConsoleLogger().getLogs().size();

        LogObject logObject = new LogObject(VibesLogger.Level.WARN,
                "Testing warn message");
        LogObject logObject1 = new LogObject(VibesLogger.Level.ERROR,
                "Testing error message");
        LogObject logObject2 = new LogObject(VibesLogger.Level.VERBOSE,
                "Testing verbose message");
        logger.log(logObject);
        logger.log(logObject1);
        logger.log(logObject2);

        int currentLogsAfter = logger.getConsoleLogger().getLogs().size();

        int logDiff = currentLogsAfter - currentLogsBefore;

        assertEquals(2, logDiff);
    }

    @Test
    public void testLogsInfoSuccessful() {
        VibesConfig config = new VibesConfig.Builder().setAppId("appId").enableDevLogging(VibesLogger.Level.INFO).build();
        Vibes.initialize(ApplicationProvider.getApplicationContext(), config);

        CombinedLogger logger = (CombinedLogger) config.getLogger();
        int currentLogsBefore = logger.getConsoleLogger().getLogs().size();

        LogObject logObject = new LogObject(VibesLogger.Level.WARN,
                "Testing warn message");
        LogObject logObject2 = new LogObject(VibesLogger.Level.INFO,
                "Testing info message");
        LogObject logObject1 = new LogObject(VibesLogger.Level.ERROR,
                "Testing error message");
        LogObject logObject3 = new LogObject(VibesLogger.Level.VERBOSE,
                "Testing verbose message");
        logger.log(logObject);
        logger.log(logObject1);
        logger.log(logObject2);
        logger.log(logObject3);

        int currentLogsAfter = logger.getConsoleLogger().getLogs().size();

        int logDiff = currentLogsAfter - currentLogsBefore;

        assertEquals(3, logDiff);
    }

    @Test
    public void testLogsVerboseSuccessful() {
        VibesConfig config = new VibesConfig.Builder().setAppId("appId").enableDevLogging(VibesLogger.Level.VERBOSE).build();

        CombinedLogger logger = (CombinedLogger) config.getLogger();
        int currentLogsBefore = logger.getConsoleLogger().getLogs().size();

        LogObject logObject = new LogObject(VibesLogger.Level.WARN,
                "Testing warn message");
        LogObject logObject2 = new LogObject(VibesLogger.Level.INFO,
                "Testing info message");
        LogObject logObject1 = new LogObject(VibesLogger.Level.ERROR,
                "Testing error message");
        LogObject logObject3 = new LogObject(VibesLogger.Level.VERBOSE,
                "Testing verbose message");
        logger.log(logObject);
        logger.log(logObject1);
        logger.log(logObject2);
        logger.log(logObject3);

        int currentLogsAfter = logger.getConsoleLogger().getLogs().size();

        int logDiff = currentLogsAfter - currentLogsBefore;

        assertEquals(4, logDiff);
    }
}
