package com.vibes.vibes;

public class VibesEvent {

    /**
     * Action sent out when a Push Notification has been received from FCM
     */
    public static final String ACTION_PUSH_RECEIVED = "com.vibes.action.push.RECEIVED";

    /**
     * Action sent out when a Push Notification has been clicked on the handset
     */
    public static final String ACTION_PUSH_OPENED = "com.vibes.action.push.OPENED";

    /**
     * Action sent out when a Push Notification has been cleared from the
     * notification panel on the handset either by clicking the "Clear all"
     * button or the individual "X" button on the notification.
     */
    public static final String ACTION_PUSH_DISMISSED = "com.vibes.action.push.DISMISSED";

}