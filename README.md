<div align="center">

<img src="fastlane/metadata/android/en-US/images/icon.png" width="160" height="160" align="center">

# Markhor

### Video and audio downloader for Android

</div>

> [!Note]
>
> **Markhor is a fork of [Seal](https://github.com/JunkFood02/Seal) by JunkFood02.**
> It is a modified version of the original app, renamed and maintained separately since November 2026.
> All credit for the original app goes to the Seal authors and contributors.

## 📖 Features

- Download videos and audio from any platform supported by [yt-dlp](https://github.com/yt-dlp/yt-dlp).
- Embed metadata and thumbnails into extracted audio files with [mutagen](https://github.com/quodlibet/mutagen).
- Download a whole playlist in one tap.
- Use the embedded [aria2c](https://github.com/aria2/aria2) as an external downloader.
- Embed subtitles into downloaded videos.
- Run custom yt-dlp commands from saved templates.
- Manage downloads and command templates in the app.
- [Material Design 3](https://m3.material.io/) UI with dynamic color themes.
- Written in Kotlin with Jetpack Compose: one activity, no fragments.

## 🛠️ Building

Open the project in Android Studio, or build from the command line:

```bash
./gradlew assembleGenericDebug
```

For most devices, install the **arm64-v8a** APK.

## 🚀 Releasing

Versions are never edited by hand. Run the **Release** workflow from the Actions tab and it:

1. Works out the next version from the last `v*` tag and the commit messages since then: `feat!:` or `BREAKING CHANGE` bumps major, `feat:` bumps minor, anything else bumps patch. You can also force patch, minor or major.
2. Builds signed APKs with that version and publishes them as a GitHub release, which the in-app updater picks up.
3. Adds the release to [CHANGELOG.md](CHANGELOG.md) with [git-cliff](https://git-cliff.org): `feat` commits go under **Added**, `fix` under **Fixed**, `perf`/`refactor`/`style` under **Changed**, and `docs`/`chore`/`ci`/`test` are left out. Anything you type in the optional **notes** box (a summary, known issues) appears above the list. The release page gets the same text.

Pick the **beta** channel to publish a pre-release (for example `v1.1.0-beta.1`) that only reaches users on the preview update channel. The workflow needs the `SIGNING_KEY` (base64 keystore), `ALIAS`, `KEY_STORE_PASSWORD` and `KEY_PASSWORD` repository secrets.

## 💖 Support

[![Support me on Patreon](https://img.shields.io/badge/Patreon-Support%20Markhor-F96854?style=for-the-badge&logo=patreon&logoColor=white)](https://patreon.com/hammadxp)

Markhor is free and open source. If you find it useful, you can support its development on [Patreon](https://patreon.com/hammadxp). Supporters are listed below and in the app under **Settings > About > Sponsor**.

<!-- sponsors:start -->
No supporters yet. [Be the first!](https://patreon.com/hammadxp)
<!-- sponsors:end -->

## 🧱 Credits

Markhor is based on [Seal](https://github.com/JunkFood02/Seal), a GUI for [yt-dlp](https://github.com/yt-dlp/yt-dlp) built on [youtubedl-android](https://github.com/yausername/youtubedl-android).

Seal itself credits:

- [Read You](https://github.com/Ashinch/ReadYou) and [Music You](https://github.com/Kyant0/MusicYou) for parts of the UI design and code
- [dvd](https://github.com/yausername/dvd)
- [Material color utilities](https://github.com/material-foundation/material-color-utilities)
- [Monet](https://github.com/Kyant0/Monet)

The full list of libraries and their licenses is in the app under **Settings > About > Credits**.

## 📃 License

Markhor is licensed under the [GNU General Public License v3.0](LICENSE), the same license as Seal.

Some files keep their original Apache License 2.0 headers (Material color utilities and the motion helpers), as that license requires.
