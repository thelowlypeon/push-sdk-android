package com.vibes.vibes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class VibesAPITest {
    HTTPResource<Credential> resource = APIDefinition.registerDevice("app-key");
    ResourceListener<Credential> listener = new TestResourceListener<Credential>();

    @Test
    public void unauthenticatedRequest() throws Exception {
        StubResourceClient client = new StubResourceClient();
        VibesAPI api = new VibesAPI(client);

        api.request(resource, listener);

        assertThat(client.lastResource, is((Object) resource));
        assertThat(client.lastListener, is((Object) listener));
        assertThat(client.lastBehavior, nullValue());
    }

    @Test
    public void authenticatedRequest() throws Exception {
        StubResourceClient client = new StubResourceClient();
        VibesAPI api = new VibesAPI(client);

        api.request("token", resource, listener);

        assertThat(client.lastResource, is((Object) resource));
        assertThat(client.lastListener, is((Object) listener));
        assertThat(((AuthTokenBehavior) client.lastBehavior).getAuthToken(), is("token"));
    }
}
