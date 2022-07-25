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
import static org.hamcrest.MatcherAssert.assertThat;

public class PersonTest extends TestConfig {

    @Test
    public void createPersonCorrectJson() throws Exception {
        JSONObject json = new JSONObject();
        JSONObject mobilePhone = new JSONObject();
        mobilePhone.put("mdn", "mock_mdn_value_321");
        json.put("external_person_id", "mock_external_id_1234");
        json.put("person_key", "mock_person_key_123");
        json.put("mobile_phone", mobilePhone);

        Person person = new Person(json);
        assertThat(person.getExternalPersonId(), is("mock_external_id_1234"));
        assertThat(person.getMdn(), is("mock_mdn_value_321"));
        assertThat(person.getPersonKey(), is("mock_person_key_123"));
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void createPersonInCorrectJson() throws Exception {
        JSONObject json = new JSONObject();
        json.put("person_key", "mock_person_key_123");

        exception.expect(JSONException.class);
        Person person = new Person(json);
    }

    @Test
    public void personObjectFactorySerialize() throws Exception {
        Person person = new Person("mock_external_id1", "mock_person_key32", "mock_mdn");
        String serializeText = new Person.PersonObjectFactory().serialize(person).replaceAll("\\s", "");

        String expectedText = "{\"mdn\":\"mock_mdn\",\"person_key\":\"mock_person_key32\",\"external_person_id\":\"mock_external_id1\"}";
        assertThat(serializeText, is(expectedText));
    }

    @Test
    public void personObjectFactoryCreateInstance() throws Exception {
        String serializedText = "{\"mdn\":\"mock_mdn\",\"person_key\":\"mock_person_key32\",\"external_person_id\":\"mock_external_id1\"}";
        Person person = new Person.PersonObjectFactory().createInstance(serializedText);

        assertThat(person.getPersonKey(), is("mock_person_key32"));
        assertThat(person.getMdn(), is("mock_mdn"));
        assertThat(person.getExternalPersonId(), is("mock_external_id1"));
    }
}
