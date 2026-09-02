# games

Five small games in one repository. `main` holds everything, one folder per game.
**Each game also has its own branch** where that game sits at the repository root
with its own deploy files — point a hosting platform straight at the branch.

| Game | Folder | Branch | Type | Deploy with |
|------|--------|--------|------|-------------|
| [Hangman](hangman/) | `hangman/` | `hangman` | Python CLI | web terminal (ttyd) — Docker (Render, Railway, Fly) |
| [Wordle](wordle/) | `wordle/` | `wordle` | Python CLI | web terminal (ttyd) — Docker (Render, Railway, Fly) |
| [Sudoku](sudoku/) | `sudoku/` | `sudoku` | Flask web app | Docker / Procfile (Render, Railway, Heroku) |
| [25 Words or Less](25wol/) | `25wol/` | `25wol` | Node + Socket.IO web app | Docker / Procfile (Render, Railway, Heroku) |
| [Alagulimane](alagulimane/) | `alagulimane/` | `alagulimane` | Android (Kotlin / Compose) | `./gradlew assembleDebug` / GitHub Actions |

Every folder has its own `README.md` with exact run and deploy steps.

## Layout

```
games/
├── hangman/       hangman.py, words.txt, play.sh, Dockerfile (ttyd web terminal)
├── wordle/        wordle.py, words.txt, requirements.txt, play.sh, Dockerfile (ttyd web terminal)
├── sudoku/        app.py, templates/, requirements.txt, Procfile, Dockerfile
├── 25wol/         server.js, public/, package.json, Procfile, Dockerfile
└── alagulimane/   Gradle Android project (Kotlin + Jetpack Compose)
```

## Working with the branches

```bash
git switch sudoku          # that game, at the repo root, ready to deploy
git switch main            # back to the full monorepo
```

The game branches are derived from `main`. When you change a game on `main`,
re-sync its branch:

```bash
./scripts/sync-branches.sh   # rebuilds every game branch from main
```
