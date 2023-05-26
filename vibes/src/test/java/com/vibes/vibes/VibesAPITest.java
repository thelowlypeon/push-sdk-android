package com.vibes.vibes;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class VibesAPITest extends TestConfig {

    @Test
    public void unauthenticatedRequest() throws Exception {
        StubResourceClient client = new StubResourceClient();
        VibesConfig vibesConfig = new VibesConfig.Builder()
                .setAppId("an-app-id")
                .build();
        VibesAPI api = new VibesAPI(client);
        Vibes vibes = new Vibes(vibesConfig, api, new StubCredentialManager(), new StubPersistentEventStorage(), new StubActivityLifecycleListener(), new NotificationFactory(ApplicationProvider.getApplicationContext()));
        Vibes.setInstance(vibes);

        HTTPResource<Credential> resource = APIDefinition.registerDevice("app-key");
        ResourceListener<Credential> listener = new TestResourceListener<Credential>();
        api.request(resource, listener);

        assertThat(client.lastResource, is((Object) resource));
        assertThat(client.lastListener, is((Object) listener));
        assertThat(client.lastBehavior, nullValue());
    }

    @Test
    public void authenticatedRequest() throws Exception {
        StubResourceClient client = new StubResourceClient();
        VibesConfig vibesConfig = new VibesConfig.Builder()
                .setAppId("an-app-id")
                .build();
        VibesAPI api = new VibesAPI(client);
        Vibes vibes = new Vibes(vibesConfig, api, new StubCredentialManager(), new StubPersistentEventStorage(), new StubActivityLifecycleListener(), new NotificationFactory(ApplicationProvider.getApplicationContext()));
        Vibes.setInstance(vibes);

        HTTPResource<Credential> resource = APIDefinition.registerDevice("app-key");
        ResourceListener<Credential> listener = new TestResourceListener<Credential>();

        api.request("token", resource, listener);

        assertThat(client.lastResource, is((Object) resource));
        assertThat(client.lastListener, is((Object) listener));
        assertThat(((AuthTokenBehavior) client.lastBehavior).getAuthToken(), is("token"));
    }
}
