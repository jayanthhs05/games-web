#!/usr/bin/env bash
#
# Rebuild one branch per game from `main`.
#
# Each game branch contains only that game's files, hoisted to the repository
# root, so a hosting platform (Render, Railway, Heroku, Netlify, GitHub Actions,
# ...) can be pointed straight at the branch with no "root directory" config.
#
# Safe to run repeatedly: every game branch is recreated from the current `main`.

set -euo pipefail

cd "$(dirname "$0")/.."
GAMES=(hangman wordle sudoku 25wol alagulimane)

if [ -n "$(git status --porcelain)" ]; then
  echo "Working tree is dirty. Commit or stash first." >&2
  exit 1
fi

# Stash the Alagulimane web-deploy workflow before we start deleting files on branches.
ALA_WF="$(mktemp)"
git show main:scripts/alagulimane-web.yml > "$ALA_WF"
trap 'rm -f "$ALA_WF"' EXIT

git switch main >/dev/null

for g in "${GAMES[@]}"; do
  echo "== $g =="
  git switch -C "$g" main >/dev/null

  shopt -s dotglob
  for entry in *; do
    case "$entry" in
      .git|.gitignore|"$g") continue ;;
    esac
    git rm -rq -- "$entry"
  done

  mv "$g"/* .
  rmdir "$g"

  if [ "$g" = "alagulimane" ]; then
    mkdir -p .github/workflows
    cp "$ALA_WF" .github/workflows/web.yml
  fi
  shopt -u dotglob

  git add -A
  if git diff --cached --quiet; then
    echo "  unchanged"
  else
    git commit -qm "$g: standalone deployable branch (synced from main)"
  fi
done

git switch main >/dev/null
echo
echo "Branches:"
git branch --list
