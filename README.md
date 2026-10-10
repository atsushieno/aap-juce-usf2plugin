# aap-juce-usf2plugin

This project ports [USF2Plugin](https://github.com/atsushieno/usf2plugin) to [Audio Plugins for Android](https://github.com/atsushieno/aap-core) using [aap-juce](https://github.com/atsushieno/aap-juce).

The Android build is a self-contained General MIDI instrument using the bundled
GeneralUser GS SoundFont. Additional SoundFont folders can be selected through
Android's system folder picker; access is retained through a persistent Storage
Access Framework permission, and `.sf2` files in nested folders are scanned
directly without copying them into app-private storage.

## Build

Initialize the submodules, then run:

```sh
./gradlew build
```

Gradle prepares and patches the application and JUCE sources before the Android build. Use `gradlew.bat build` on Windows, and `./gradlew bundleRelease` to produce the release app bundle.

See [PORTING_PLAN.md](PORTING_PLAN.md) for the investigation, known constraints, and device verification checklist.

## License

USF2Plugin and this port are distributed under the AGPL v3 license.
