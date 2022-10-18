package com.vibes.vibes.versioning;

import androidx.test.core.app.ApplicationProvider;

import com.vibes.vibes.HTTPResource;
import com.vibes.vibes.TestConfig;
import com.vibes.vibes.Vibes;
import com.vibes.vibes.VibesConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GitVersionTrackerTest extends TestConfig {

    @Before
    public void setUp() throws Exception {
        VibesConfig config = new VibesConfig.Builder().setAppId("TEST_APP_ID").build();
        Vibes.initialize(ApplicationProvider.getApplicationContext(), config);
    }

    @Test
    public void testGetCurrentVersionResource() throws Exception {
        HTTPResource<GitTag> resource = GitVersionTracker.getCurrentVersion();

        assertThat(resource.getPath(), is(GitVersionTracker.GET_TAGS_URL));
        assertThat(resource.getMethod().toString(), is("GET"));
        assertThat(resource.getHeaders().get("Accept"), is("application/vnd.github.v3+json"));
    }
}
