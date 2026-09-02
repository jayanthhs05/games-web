# 25 Words or Less (real-time web game)

Two-player co-op word game. One player gives one-word clues, the other guesses,
against a shared timer and clue budget. Node + Express + Socket.IO. Full rules in
[`rules.md`](rules.md).

## Run locally

```bash
cd 25wol
npm install
npm start                 # http://localhost:3000
```

`PORT` overrides the port. Open the URL in two tabs / devices, create a game in
one and join with the 6-character Game ID in the other.

## Run with Docker

```bash
cd 25wol
docker build -t 25wol .
docker run --rm -p 3000:3000 25wol
```

## Deploy to a PaaS

`Procfile` (`web: node server.js`) works on Heroku / Railway / Render (Node
buildpack). Point the service at the `25wol` branch (app at repo root) or set the
root directory to `25wol/`.

- Needs WebSocket support (all of the above provide it).
- Game state lives in memory in a single process — run **one** instance / no
  autoscaling, or move state to Redis for multiple instances.
