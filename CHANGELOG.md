Version 1.0.22
=============
- Removing VibesConfiguration ENUM as use of it is deprecated.

Version 1.0.21
=============
Other Changes
-------------
- After some investigation with Rich, collapse_key is automatically parse by Android and is accessible from RemoteMessage.getCollapseKey(). It's therefore not needed anymore in the SDK -> Remove it from the PushPayloadParser

Version 1.0.20
=============
New Features
---------
- Add collapse_key parsing method in the PushPayloadParser

Bug Fixes
---------
- Add missing `workerThread.onSuccess(completion, value);` in Vibes.uploadStoredEvent

Other Changes
-------------
- Fix unit tests

Version 1.0.19
=============
Bug Fixes
---------
- Update priority/importance mapping to only keep 'default' and 'high'

Version 1.0.18
=============
New Features
---------
- Add silent_push parsing method in the PushPayloadParser

Version 1.0.17
=============
New Features
---------
- Add custom data parsing method in the PushPayloadParser

Version 1.0.16
=============
New Features
---------
- Add sound parsing method in the PushPayloadParser

Version 1.0.15
=============
New Features
---------
- Add image_url parsing method in the PushPayloadParser

Other Changes
-------------
- PushPayloadParser refactoring

Version 1.0.14
=============
New Features
---------
- Add a random delay of 15s max, for every attempt (after timeout) of the workerThread.
 
Version 1.0.13
=============
Bug Fixes
---------
- Fix: Update the Vibes.updateDevice to only update the local credentials if the credentials returns by updateDevice aren't nil. If it's called for updating the device location, credentials pushed in the callback will be nil and in that case we don't want to overwrite the local credential.

Version 1.0.12
=============
Bug Fixes
---------
- Fix: Update the APIDefinition.updateDevice to use an empty parser in case it's called to update the device location. The backend returns, for that case an empty body.

Version 1.0.11
=============
Bug Fixes
---------
- Fix: Check in the retryRequest method if the credential are nil (for registerDevice for instance) and call the appropriate method of the workerThread (with or without the parameter credential)

Version 1.0.10
=============
New Features
---------
- Update VibesConfiguration.EUROPE with the correct value.
- Add a callback for the method to update the device location
- Add a retry mechanism for every requests in case an HTTP Response code 429, 408, 500, 502, 504 is caught.
- Add a new method in the PushPayloadParser to retrieve the notification channel.

Version 1.0.9
=============
New Features
---------
- Add a new method in Vibes to update Vibes app url (for UK). There is a bug
in the demo app preventing from using the AndroidManifest variable injection.
  
Version 1.0.8
=============
New Features
---------
- Use the backend new endpoint (updateDevice) to update the device location.
  (PATCH is used to update the location, PUT is used to get a new token)

Version 1.0.7
=============
Bug Fixes
---------
- Fix Priority maping between Oreo and previous Android version.

Version 1.0.6
=============
New Features
---------
- Add Notification channel, priority and collapse_key

Bug Fixes
---------
- Fix badging number parsing

Version 1.0.5
=============

New Features
---------
- Parse the badge number in the push notification payload

Version 1.0.4
=============

Bug Fixes
---------
- Update correctly the SDK version in the Device info

Version 1.0.3
=============

New Features
---------
- Register device location (lat/long)

Version 1.0.2
=============

Bug Fixes
---------
- Fix event launch race condition

Version 1.0.1
=============

New Features
------------
- Detect when the application is started from clicking a simple push
- Generate a clickthru event
- Add a push payload parser

Other Changes
-------------
- Fix various findbugs warnings
- Fix style
- Remove embedded app


Version 1.0.0
=============

New Features
------------
- register/unregister Device
- registerPush/unregisterPush
- event Launch
- Simple push notification