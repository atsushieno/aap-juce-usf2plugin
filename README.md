# aap-juce-usf2plugin

This project ports [USF2Plugin](https://github.com/atsushieno/usf2plugin) to [Audio Plugins for Android](https://github.com/atsushieno/aap-core) using [aap-juce](https://github.com/atsushieno/aap-juce).

The initial Android build is a self-contained General MIDI instrument using the bundled GeneralUser GS SoundFont. Android external-folder selection is disabled
because the upstream filesystem-directory workflow is not compatible with scoped storage. App-private SoundFont import is planned as a follow-up.

## Build

Initialize the submodules, then run:

```sh
make
```

The Makefile publishes the local AAP dependencies, applies `aap-juce-support.patch` to the pinned plugin source, and builds the Android APK and app bundle. Direct `./gradlew` builds expect the upstream patch to have already been applied.

See [PORTING_PLAN.md](PORTING_PLAN.md) for the investigation, known constraints, and device verification checklist.

## License

USF2Plugin and this port are distributed under the AGPL v3 license.
