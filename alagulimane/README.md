# Alagulimane (ಅಳಗುಳಿಮನೆ)

A traditional South Indian Mancala-style board game, built with Kotlin and
**Compose Multiplatform**. Runs in the browser as a WebAssembly app; the same
`commonMain` UI and game logic can also target Android.

![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple)
![Compose Multiplatform](https://img.shields.io/badge/UI-Compose%20Multiplatform-blue)
![Target](https://img.shields.io/badge/Target-wasmJs-orange)

## Play on the web

```bash
./gradlew wasmJsBrowserRun -t        # dev server with hot reload → http://localhost:8080
```

Build the static site:

```bash
./gradlew wasmJsBrowserDistribution
# output: build/dist/wasmJs/productionExecutable/
#   index.html, alagulimane.js, *.wasm, skiko.wasm, ...
```

That folder is a self-contained static site — no server code. Upload it to any
static host (Netlify, GitHub Pages, Cloudflare Pages, S3, ...).

### Deploy with Docker (nginx)

```bash
docker build -t alagulimane-web .
docker run --rm -p 8080:80 alagulimane-web     # http://localhost:8080
```

The image is multi-stage: it builds the wasm bundle with JDK 17, then serves the
static output with nginx (correct `application/wasm` MIME type + gzip).

### Deploy to GitHub Pages

Pushing the `alagulimane` branch runs `.github/workflows/web.yml`, which builds
`wasmJsBrowserDistribution` and publishes it to GitHub Pages.

## Requirements

- **JDK 17–24** (Gradle 8.14 does not run on JDK 25+). If your default `java` is
  newer, set `org.gradle.java.home` in `gradle.properties` or export `JAVA_HOME`.
- First build downloads the Kotlin/Wasm toolchain, Node, and Compose artifacts
  (~600 MB) and takes a few minutes; later builds are cached.

## Browser notes

- The bundle is a Skia canvas (~5–8 MB gzipped on first load) — Compose draws the
  whole UI itself, so it looks identical to the Android version.
- Needs a current browser with WebAssembly GC (Chrome/Edge 119+, Firefox 120+,
  Safari 18.2+).
- The Kannada title renders if the browser has a Kannada font; otherwise it falls
  back to tofu boxes (cosmetic only).

## Project structure

```
src/
├── commonMain/kotlin/com/alagulimane/
│   ├── App.kt                 # root composable + screen navigation
│   ├── model/GameState.kt     # board model, sowing/opposite-hole helpers
│   ├── viewmodel/GameViewModel.kt   # game logic + sowing animation (owns a CoroutineScope)
│   └── ui/
│       ├── theme/             # colors, typography, MaterialTheme wrapper
│       ├── components/        # Seed, GameHole, GameBoard
│       └── screens/           # TitleScreen, GameScreen, HowToPlayScreen
└── wasmJsMain/
    ├── kotlin/com/alagulimane/main.kt   # ComposeViewport entry point
    └── resources/index.html
```

## Game rules

### Setup
- Board: two rows of seven holes (14 total), five seeds per hole (70 total)
- Each player owns one row

### Gameplay
1. **Sowing**: pick seeds from any of your holes, drop one per hole counter-clockwise
2. **Continuation**: when your hand empties, pick up the next hole's seeds and continue
3. **Karu**: a hole reaching exactly 4 seeds is captured immediately by its owner
4. **Turn end**: reaching an empty hole ends your turn
5. **Capture (wipe)**: if the hole after the empty one has seeds, capture those plus the opposite hole
6. **Double empty**: two empty holes in a row — no capture, turn just ends

### Winning
- After each round, refill holes with 5 seeds each from captured seeds
- Holes that can't be filled become "pauper" holes (inactive)
- The game ends when a player can't fill any holes

## License

MIT License - Feel free to use and modify!
