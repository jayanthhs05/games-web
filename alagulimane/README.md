# Alagulimane (ಅಳಗುಳಿಮನೆ)

A traditional South Indian Mancala-style board game for Android, built with Kotlin and Jetpack Compose.

![Game](https://img.shields.io/badge/Platform-Android-green)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue)

## Features

- 🎮 **Classic Gameplay**: Authentic Alagulimane rules with sowing, Karu (4-seed capture), and wipe mechanics
- 🪵 **Beautiful Design**: Wood-textured board with tamarind-like seeds
- 👥 **Two Player**: Local two-player gameplay
- 📊 **Round System**: Multiple rounds with pauper hole mechanics
- 🏆 **Win Detection**: Automatic game end when a player becomes a total pauper

## Game Rules

### Setup
- Board: Two rows of seven holes (14 total)
- Seeds: 5 seeds per hole (70 total)
- Each player owns one row

### Gameplay
1. **Sowing**: Pick seeds from any of your holes, drop one per hole in your direction
2. **Continuation**: When hand is empty, pick from next hole and continue
3. **Karu**: If any hole reaches exactly 4 seeds, the owner captures them immediately
4. **Turn End**: Reaching an empty hole ends your turn
5. **Capture (Wipe)**: If next hole after empty has seeds, capture those plus opposite hole
6. **Double Empty**: If next two holes are empty, no capture - turn just ends

### Winning
- After each round, refill holes with 5 seeds each from captured seeds
- Holes that can't be filled become "pauper" holes (inactive)
- Game ends when a player can't fill any holes

## Building the App

### Prerequisites
- Android Studio Arctic Fox (2021.3.1) or later
- JDK 17
- Android SDK with API level 34

### Steps
1. Open the project in Android Studio
2. Sync Gradle files
3. Run on device or emulator

Or build from command line (needs the Android SDK; set `ANDROID_HOME` or add a
`local.properties` with `sdk.dir=...`):
```bash
./gradlew assembleDebug           # app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug            # install onto a connected device / emulator
./gradlew assembleRelease         # unsigned release APK
```

### CI build

Pushing the `alagulimane` branch runs `.github/workflows/android.yml`, which
builds the debug APK on GitHub's Ubuntu runners and uploads it as a build
artifact — no local Android SDK needed. Add a signing config + secrets to turn
that into a Play Store `bundleRelease`.

## Project Structure

```
app/src/main/java/com/alagulimane/
├── MainActivity.kt           # Entry point
├── model/
│   └── GameState.kt         # Game data models
├── viewmodel/
│   └── GameViewModel.kt     # Game logic
└── ui/
    ├── theme/               # Colors, typography, theme
    ├── components/          # Seed, Hole, GameBoard
    └── screens/             # GameScreen
```

## License

MIT License - Feel free to use and modify!
