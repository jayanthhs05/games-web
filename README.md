# Sudoku (Flask web app)

Generates a Sudoku puzzle, lets you fill the blanks in the browser, and checks
your solution on submit.

## Run locally

```bash
cd sudoku
python3 -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
python3 app.py            # dev server on http://localhost:5000
```

Set `FLASK_DEBUG=1` for auto-reload. `PORT` overrides the port.

## Run with gunicorn (production)

```bash
gunicorn app:app --bind 0.0.0.0:8000
```

## Run with Docker

```bash
cd sudoku
docker build -t sudoku .
docker run --rm -p 8000:8000 sudoku      # http://localhost:8000
```

## Deploy to a PaaS

The `Procfile` works on Heroku / Railway / Render (Python buildpack):

```
web: gunicorn app:app --bind 0.0.0.0:$PORT
```

- **Render / Railway:** point at the `sudoku` branch (app at repo root) or set the
  root directory to `sudoku/`. Build: `pip install -r requirements.txt`.
  Start: `gunicorn app:app --bind 0.0.0.0:$PORT`.
- Puzzle state is kept in `app.config` (single worker). For multiple workers,
  move state to a session or a store.
