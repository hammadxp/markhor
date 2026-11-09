"""Inserts a git-cliff release section into CHANGELOG.md and writes the GitHub release notes.

Usage: update_changelog.py <section.md> <release-notes.md>
Optional RELEASE_NOTES env var: free text (summary, known issues) placed under the version heading.

The section goes right below the <!-- next-release --> marker, so the newest release is on top
and the fork note at the end of the file stays in place.
"""

import os
import sys

CHANGELOG_FILE = "CHANGELOG.md"
MARKER = "<!-- next-release -->"


def main() -> None:
    section_file, notes_file = sys.argv[1], sys.argv[2]

    with open(section_file, encoding="utf-8") as file:
        lines = file.read().strip().splitlines()
    if not lines or not lines[0].startswith("## "):
        sys.exit(f"Unexpected git-cliff output in {section_file}.")

    heading, body = lines[0], "\n".join(lines[1:]).strip()
    extra = os.environ.get("RELEASE_NOTES", "").strip()
    if extra:
        body = f"{extra}\n\n{body}" if body else extra

    with open(CHANGELOG_FILE, encoding="utf-8", newline="") as file:
        changelog = file.read()
    if MARKER not in changelog:
        sys.exit(f"{MARKER} not found in {CHANGELOG_FILE}.")

    entry = f"{heading}\n\n{body}\n" if body else f"{heading}\n"
    before, after = changelog.split(MARKER, 1)
    # Exactly one blank line around the new entry, however many runs came before.
    changelog = f"{before}{MARKER}\n\n{entry}\n{after.lstrip(chr(10) + chr(13))}"
    with open(CHANGELOG_FILE, "w", encoding="utf-8", newline="") as file:
        file.write(changelog)

    # The release page already shows the version as its title, so the notes skip the heading.
    with open(notes_file, "w", encoding="utf-8", newline="\n") as file:
        file.write(f"{body}\n")


if __name__ == "__main__":
    main()
