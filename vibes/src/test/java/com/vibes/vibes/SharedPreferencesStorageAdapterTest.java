package com.vibes.vibes;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SharedPreferencesStorageAdapterTest {
    SharedPreferencesStorageAdapter adapter;

    @Before
    public void setup() {
        Context appContext = RuntimeEnvironment.application.getApplicationContext();
        this.adapter = new SharedPreferencesStorageAdapter(appContext);
    }

    @Test
    public void getStringWithUnknownKey() throws Exception {
        assertThat(this.adapter.get("Unknown"), is(nullValue()));
    }

    @Test
    public void getStringWithKnownKey() throws Exception {
        this.adapter.put("Oh, Hey", "There");
        assertThat(this.adapter.get("Oh, Hey"), is("There"));
    }

    @Test
    public void removeString() throws Exception {
        this.adapter.put("Oh, Hey", "There");
        this.adapter.remove("Oh, Hey");
        assertThat(this.adapter.get("Oh, Hey"), is(nullValue()));
    }
}