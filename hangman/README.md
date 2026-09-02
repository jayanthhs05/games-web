# Hangman (CLI)

Terminal hangman. Guess the word letter by letter across 1–5 rounds and three
difficulty levels. Pure Python standard library — no dependencies.

## Run locally

```bash
cd hangman
python3 hangman.py
```

Run it from inside this directory so it can find `words.txt`.

## Run with Docker

```bash
cd hangman
docker build -t hangman .
docker run --rm -it hangman
```

The `-it` flags are required — the game reads from stdin.
