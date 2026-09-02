# games

Five small games, each on its own branch, each deployable to the web on its own.

**`main` is just this index.** Every game's code lives on its own branch, with
that game at the repository root plus its deploy files — so a hosting platform
can be pointed straight at the branch with no extra configuration.

```bash
git switch hangman     # work on / deploy that game
git switch main        # back to this index
```

| Game | Branch | Stack | Runs in the browser as | Deploy with |
|------|--------|-------|------------------------|-------------|
| Hangman | [`hangman`](../../tree/hangman) | Python CLI | a live terminal (ttyd + xterm.js) | Docker → any container host |
| Wordle | [`wordle`](../../tree/wordle) | Python CLI | a live terminal (ttyd + xterm.js) | Docker → any container host |
| Sudoku | [`sudoku`](../../tree/sudoku) | Flask | a web page | Docker or Procfile → Render / Railway / Heroku |
| 25 Words or Less | [`25wol`](../../tree/25wol) | Node + Socket.IO | a real-time web page | Docker or Procfile → Render / Railway / Heroku |
| Alagulimane | [`alagulimane`](../../tree/alagulimane) | Kotlin / Compose Multiplatform | a WebAssembly canvas app | static site, or Docker (nginx), or GitHub Pages |

Each branch has its own `README.md` with the exact build/run/deploy steps.

---

## Deploy — the easy path

**Two moves total.** The four server games go up together via one Render
Blueprint; Alagulimane is a static site, so it goes on GitHub Pages.

### 1. The four servers → Render Blueprint (`render.yaml`)

[render.com](https://render.com) → **New → Blueprint** → pick this repo → **Apply**.

`render.yaml` (on `main`) defines all four as Docker web services, each building
the `Dockerfile` at the root of its own branch. You get four
`https://<name>.onrender.com` URLs. Free instances sleep after ~15 min idle and
cold-start (~30 s) on the next hit — fine for demos.

### 2. Alagulimane → GitHub Pages

Repo **Settings → Pages → Source: GitHub Actions**. The `alagulimane` branch
ships `.github/workflows/web.yml`, which builds the wasm bundle and publishes it
on every push to that branch. URL: `https://<you>.github.io/<repo>/`.

> Pages is static-only, so it can host **only** Alagulimane — the other four are
> live servers (a terminal, Flask, Node+WebSocket) and need a container host.

### Other hosts

Every branch's `Dockerfile` listens on `$PORT`, so any container platform works
with zero extra config: `git switch hangman && fly launch --now`, Cloud Run,
Railway (Docker or the committed `Procfile`), etc.

---

## Test locally

```bash
git switch hangman     && docker run --rm -p 7681:7681 $(docker build -q .)   # localhost:7681
git switch sudoku      && docker run --rm -p 8000:8000 $(docker build -q .)   # localhost:8000
git switch 25wol       && docker run --rm -p 3000:3000 $(docker build -q .)   # localhost:3000
git switch alagulimane && docker run --rm -p 8080:8080 $(docker build -q .)   # localhost:8080
```

## Notes

- The branches are independent — edit and deploy each on its own branch. There is
  no shared code between games.
- History for every game goes back to the initial import; `main` was the original
  monorepo before it was split.
