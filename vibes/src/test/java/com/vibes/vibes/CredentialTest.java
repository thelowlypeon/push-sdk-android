package com.vibes.vibes;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 18)
public class CredentialTest {
    @Test
    public void createFromCorrectJson() throws Exception {
        JSONObject json = new JSONObject("\n" +
                "{\n" +
                "    \"auth_token\": \"mock_auth_token_12473906\",\n" +
                "    \"device\": {\n" +
                "        \"vibes_device_id\": \"MOCK_cb50209b-3c0b-465a-ab7d-cfae7ee2d78e\"\n" +
                "    }\n" +
                "}\n" +
                "\n");
        Credential credential = new Credential(json);
        assertThat(credential.getAuthToken(), is("mock_auth_token_12473906"));
        assertThat(credential.getDeviceID(), is("MOCK_cb50209b-3c0b-465a-ab7d-cfae7ee2d78e"));
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void createFromIncorrectJson() throws Exception {
        JSONObject json = new JSONObject("\n" +
                "{\n" +
                "    \"device\": {\n" +
                "        \"vibes_device_id\": \"MOCK_cb50209b-3c0b-465a-ab7d-cfae7ee2d78e\"\n" +
                "    }\n" +
                "}\n" +
                "\n");
        exception.expect(JSONException.class);
        Credential credential = new Credential(json);
    }

    @Test
    public void equality() throws Exception {
        Credential credential1 = new Credential("id", "token");
        Credential credential2 = new Credential("id", "token");

        assertThat(credential1, is(credential2));
    }

    @Test
    public void inequalityDeviceId() throws Exception {
        Credential credential1 = new Credential("a-id", "token");
        Credential credential2 = new Credential("id", "token");

        assertThat(credential1, is(not(credential2)));
    }

    @Test
    public void inequalityAuthToken() throws Exception {
        Credential credential1 = new Credential("id", "token");
        Credential credential2 = new Credential("id", "a-token");

        assertThat(credential1, is(not(credential2)));
    }

    @Test
    public void objectFactorySerialize() throws Exception {
        Credential credential = new Credential("id", "token");
        String serializedText = new Credential.CredentialObjectFactory().serialize(credential).replaceAll("\\s", "");

        String expected = "{\"device_id\":\"id\",\"auth_token\":\"token\"}";
        assertThat(serializedText, is(expected));
    }

    @Test
    public void objectFactoryCreateInstance() throws Exception {
        String serializedText = "{ \"device_id\": \"id\", \"auth_token\": \"token\" }";
        Credential credential = new Credential.CredentialObjectFactory().createInstance(serializedText);

        assertThat(credential.getAuthToken(), is("token"));
        assertThat(credential.getDeviceID(), is("id"));
    }
}
