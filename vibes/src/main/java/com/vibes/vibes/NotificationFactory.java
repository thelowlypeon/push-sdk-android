package com.vibes.vibes;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.vibes.vibes.logging.LogObject;

import java.util.List;

/**
 * The default implementation to build a notification based on a payload from the Vibes Push Service
 * Override this class to provide a custom implementation of the notification.
 */
public class NotificationFactory {

    private static final String DEFAULT_CHANNEL = "VIBES";

    private final BitmapExtractor bitmapExtractor;
    private final SoundExtractor soundExtractor;
    private final DynamicResourcesLoader dynamicResourcesLoader;

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
        this(context, bitmapExtractor, soundExtractor, new DynamicResourcesLoader(context));
    }

    /**
     * Construct the Notification Factory
     *
     * @param bitmapExtractor - used to determine how to create largeIcon displays.
     * @param soundExtractor  - used to determine how to create sounds for notification.
     */
    public NotificationFactory(Context context, BitmapExtractor bitmapExtractor, SoundExtractor soundExtractor, DynamicResourcesLoader loader) {
        this.bitmapExtractor = bitmapExtractor;
        this.soundExtractor = soundExtractor;
        this.dynamicResourcesLoader = loader;

        String packageName = context.getApplicationInfo().packageName;
        this.contentIntent = new Intent(VibesEvent.ACTION_PUSH_OPENED)
                .addCategory(packageName)
                .setPackage(packageName);

        this.deleteIntent = new Intent(VibesEvent.ACTION_PUSH_DISMISSED)
                .addCategory(packageName)
                .setPackage(packageName);
        this.contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        int resourceId = dynamicResourcesLoader.getNotifIconResourceId();
        if (resourceId < 1) {
            resourceId = context.getApplicationInfo().icon;
        }

        String body = pushModel.getBody();
        if (body == null || body.length() <= 37) {
            builder.setContentTitle(pushModel.getTitle())
                    .setSmallIcon(resourceId)
                    .setContentText(body)
                    .setAutoCancel(true);
        } else {
            builder.setContentTitle(pushModel.getTitle())
                    .setSmallIcon(resourceId)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                    .setAutoCancel(true);
        }
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

        int pendingIntentFlags =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                        ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        : PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent contentPendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Uri uri = pushModel.getDeeplinkUri();
            Intent linkableIntent = contentIntent;
            if (uri != null) {
                linkableIntent = createDeeplinkIntent(context, pushModel, uri);
            }
            Class<? extends Activity> cls = getActivity(context, linkableIntent);
            if (cls == null) {
                String message = "No Activity is configured to receive the intent {}. This notification only be viewed, not opened. Please configure an appropriate Activity with " +
                        "the relevant category, action='" + VibesEvent.ACTION_PUSH_OPENED + "' and data tags that matching this notification";
                Vibes.getCurrentLogger().log(new LogObject(VibesLogger.Level.WARN, message));
                contentPendingIntent = PendingIntent.getActivity(context,
                        (int) System.currentTimeMillis(),
                        linkableIntent, pendingIntentFlags);
            } else {
                Vibes.getCurrentLogger().log(new LogObject(VibesLogger.Level.INFO, "Push received. Opening will navigate to " + cls));
                Intent onPushOpenedIntent = createPushOpenedIntent(context, pushModel, cls);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntentWithParentStack(onPushOpenedIntent);
                contentPendingIntent = stackBuilder.getPendingIntent(0, pendingIntentFlags);
            }
        } else {
            contentPendingIntent = PendingIntent.getBroadcast(context,
                    (int) System.currentTimeMillis(),
                    contentIntent, pendingIntentFlags
            );
        }
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context,
                (int) System.currentTimeMillis(),
                deleteIntent, pendingIntentFlags
        );

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

    private Intent createDeeplinkIntent(Context context, PushPayloadParser pushPayloadParser, Uri uri) {
        String packageName = context.getApplicationInfo().packageName;
        Intent intent = new Intent(VibesEvent.ACTION_PUSH_OPENED)
                .addCategory(packageName)
                .setPackage(packageName);
        intent.setData(uri);
        return intent;
    }

    private Intent createPushOpenedIntent(Context context, PushPayloadParser pushPayloadParser, Class<? extends Activity> cls) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA, pushPayloadParser.getMap());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }



    /**
     * This is used to determine which activity best responds to an intent. The default implementation retrieves the launch activity class for the package.
     *
     * @param context The {@code Context} in which the receiver is running.
     * @param intent  An {@code Intent} containing the channel and data of the current push
     *                notification.
     * @return The default {@code Activity} class of the package or {@code null} if no launch intent
     * is defined in {@code AndroidManifest.xml}.
     */
    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        String packageName = context.getPackageName();
        List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(intent, 0);
        String className = null;
        if (!activities.isEmpty()) {
            ResolveInfo resolveInfo = activities.get(0);
            className = resolveInfo.activityInfo.name;
        } else {
            Vibes.getCurrentLogger().log(new LogObject(VibesLogger.Level.WARN, "No Activity found matching intent. Using default Activity"));
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent == null) {
                return null;
            }
            className = launchIntent.getComponent().getClassName();
        }

        Class<? extends Activity> cls = null;
        try {
            cls = (Class<? extends Activity>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            // do nothing
        }
        return cls;
    }
}