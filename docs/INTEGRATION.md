# Installing and Integrating the Vibes Push Notifications SDK into an Android App

##Table of contents:
1. [Install SDK](#install)
  - [Using GCS](#gcs)
  - [Code Example One](#code-example-one)
  - [Code Example Two](#code-example-two)
2. [Integration](#integration)
  - [Configuration](#configuration)
  - [Register a Device](#register-device)
  - [Unregister a Device](#unregister-device)
  - [Register for Push](#register-push)
  - [Unregister for Push](#unregister-push)
3. [Tracking Events](#tracking-events)
4. [Deep Linking](#deep-linking)
  - [Payload](#payload)
  - [Start Deep Link Activity](#start-activity)
  - [Testing](#testing)

The following information will show you how to install and integrate the Vibes
Push Notifications SDK into an Android App.

Note: This document specifies Firebase Cloud messaging (FCM) but 
the older platform [Google Cloud Messaging][3] is also supported.
    
## Installing the Vibes Push Notifications Android SDK <a name="install"></a>

You can install the Vibes Push Notifications Android SDK with the following steps.

1. Click [Add Firebase][1] (Note: This will take you away from the wiki) and
   follow the instructions on the Firebase website. This will include setting up
   the Google Services plugin and downloading the `google-services.json` into
   your `app` folder.
2. Click [Add Firebase Cloud Messaging][2] (Note: This will take you away from
   the wiki) and follow the instructions. This will include adding two services
   to your App to handle App token refresh and incoming push notifications.
   Note: If you are not using Firebase Cloud Messaging, follow the instructions
   below for using an older version of Google Services.
3. Add the Vibes SDK by doing the following:

   a. Add the following to your App-level `build.gradle` file:

```groovy
repositories {
    maven {
        url "https://raw.githubusercontent.com/vibes/android-sdk-repository/releases/"
    }
}
dependencies {
   // other dependencies here
   compile "com.vibes.vibes:vibes:2.+"
}
```

   b. Sync your project in Android Studio.

## Using an Older Version of Google Services <a name="gcs"></a>

If you are using an older version of Google Services (the latest version uses Firebase) to handle push notifications, you need to do the following.

1. Verify that the dependency Play-Services version is a minimum of 9.0.0. You can check that in your application build gradle configuration
CustomerApplication/app/build.gradle.

```
dependencies {
    ...
    compile 'com.vibes.vibes:vibes:2.+'
    compile "com.google.android.gms:play-services:9.0.0"
}

apply plugin: 'com.google.gms.google-services'
```

2. Verify that the dependency gradle version is minimum 2.3.3 and the Google-Services is version 3.0.0. You can check that in the CustomerApplication/build.gradle.

```
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.3.3'
        classpath 'com.google.gms:google-services:3.0.0'
        classpath 'com.android.tools.build:gradle:2.0.0-alpha6'
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
```

3. Verify that your application doesn’t use the deprecated class GooglePlayServicesUtil. The com.google.android.gms.common.GooglePlayServicesUtil has been deprecated and replaced by import com.google.android.gms.common.GoogleApiAvailability.
If you are using it, replace the code in Code Example One below by the code in Code Example Two in your application activity (java class) where you check Google Play Services availability.

## Code Example One (CustomerApplication/[ACTIVITY_CLASS_NAME].java) <a name="code-example-one"></a>
```
import com.google.android.gms.common.GooglePlayServicesUtil; // GooglePlayServicesUtil has been deprecated

...

int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
if(ConnectionResult.SUCCESS != resultCode) {
    //Check type of error
    if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
       Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled on this device!", Toast.LENGTH_LONG).show();
        //So notification
        GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
    } else {
       Toast.makeText(getApplicationContext(), "This device does not have support for Google Play Service!", Toast.LENGTH_LONG).show();
    }
} else {
    //Start service
    Intent itent = new Intent(this, GCMRegistrationIntentService.class);
    startService(itent);
}
```

## Code Example Two <a name="code-example-two"></a>
```
import com.google.android.gms.common.GoogleApiAvailability;

...

GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
int resultCode = googleAPI.isGooglePlayServicesAvailable(getApplicationContext());
if (ConnectionResult.SUCCESS != resultCode) {
    //Check type of error
    if(googleAPI.isUserResolvableError(resultCode)) { Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
        //So notification
        googleAPI.showErrorNotification(getApplicationContext(), resultCode);
    } else {
       Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
    }
} else {
    //Start service
    Intent itent = new Intent(this, GCMRegistrationIntentService.class);
    startService(itent);
}
```

4. If you had a deprecated version of Google Services and have followed the previous steps, the last step is to regenerate a google-services.json, then add/replace it into your App folder on https://console.firebase.google.com/.


## Integrating the Vibes Push Notifications SDK into an Android App. <a name="integration"></a>


Note:  This document specifies Firebase Cloud messaging (FCM) but the older platform Google Cloud messaging (GCM) is also supported: https://developers.google.com/cloud-messaging/faq.


### Configuration <a name="configuration"></a>

Set the Vibes App ID in your `AndroidManifest.xml`, inside your `<application>` block:

```xml
<meta-data android:name="vibes_app_id" android:value="TEST_APP_ID" />
```

### Registering a Device <a name="register-device"></a>

You can add the following code wherever it is most appropriate for your application. You
may find that an `onCreate` for your main `Activity` or custom
`Application`-subclass is the best place.

```java
// NOTE: the `VibesListener` is optional; you can ignore it if you don't care
// about the result of the registration.
Vibes.getInstance().registerDevice(new VibesListener<Credential>() {
    @Override
    public void onSuccess(Credential value) {
    }

    @Override
    public void onFailure(String errorText) {
    }
});
```

### Unregistering a Device <a name="unregister-device"></a>

Put the following code wherever it makes the most sense for your application:

```java
// NOTE: the `VibesListener` is optional; you can ignore it if you don't care
// about the result of the unregistration.
Vibes.getInstance().unregisterDevice(new VibesListener<Void>() {
    @Override
    public void onSuccess(Void value) {
    }

    @Override
    public void onFailure(String errorText) {
    }
});
```

### Registering for Push <a name="register-push"></a>

Use the following code to register for push.

```java
// NOTE: the `VibesListener` is optional; you can ignore it if you don't care
// about the result of the unregistration.
String pushToken = FirebaseInstanceId.getInstance().getToken();
Vibes.getInstance().registerPush(pushToken, new VibesListener<Void>() {
    @Override
    public void onSuccess(Void value) {
    }

    @Override
    public void onFailure(String errorText) {
    }
});
```

### Unregistering for Push <a name="unregister-push"></a>

Use the following code to unregister for push.

```java
// NOTE: the `VibesListener` is optional; you can ignore it if you don't care
// about the result of the unregistration.
Vibes.getInstance().unregisterPush(new VibesListener<Void>() {
    @Override
    public void onSuccess(Void value) {
    }

    @Override
    public void onFailure(String errorText) {
    }
});
```

## Tracking Events <a name="tracking-events"></a>

The events are automatically triggered by the SDK. If a device is not
registered, the events will be stored locally and the next time the device is
registered, the events will be sent to the Vibes backend. Nothing needs to be
configured.

## Deep Linking <a name="deep-linking"></a>

### Payload <a name="payload"></a>

The following is the payload that is sent as part of the notification.

```json
{
  "to": "optional device token",
  "data": {  
      "message_uid": "011ef11b-1d01-de1e-a111-11cfa1d1111a",
      "body": "body content",
      "title":"title content"
      "client_app_data": {
        ...
          "deep_link": "XXXXX",
        ...
      }
   }
}
```

Note: The `deep_link` key will be hardcoded in Campaign Manager and only the
value will be modifiable.

### Start Deep Link Activity <a name="start-activity"></a>

You can pass in a key value pair as part of the `client_app_data` and parse the
deep link information. Based on this information you can decide which activity
you want to start.

The following is one way to do this:

```java
public class FMS extends FirebaseMessagingService {
    private final String kTitle = "title";
    private final String kBody = "body";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Map<String, String> content = message.getData();

        // code to parse client data and decide which activity you want to start
        // change DeeplinkActivity.class with desired class in the Intent below

        Intent intent = new Intent(this, DeeplinkActivity.class);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(content.get(kTitle))
                .setSmallIcon(R.drawable.firebase_icon)
                .setContentText(content.get(kBody))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        Integer uniqueId = ((Long)System.currentTimeMillis()).intValue();
        mNotificationManager.notify(uniqueId, notification);
    }
}
```

### Testing <a name="testing"></a>

If you would like to test that you receive a notification in your application,
we recommend creating a [firebase][1] project and using a tool like [postman][4] to
trigger a request. You can use the payload above with your desired
``client_app_data`` content.

[1]: https://firebase.google.com/docs/android/setup
[2]: https://firebase.google.com/docs/cloud-messaging/android/client
[3]: https://developers.google.com/cloud-messaging/faq
[4]: https://www.getpostman.com/
