package com.vibes.vibes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CredentialManagerTest {
    CredentialManager manager;

    @Before
    public void setup() throws Exception {
        LocalStorage storage = new LocalStorage(new StubLocalStorageAdapter());
        this.manager = new CredentialManager(storage);
    }

    @Test
    public void getCurrentWhenEmpty() throws Exception {
        Credential credential = manager.getCurrent();
        assertThat(credential, is(nullValue()));
    }

    @Test
    public void getCurrentWhenNotEmpty() throws Exception {
        Credential expectedCredential = new Credential("id", "token");
        manager.setCurrent(expectedCredential );

        Credential credential = manager.getCurrent();
        assertThat(credential, is(expectedCredential));
    }

    @Test
    public void setWithNull() throws Exception {
        manager.setCurrent(null);
        assertThat(manager.getCurrent(), is(nullValue()));
    }
}
