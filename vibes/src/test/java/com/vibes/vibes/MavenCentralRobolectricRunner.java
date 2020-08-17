package com.vibes.vibes;

import org.junit.runners.model.InitializationError;
import org.robolectric.RoboSettings;
import org.robolectric.RobolectricTestRunner;

public class MavenCentralRobolectricRunner extends RobolectricTestRunner {
    /**
     * Creates a runner to run {@code testClass}. Looks in your working directory for your AndroidManifest.xml file
     * and res directory by default. Use the {@link org.robolectric.annotation.Config} annotation to configure.
     *
     * @param testClass the test class to be run
     * @throws InitializationError if junit says so
     */
    public MavenCentralRobolectricRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    static {
        RoboSettings.setMavenRepositoryId("mavenCentral");
        RoboSettings.setMavenRepositoryUrl("https://repo1.maven.org/maven2");
    }
}
