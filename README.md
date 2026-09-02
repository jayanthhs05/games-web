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

## Deploy everything

### 1. Push (one-time)

```bash
gh repo create games --public --source=. --remote=origin
git push origin --all          # main + all five game branches
```

### 2. Container games — `hangman`, `wordle`, `sudoku`, `25wol`

Same steps for each, once per game:

1. [render.com](https://render.com) → **New → Web Service** → connect this repo
2. **Branch**: `hangman` (then `wordle`, `sudoku`, `25wol`)
3. **Runtime**: Docker — it picks up the `Dockerfile` at the branch root
4. **Create** → you get `https://<name>.onrender.com`

The `Dockerfile` is the whole config. Free instances cold-start after idle.
Fly.io works the same way: `git switch hangman && fly launch --now`.

### 3. Alagulimane — `alagulimane`

Any one of:

- **GitHub Pages**: repo **Settings → Pages → Source: GitHub Actions**. The
  branch ships `.github/workflows/web.yml`, which builds the wasm bundle and
  publishes on every push to `alagulimane`. URL: `https://<you>.github.io/games/`.
- **Static host**: `./gradlew wasmJsBrowserDistribution` on the branch, then drop
  `build/dist/wasmJs/productionExecutable/` on Netlify / Cloudflare Pages.
- **Container host**: same as the games above — `Dockerfile` builds the bundle
  and serves it with nginx.

---

## Test locally

```bash
git switch hangman     && docker run --rm -p 7681:7681 $(docker build -q .)   # localhost:7681
git switch sudoku      && docker run --rm -p 8000:8000 $(docker build -q .)   # localhost:8000
git switch 25wol       && docker run --rm -p 3000:3000 $(docker build -q .)   # localhost:3000
git switch alagulimane && docker run --rm -p 8080:80   $(docker build -q .)   # localhost:8080
```

## Notes

- The branches are independent — edit and deploy each on its own branch. There is
  no shared code between games.
- History for every game goes back to the initial import; `main` was the original
  monorepo before it was split.
