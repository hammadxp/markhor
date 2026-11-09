#!/usr/bin/env bash
# Prints the next version (without "v") from git tags and commit messages.
#
# Usage: next_version.sh <auto|patch|minor|major> <stable|beta>
#
# auto picks the bump from commits since the last stable tag (Conventional Commits):
#   "feat!: ..." or "BREAKING CHANGE" -> major, "feat: ..." -> minor, anything else -> patch.
# Examples: last tag v1.2.3 + a "feat:" commit -> 1.3.0; same with beta -> 1.3.0-beta.1, then 1.3.0-beta.2.
# With no stable tag yet, the first version is 1.0.0.
set -euo pipefail

bump="${1:-auto}"
channel="${2:-stable}"

last_stable=$(git tag -l 'v[0-9]*' | grep -E '^v[0-9]+\.[0-9]+\.[0-9]+$' | sort -V | tail -n 1 || true)

if [[ -z "$last_stable" ]]; then
  next="1.0.0"
else
  IFS=. read -r major minor patch <<<"${last_stable#v}"

  if [[ "$bump" == "auto" ]]; then
    messages=$(git log "$last_stable..HEAD" --format=%B)
    if [[ -z "$messages" ]]; then
      echo "No commits since $last_stable, nothing to release." >&2
      exit 1
    fi

    if grep -qE '^[a-z]+(\([^)]*\))?!:|BREAKING[ -]CHANGE' <<<"$messages"; then
      bump="major"
    elif grep -qE '^feat(\([^)]*\))?:' <<<"$messages"; then
      bump="minor"
    else
      bump="patch"
    fi
  fi

  case "$bump" in
    major) next="$((major + 1)).0.0" ;;
    minor) next="$major.$((minor + 1)).0" ;;
    patch) next="$major.$minor.$((patch + 1))" ;;
    *) echo "Unknown bump \"$bump\". Use auto, patch, minor or major." >&2; exit 1 ;;
  esac
fi

if [[ "$channel" == "beta" ]]; then
  count=$(git tag -l "v$next-beta.*" | wc -l | tr -d ' ')
  next="$next-beta.$((count + 1))"
elif [[ "$channel" != "stable" ]]; then
  echo "Unknown channel \"$channel\". Use stable or beta." >&2
  exit 1
fi

echo "$next"
