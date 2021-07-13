package com.vibes.vibes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 18)
public class InboxMessageTest {

    @Test
    public void createInboxMessageJson() throws Exception {
        JSONObject imagesJson = new JSONObject();
        imagesJson.put("icon", "icon-img-url");
        imagesJson.put("main", "main-icon-img-url");

        String expiresAt = "2019-10-28T14:43:53.359-05:00";
        String createdAt = ISODateFormatter.toISOString(new Date());

        JSONObject inboxCustomDataJson = new JSONObject();
        inboxCustomDataJson.put("key1", "mock-key");
        inboxCustomDataJson.put("key2", "mock-key2");

        JSONObject apprefdataJson = new JSONObject();
        apprefdataJson.put("activityType", "mock-activity-type");
        apprefdataJson.put("activityUid", "mock-activity-uid");
        apprefdataJson.put("personUid", "mock-personUid");
        apprefdataJson.put("flightUid", "mock-flightUid");
        apprefdataJson.put("vibesDeviceId", "mock-vibesDeviceId");

        JSONObject json = new JSONObject();
        json.put("message_uid", "mock-message-uid");
        json.put("push_message_uid", "mock-push-message-uid");
        json.put("subject", "mock-msg-subject");
        json.put("detail", "mock-msg-detail");
        json.put("collapse_key", "mock-collapse_key");
        json.put("content", "mock-content");
        json.put("read", true);
        json.put("expires_at", expiresAt);
        json.put("created_at", createdAt);
        json.put("images", imagesJson);
        json.put("inbox_custom_data", inboxCustomDataJson);
        json.put("apprefdata", apprefdataJson);

        String expectedResult = "{message_uid='mock-message-uid',subject='mock-msg-subject'," +
                "content='mock-content',detail='mock-msg-detail',collapse_key='mock-collapse_key'," +
                "read='true',images=" +
                "'{icon=icon-img-url, main=main-icon-img-url}',inboxCustomData='{key1=mock-key, " +
                "key2=mock-key2}',apprefdata='{personUid=mock-personUid, " +
                "activityUid=mock-activity-uid, vibesDeviceId=mock-vibesDeviceId, " +
                "flightUid=mock-flightUid, activityType=mock-activity-type}'}";

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        InboxMessage inboxMessage = gson.fromJson(json.toString(), InboxMessage.class);
        assertThat(inboxMessage.getDetail(), is("mock-msg-detail"));
        assertThat(inboxMessage.toString(), is(expectedResult));
        assertThat(inboxMessage.getIconImage(), is("icon-img-url"));
        assertThat(inboxMessage.getMainIcon(), is("main-icon-img-url"));
        assertThat(inboxMessage.getEventsMap(), is(notNullValue()));
        assertThat(inboxMessage.getEventsMap().get(InboxMessage.eActivityType), is("mock-activity-type"));
        assertThat(inboxMessage.getEventsMap().get(InboxMessage.eActivityUid), is("mock-activity-uid"));
        assertThat(inboxMessage.getEventsMap().get(InboxMessage.eMessageUid), is("mock-message-uid"));
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

}
