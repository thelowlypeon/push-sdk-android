package com.vibes.vibes.versioning;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 18)
public class GitTagTest {
    private static final String VERSION_1 = "0.0.1";
    private static final String VERSION_2 = "0.0.2";
    private static final String VERSION_3 = "0.0.3";

    @Test
    public void testCreateGitTag() {
        GitTag tag1 = new GitTag(VERSION_1);
        assertThat(tag1.getName(), is(VERSION_1));
    }

    @Test
    public void testEmptyBehaviour() {
        GitTag.GitTags tags = new GitTag.GitTags();
        assertThat(tags.getCurrent(), nullValue());
    }

    @Test
    public void testGetCurrentGitTag() {
        GitTag.GitTags tags = new GitTag.GitTags();

        GitTag tag1 = new GitTag(VERSION_1);
        GitTag tag2 = new GitTag(VERSION_2);
        tags.getList().add(tag1);
        tags.getList().add(tag2);
        assertThat(tags.getCurrent(), is(tag2));

        GitTag tag3 = new GitTag(VERSION_3);
        tags.getList().add(tag3);
        assertThat(tags.getCurrent(), is(tag3));
    }

    @Test
    public void testGetTagsUniqueness() {
        GitTag.GitTags tags = new GitTag.GitTags();

        GitTag tag1 = new GitTag(VERSION_2);
        GitTag tag2 = new GitTag(VERSION_2);
        tags.getList().add(tag1);
        tags.getList().add(tag2);
        assertThat(tags.getList().size(), is(1));
    }
}
