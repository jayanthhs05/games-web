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

## Run with Docker

```bash
cd wordle
docker build -t wordle .
docker run --rm -it wordle
```
