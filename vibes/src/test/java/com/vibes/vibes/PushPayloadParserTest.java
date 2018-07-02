package com.vibes.vibes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

/**
 * Created by marius.pop on 11/14/17.
 */

@RunWith(RobolectricTestRunner.class)
public class PushPayloadParserTest {
    Map map;
    PushPayloadParser pushPayloadParser;

    @Before
    public void setUp() throws Exception {
        Map map = new TreeMap();
        map.put("message_uid", "069ef69b-6d07-de1e-a669-77cfa2d1413a");
        map.put("body", "Smart content goes here.");
        map.put("title", "Title");
        map.put("client_app_data", "{\"deep_link\"=>\"XXXXXXX\"}");
        this.map = map;
        this.pushPayloadParser = new PushPayloadParser(map);
    }

    @Test
    public void convertsPayloadToHashMap() throws Exception {
        assertThat(pushPayloadParser.getMap(), is(new HashMap<>(map)));
    }

    @Test
    public void retrievesDeepLink() throws Exception {
        assertThat(pushPayloadParser.getDeepLink(), is("XXXXXXX"));
    }

    @Test
    public void retrievesTitle() throws Exception {
        assertThat(pushPayloadParser.getTitle(), is("Title"));
    }

    @Test
    public void retrievesBody() throws Exception {
        assertThat(pushPayloadParser.getBody(), is("Smart content goes here."));
    }
}
