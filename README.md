# Vibes SDK - Android

## Installation and Usage

For installation and integration instructions, see the [Android SDK documentation][android-docs].

## Contributing

### Installation

Install [Android Studio][android-studio] and [Ruby][ruby]. Once you open Android Studio, it
should handle downloading and syncing any of the Android SDK components it
needs.

When you're ready, run:

```
./bin/setup
```

### Tests

#### Unit
You can run the unit tests from within Android Studio, or via the command line
with `./bin/test`. Test results live in
`vibes/build/reports/tests/testDebugUnitTest/`.

#### Integration
You can run the integration tests from within Android Studio, or via the
command line with `./bin/integration-test`. Test results live in
`vibes/build/reports/androidTests/connected`

> NOTE: The integration tests are "connected" tests; you must have an open
> emulator or connected device to run them.

### Documentation

To generate the public docs, run `./bin/docs-public`; for internal docs, use: `./bin/docs-internal`.

### Check

Code style is checked via [Android lint][android-lint], [Checkstyle][checkstyle], [FindBugs][findbugs], and [pmd][pmd]. All of these can be run with `./bin/check`

### Releases

#### Setup

Configure your root- or system-level `gradle.properties` file with your github
username and either a password or a personal access token (see the sample file
for format).

#### To release

Bump the `ARTIFACT_VERSION` in the `vibes/gradle.properties` file. Then run:

```
./bin/release
```

### SDK Example App
See `example-java/` for the `java` example app or `example-kotlin/` for the `kotlin` example app using the Vibes Android SDK. They implement device registration/unregistration and push registration/unregistration of the Vibes SDK.

#### Setup

1. In your Android Studio, open the `/example-java/` for the `java` app or `/example-kotlin` for the `kotlin` app ie **FILE** > **Open** > `example-java`/`example-kotlin`
1. In the `AndroidManifest.xml` located in the `example-java`/`example-kotlin` module, replace `'[YOUR KEY GOES HERE]'` with your vibes_app_id.
1. Create a [Firebase](https://firebase.google.com/) project using the package name.
1. Build and run the example-java app.

>Note The main purpose of this example app is to show a standard integration with the Vibes Android SDK. Sending push notification is outside the scope of this example app, but you have the option to configure your own Firebase project and send out notifications.



[android-docs]: https://developer.vibes.com/display/APIs/Android+SDK+Documentation
[android-studio]: https://developer.android.com/studio
[android-lint]: https://developer.android.com/studio/write/lint.html
[checkstyle]: http://checkstyle.sourceforge.net/
[findbugs]: http://findbugs.sourceforge.net/
[pmd]: https://pmd.github.io/
[ruby]: https://www.ruby-lang.org/en/
