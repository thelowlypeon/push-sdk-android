package com.vibes.vibes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class PersonManagerTest extends TestConfig {
    PersonManager manager;

    @Before
    public void setup() throws Exception {
        LocalStorage storage = new LocalStorage(new StubLocalStorageAdapter());
        this.manager = new PersonManager(storage);
    }

    @Test
    public void getCurrentWhenEmpty() throws Exception {
        Person person = manager.getCurrent();
        assertThat(person, is(nullValue()));
    }

    @Test
    public void getCurrentWhenNotEmpty() throws Exception {
        Person expectedPerson = new Person("mock_external", "mock_person_key", "mock_mdn");
        manager.setCurrent(expectedPerson);

        Person person = manager.getCurrent();
        assertThat(person.toString(), is(expectedPerson.toString()));
    }

    @Test
    public void setWithNull() throws Exception {
        manager.setCurrent(null);
        assertThat(manager.getCurrent(), is(nullValue()));
    }
}
