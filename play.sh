#!/bin/sh
# Entry point for the web terminal (ttyd). Restart the game after each session so
# a browser tab is always playable without reconnecting.
while true; do
  clear
  python3 wordle.py || true
  echo
  echo "Starting a new session in 3s...  (close the tab to quit)"
  sleep 3
done
