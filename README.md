# aap-juce-usf2plugin

This project ports [USF2Plugin](https://github.com/atsushieno/usf2plugin) to [Audio Plugins for Android](https://github.com/atsushieno/aap-core) using [aap-juce](https://github.com/atsushieno/aap-juce).

The Android build is a self-contained General MIDI instrument using the bundled
GeneralUser GS SoundFont. More SoundFonts come through the soundpack libraries
([soundpack-provider-for-android](https://github.com/atsushieno/soundpack-provider-for-android)),
from these sources:

- **SF2 folders**: folders you put on the device yourself and link in the plugin UI's
  directory settings, through Android's system folder picker. Access is retained through a
  persistent Storage Access Framework permission. Folders linked by earlier versions
  (`sf2Folders`) are carried over on first start. On Android 14 and later, Android blocks
  the plugin UI from opening the folder picker while another app (a DAW) shows it.
- **SF2 packs** published by other installed apps, and folders shared in the Soundpack
  Manager (`org.androidaudioplugin.soundpack.manager`); they appear with no further step.

Linking a folder indexes the `.sf2` files in it and its subfolders, with their presets, so
listing SoundFonts opens no SF2 file. Loading one maps the file instead of copying it into
app-private storage. The selection is saved in the plugin state as a `commons-soundpack://`
identity; selections saved as folder document URLs by earlier versions are resolved through
the carried-over folders.

## Build

Initialize the submodules, then build the aap-core and soundpack-provider-for-android
submodules into Maven Local, and build this app:

```sh
./gradlew -p external/aap-core publishToMavenLocal
(cd external/soundpack-provider-for-android && ./gradlew publishToMavenLocal)
./gradlew build
```

Gradle prepares and patches the application and JUCE sources before the Android build. Use `gradlew.bat build` on Windows, and `./gradlew bundleRelease` to produce the release app bundle.

See [PORTING_PLAN.md](PORTING_PLAN.md) for the investigation, known constraints, and device verification checklist.

## License

USF2Plugin and this port are distributed under the AGPL v3 license.
