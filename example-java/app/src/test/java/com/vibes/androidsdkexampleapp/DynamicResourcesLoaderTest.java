package com.vibes.androidsdkexampleapp;

import android.content.Context;

import com.vibes.vibes.DynamicResourcesLoader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.hamcrest.number.OrderingComparison.greaterThan;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19)
public class DynamicResourcesLoaderTest {
    private DynamicResourcesLoader provider;

    @Before
    public void setUp() {
        provider = new DynamicResourcesLoader(RuntimeEnvironment.application.getBaseContext());
    }

    @Test
    public void testGetMissingNotifIconResource() {
        Context context = RuntimeEnvironment.application.getBaseContext();
        int result = provider.getNotifIconResourceId("ic_stat_missing_notif_icon","drawable", context.getPackageName());
        assertThat(result, is(lessThan(1)));
    }


    @Test
    public void testGetNotifIconResource() {
        int result = provider.getNotifIconResourceId();
        assertThat(result, is(greaterThan(0)));
    }
}
