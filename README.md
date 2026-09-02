# Hangman (CLI)

Terminal hangman. Guess the word letter by letter across 1–5 rounds and three
difficulty levels. Pure Python standard library — no dependencies.

## Run locally

```bash
cd hangman
python3 hangman.py
```

Run it from inside this directory so it can find `words.txt`.

## Play in a browser (web terminal)

The `Dockerfile` serves the real terminal over the web with
[`ttyd`](https://github.com/tsl0922/ttyd) + xterm.js. The game code is unchanged;
`play.sh` just restarts it after each session.

```bash
cd hangman
docker build -t hangman-web .
docker run --rm -p 7681:7681 hangman-web
# open http://localhost:7681
```

### Deploy it online

Any host that runs a container and gives you `$PORT` works — Render, Railway,
Fly.io. Create a "Docker" / "Web Service", point it at the `hangman` branch,
no build or start command needed.

- Each browser tab gets its own isolated process — players don't collide.
- `ttyd` only ever runs `./play.sh` (never a shell); the container runs as a
  non-root user. `-m 25` caps concurrent players.
- To gate access, add `-c user:pass` to the `ttyd` line in the `Dockerfile`, or
  use the platform's auth.
- Building on an ARM machine: `docker build --build-arg TTYD_ARCH=aarch64 ...`
  (deploy builders are almost always x86_64).
