package com.vibes.vibes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collection;
import java.util.HashMap;


/**
 * This test attempts to call SDK methods without initializing it, which should lead to crashes except for when it's related to logging.
 */
public class AllVibesCrashTest extends TestConfig {
    private static final String EXCEPTION_MESSAGE = "Attempt to access Vibes instance before calling initialize";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        Vibes.setInstance(null);
    }

    @Test
    public void testAssociateThrowsException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXCEPTION_MESSAGE);
        Vibes.getInstance().associatePerson("person1");
    }

    @Test
    public void testRegisterDeviceThrowsException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXCEPTION_MESSAGE);
        Vibes.getInstance().registerDevice();
    }

    @Test
    public void testUnregisterDeviceThrowsException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXCEPTION_MESSAGE);
        Vibes.getInstance().unregisterDevice();
    }

    @Test
    public void testRegisterPushThrowsException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXCEPTION_MESSAGE);
        Vibes.getInstance().registerPush("token");

    }

    @Test
    public void testUnregisterPushThrowsException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXCEPTION_MESSAGE);
        Vibes.getInstance().unregisterPush();
    }

    @Test
    public void testFetchInboxMessageThrowsException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXCEPTION_MESSAGE);
        Vibes.getInstance().fetchInboxMessage("messageI", new VibesListener<InboxMessage>() {
            @Override
            public void onSuccess(InboxMessage value) {

            }

            @Override
            public void onFailure(String errorText) {

            }
        });
    }

    @Test
    public void testFetchInboxMessageListThrowsException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXCEPTION_MESSAGE);
        Vibes.getInstance().fetchInboxMessages(new VibesListener<Collection<InboxMessage>>() {
            @Override
            public void onSuccess(Collection<InboxMessage> value) {

            }

            @Override
            public void onFailure(String errorText) {

            }
        });
    }

    @Test
    public void testExpireInboxMessageThrowsException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXCEPTION_MESSAGE);

        Vibes.getInstance().expireInboxMessage("messageI", new VibesListener<InboxMessage>() {
            @Override
            public void onSuccess(InboxMessage value) {

            }

            @Override
            public void onFailure(String errorText) {

            }
        });

    }

    @Test
    public void testMarkInboxMessageAsReadThrowsException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXCEPTION_MESSAGE);
        Vibes.getInstance().markInboxMessageAsRead("messageI", new VibesListener<InboxMessage>() {
            @Override
            public void onSuccess(InboxMessage value) {

            }

            @Override
            public void onFailure(String errorText) {

            }
        });
    }

    @Test
    public void testGetPersonThrowsException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXCEPTION_MESSAGE);
        Vibes.getInstance().getPerson(new VibesListener<Person>() {
            @Override
            public void onSuccess(Person value) {

            }

            @Override
            public void onFailure(String errorText) {

            }
        });

    }

    @Test
    public void testOnInboxOpenThrowsException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXCEPTION_MESSAGE);
        Vibes.getInstance().onInboxMessageOpen(new InboxMessage());

    }

    @Test
    public void testOnInboxMessagesFetchedThrowsException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXCEPTION_MESSAGE);
        Vibes.getInstance().onInboxMessagesFetched();
    }

    @Test
    public void testHandleNotificationThrowsException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXCEPTION_MESSAGE);
        Vibes.getInstance().handleNotification(InstrumentationRegistry.getInstrumentation().getContext(), new HashMap<String, String>());

    }

    @Test
    public void testUnconfiguredLoggerNoException() {
        VibesLogger configuredLogger = Vibes.getCurrentLogger();
        assertNotNull(configuredLogger);
        assertEquals(configuredLogger.getClass(), InactiveLogger.class);
    }
}
