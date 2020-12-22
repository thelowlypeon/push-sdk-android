package com.vibes.vibes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 18)
public class LocalStorageTest {
    LocalStorage storage;

    @Before
    public void setup() {
        StubLocalStorageAdapter adapter = new StubLocalStorageAdapter();
        this.storage = new LocalStorage(adapter);
    }

    @Test
    public void getStringWithUnknownKey() throws Exception {
        assertThat(this.storage.get("Unknown"), is(nullValue()));
    }

    @Test
    public void getStringWithKnownKey() throws Exception {
        this.storage.put("Oh, Hey", "There");
        assertThat(this.storage.get("Oh, Hey"), is("There"));
    }

    @Test
    public void removeString() throws Exception {
        this.storage.put("Oh, Hey", "There");
        this.storage.remove("Oh, Hey");
        assertThat(this.storage.get("Oh, Hey"), is(nullValue()));
    }

    @Test
    public void getObjectWithUnknownItem() throws Exception {
        LocalObject<Credential> item = new LocalObject<Credential>("unknown", new Credential.CredentialObjectFactory());
        assertThat(this.storage.get(item), is(nullValue()));
    }

    @Test
    public void getObjectWithKnownItem() throws Exception {
        LocalObject<Credential> item = new LocalObject<Credential>("known", new Credential.CredentialObjectFactory());
        Credential credential = new Credential("device-id", "auth-token");

        this.storage.put(item, credential);

        assertThat(this.storage.get(item), is(credential));
    }

    @Test
    public void removeObject() throws Exception {
        LocalObject<Credential> item = new LocalObject<Credential>("known", new Credential.CredentialObjectFactory());
        Credential credential = new Credential("device-id", "auth-token");

        this.storage.put(item, credential);
        this.storage.remove(item);

        assertThat(this.storage.get(item), is(nullValue()));
    }
}