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

[android-docs]: https://developer.vibes.com/display/APIs/Android+SDK+Documentation
[android-studio]: https://developer.android.com/studio
[android-lint]: https://developer.android.com/studio/write/lint.html
[checkstyle]: http://checkstyle.sourceforge.net/
[findbugs]: http://findbugs.sourceforge.net/
[pmd]: https://pmd.github.io/
[ruby]: https://www.ruby-lang.org/en/
