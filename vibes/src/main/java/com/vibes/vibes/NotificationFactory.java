package com.vibes.vibes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

/**
 * The default implementation to build a notification based on a payload from the Vibes Push Service
 * Override this class to provide a custom implementation of the notification.
 */
public class NotificationFactory {

    private static final String DEFAULT_CHANNEL = "VIBES";

    private final BitmapExtractor bitmapExtractor;
    private final SoundExtractor soundExtractor;

    private final Intent contentIntent;
    private final Intent deleteIntent;

    private PushPayloadParser pushModel;

    public NotificationFactory(Context context) {
        this(context, new HTTPBitmapExtractor(), new NoOpSoundExtractor());
    }

    /**
     * Construct the Notification Factory
     *
     * @param bitmapExtractor - used to determine how to create largeIcon displays.
     * @param soundExtractor  - used to determine how to create sounds for notification.
     */
    public NotificationFactory(Context context, BitmapExtractor bitmapExtractor, SoundExtractor soundExtractor) {
        String packageName = context.getApplicationInfo().packageName;

        this.bitmapExtractor = bitmapExtractor;
        this.soundExtractor = soundExtractor;

        this.contentIntent = new Intent(VibesEvent.ACTION_PUSH_OPENED)
                .addCategory(packageName)
                .setPackage(packageName);

        this.deleteIntent = new Intent(VibesEvent.ACTION_PUSH_DISMISSED)
                .addCategory(packageName)
                .setPackage(packageName);
    }

    /**
     * Creates the notification builder depending on the version of Android.
     *
     * @param context - needed to locate the notificationManager
     * @return
     */
    private NotificationCompat.Builder createBuilder(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return null;
        }

        NotificationCompat.Builder builder;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = getNotificationChannel(context);
            notificationManager.createNotificationChannel(notificationChannel);

            builder = new NotificationCompat.Builder(context, notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(context);

            handlePriority(builder);
            handleCustomSound(builder, context);
        }

        handleContent(builder, context);
        handleDefaultSound(builder);
        handleBadging(builder);
        handleRichPush(builder);

        return builder;
    }

    /**
     * Handles priority of the notification on pre Oreo devices.
     *
     * @param builder - Current Notification Builder
     */
    private void handlePriority(NotificationCompat.Builder builder) {
        if (pushModel.getPriority() != null) {
            builder.setPriority(pushModel.getPriority());
        }
    }

    /**
     * Set the default sound when no custom sound is provided.
     *
     * @param builder - Current Notification Builder
     */
    private void handleDefaultSound(NotificationCompat.Builder builder) {
        if (isDefaultSound(pushModel.getSoundNoExt())) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        } else if (pushModel.getSoundNoExt() == null) {
            builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
        }
    }

    /**
     * Method called to handle title, small icon, content text, and auto-cancel.
     *
     * @param builder - Current Notification Builder
     * @param context
     */
    private void handleContent(NotificationCompat.Builder builder, Context context) {
        builder.setContentTitle(pushModel.getTitle())
                .setSmallIcon(context.getApplicationInfo().icon)
                .setContentText(pushModel.getBody())
                .setAutoCancel(true);
    }


    /**
     * Method called to handle custom sounds on pre Oreo devices.
     *
     * @param builder - Current Notification Builder
     * @param context
     */
    private void handleCustomSound(NotificationCompat.Builder builder, Context context) {
        if (isValidCustomSound(pushModel.getSoundNoExt())) {
            Uri sound = Uri.parse("android.resource://" +
                    context.getPackageName() + "/raw/" +
                    pushModel.getSoundNoExt());
            builder.setSound(sound);
        }
    }

    /**
     * Creates the notification channel on Oreo and above.
     * Sets the sound on the channel and importance.
     *
     * @param context
     * @return The notification channel created
     */
    @RequiresApi(android.os.Build.VERSION_CODES.O)
    @NonNull
    private NotificationChannel getNotificationChannel(Context context) {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        if (pushModel.getPriority() != null) {
            importance = pushModel.getPriority();
        }

        String channelId = DEFAULT_CHANNEL;
        if (pushModel.getNotificationChannel() != null) {
            channelId = pushModel.getNotificationChannel();
        }

        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId, importance);

        if (isValidCustomSound(pushModel.getSoundNoExt())) {
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            notificationChannel.setSound(Uri.parse("android.resource://" +
                    context.getPackageName() + "/raw/" +
                    pushModel.getSoundNoExt()), att);
        } else if (!isDefaultSound(pushModel.getSoundNoExt())) {
            notificationChannel.setImportance(NotificationManager.IMPORTANCE_LOW);
        }
        return notificationChannel;
    }

    /**
     * Method called to setup the application badging. On Android the badging is always incremental
     * (ex: 2 push notification with badge value = 5, results to show a badge with a value of 10).
     * On iOS, it's always the absolute value
     * (ex: 2 push notification with badge value = 5, results to show a badge with a value of 5).
     *
     * @param builder: NotificationCompat.Builder
     */
    private void handleBadging(NotificationCompat.Builder builder) {
        if (pushModel.getBadgeNumber() != null) {
            builder.setNumber(pushModel.getBadgeNumber());
        }
    }

    /**
     * Method called in case a rich content exists in the pushpaylod. If yes, it will download
     * the image and set it up in the builder.
     */
    private void handleRichPush(NotificationCompat.Builder builder) {
        if (pushModel.getRichPushMediaURL() != null) {
            Bitmap imageDownloaded = bitmapExtractor.getBitmapFrom(pushModel.getRichPushMediaURL());
            builder.setLargeIcon(imageDownloaded)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(imageDownloaded));
        }
    }

    /**
     * Check if the sound specified in the push payload is a custom sound.
     */
    private boolean isValidCustomSound(String sound) {
        if (sound == null || sound.isEmpty() || sound.equalsIgnoreCase("default")) {
            return false;
        }
        return true;
    }

    /**
     * Check if the sound specified in the push payload is a default sound.
     */
    private boolean isDefaultSound(String sound) {
        if (sound != null && sound.equalsIgnoreCase("default")) {
            return true;
        }
        return false;
    }

    /**
     * Sets the push model for the notification.
     *
     * @param pushModel - the notification payload
     * @return
     */
    private void setPushModel(PushPayloadParser pushModel) {
        this.pushModel = pushModel;
        contentIntent.putExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA, pushModel.getMap());
        deleteIntent.putExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA, pushModel.getMap());
    }

    /**
     * Get the builder made for the specified pushModel.
     * Override this method to provide a custom implementation of the Notification.
     *
     * @param pushModel - the notification payload
     * @param context   - needed to create the notification builder
     * @return
     */
    public NotificationCompat.Builder getBuilder(PushPayloadParser pushModel, Context context) {
        setPushModel(pushModel);

        PendingIntent contentPendingIntent = PendingIntent.getBroadcast(context,
                (int) System.currentTimeMillis(),
                contentIntent, PendingIntent.FLAG_ONE_SHOT);

        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context,
                (int) System.currentTimeMillis(),
                deleteIntent, PendingIntent.FLAG_ONE_SHOT);

        return createBuilder(context)
                .setContentIntent(contentPendingIntent)
                .setDeleteIntent(deletePendingIntent);
    }

    /**
     * Create the notification for a specified pushModel.
     *
     * @param pushModel - the notification payload
     * @param context   - needed to construct notification channels and access system resources
     * @return
     */
    public Notification build(PushPayloadParser pushModel, Context context) {
        return getBuilder(pushModel, context).build();
    }
}

