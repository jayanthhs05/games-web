# Wordle (CLI)

Terminal Wordle. Six guesses to find a 5- or 6-letter word, with the usual
green / yellow / grey feedback (rendered with `colorama`).

## Run locally

```bash
cd wordle
python3 -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
python3 wordle.py
```

Run it from inside this directory so it can find `words.txt`.

## Play in a browser (web terminal)

The `Dockerfile` serves the real terminal over the web with
[`ttyd`](https://github.com/tsl0922/ttyd) + xterm.js. The game code is unchanged;
`play.sh` just restarts it after each session.

```bash
cd wordle
docker build -t wordle-web .
docker run --rm -p 7681:7681 wordle-web
# open http://localhost:7681
```

### Deploy it online

Any host that runs a container and gives you `$PORT` works — Render, Railway,
Fly.io. Create a "Docker" / "Web Service", point it at the `wordle` branch,
no build or start command needed.

- Each browser tab gets its own isolated process — players don't collide.
- `ttyd` only ever runs `./play.sh` (never a shell); the container runs as a
  non-root user. `-m 25` caps concurrent players.
- To gate access, add `-c user:pass` to the `ttyd` line in the `Dockerfile`, or
  use the platform's auth.
- Building on an ARM machine: `docker build --build-arg TTYD_ARCH=aarch64 ...`
  (deploy builders are almost always x86_64).
