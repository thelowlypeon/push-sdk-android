package com.vibes.vibes;

import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Locale;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class DeviceTest {
    private Device device;

    @Before
    public void setUp() throws Exception {
        TimeZone timeZone = TimeZone.getTimeZone("America/Chicago");

        Locale.Builder localeBuilder = new Locale.Builder();
        localeBuilder.setLanguage("en");
        localeBuilder.setRegion("us");
        Locale locale = localeBuilder.build();

        this.device = new Device(timeZone, locale, "31d1d747-21f0-482a-a590-b5725ebf6c8a", "1.1",
                42.02, 42.32);
    }

    @Test
    public void getManufacturer() throws Exception {
        assertThat(device.getManufacturer(), is(Build.MANUFACTURER));
    }

    @Test
    public void getModel() throws Exception {
        assertThat(device.getModel(), is(Build.MODEL));
    }

    @Test
    public void getBrand() throws Exception {
        assertThat(device.getBrand(), is("Android"));
    }

    @Test
    public void getVersion() throws Exception {
        assertThat(device.getVersion(), is(Build.VERSION.RELEASE));
    }

    @Test
    public void getTimeZoneIdentifier() throws Exception {
        assertThat(device.getTimeZoneIdentifier(), is("America/Chicago"));
    }

    @Test
    public void getLocaleIdentifier() throws Exception {
        assertThat(device.getLocaleIdentifier(), is("en_US"));
    }

    @Test
    public void encode() throws Exception {
        assertThat(device.encode(), containsString("os_version"));
    }

    @Test
    public void getApplicationVersion() throws Exception {
        assertThat(device.getApplicationVersion(), is("1.1"));
    }

    @Test
    public void getAdvertisingId() throws Exception {
        assertThat(device.getAdvertisingId(), is("31d1d747-21f0-482a-a590-b5725ebf6c8a"));
    }

    @Test
    public void getLongitude() throws Exception {
        assertThat(device.getLongitude(), is(42.32));
    }

    @Test
    public void getLatitude() throws Exception {
        assertThat(device.getLatitude(), is(42.02));
    }

    @Test
    public void encodeLocation() throws Exception {
        System.out.println(device.encode());
        assertThat(device.encode(),
                containsString("\"location\":{\"latitude\":42.02,\"longitude\":42.32}"));
    }
}