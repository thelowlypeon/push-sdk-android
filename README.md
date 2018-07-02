# Vibes SDK - Android

An Android SDK for handling push integration with the [Vibes API][1].

## Contributing

### Installation

Install [Android Studio][2] and [Ruby][7]. Once you open Android Studio, it
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

Code style is checked via [Android lint][3], [Checkstyle][4], [FindBugs][5], and [pmd][6]. All of these can be run with `./bin/check`

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

[1]: https://developer.vibes.com/display/APIs/Catapult+APIs
[2]: https://developer.android.com/studio
[3]: https://developer.android.com/studio/write/lint.html
[4]: http://checkstyle.sourceforge.net/
[5]: http://findbugs.sourceforge.net/
[6]: https://pmd.github.io/
[7]: https://www.ruby-lang.org/en/
