"""Writes sponsors.json from the Patreon API.

Active patrons go to "current", patrons who cancelled or whose payment failed go to "past",
each list newest first.
Free followers are skipped. Needs PATREON_ACCESS_TOKEN (the creator's access token).

Output example:
  {"current": [{"name": "Jane", "url": "https://www.patreon.com/jane", "avatar": "https://..."}], "past": []}
"""

import json
import os
import sys
import urllib.parse
import urllib.request

API = "https://www.patreon.com/api/oauth2/v2"
OUTPUT_FILE = "sponsors.json"

CURRENT_STATUSES = {"active_patron"}
PAST_STATUSES = {"former_patron", "declined_patron"}


def get_json(url: str, token: str) -> dict:
    request = urllib.request.Request(
        url, headers={"Authorization": f"Bearer {token}", "User-Agent": "markhor-sponsors-sync"}
    )
    with urllib.request.urlopen(request, timeout=30) as response:
        return json.load(response)


def fetch_members(token: str) -> tuple[list[dict], dict[str, dict]]:
    campaigns = get_json(f"{API}/campaigns", token)["data"]
    if not campaigns:
        sys.exit("No Patreon campaign found for this token.")
    campaign_id = campaigns[0]["id"]

    query = urllib.parse.urlencode(
        {
            "include": "user",
            "fields[member]": "full_name,patron_status,pledge_relationship_start,last_charge_date",
            "fields[user]": "full_name,thumb_url,url",
            "page[count]": 500,
        }
    )
    url = f"{API}/campaigns/{campaign_id}/members?{query}"

    members: list[dict] = []
    users: dict[str, dict] = {}
    while url:
        page = get_json(url, token)
        members.extend(page.get("data", []))
        for item in page.get("included", []):
            if item["type"] == "user":
                users[item["id"]] = item["attributes"]
        url = page.get("links", {}).get("next")
    return members, users


def to_sponsor(member: dict, users: dict[str, dict]) -> dict:
    user_ref = member.get("relationships", {}).get("user", {}).get("data") or {}
    user = users.get(user_ref.get("id"), {})
    name = (member["attributes"].get("full_name") or user.get("full_name") or "Anonymous").strip()

    sponsor = {"name": name}
    if user.get("url"):
        sponsor["url"] = user["url"]
    if user.get("thumb_url"):
        sponsor["avatar"] = user["thumb_url"]
    return sponsor


def main() -> None:
    token = os.environ.get("PATREON_ACCESS_TOKEN")
    if not token:
        sys.exit("PATREON_ACCESS_TOKEN is not set.")

    members, users = fetch_members(token)

    current = [m for m in members if m["attributes"].get("patron_status") in CURRENT_STATUSES]
    past = [m for m in members if m["attributes"].get("patron_status") in PAST_STATUSES]

    # Newest first: current by when they joined, past by their last payment.
    # Patreon dates are ISO 8601 strings, so they sort correctly as text.
    def joined(member: dict) -> str:
        return member["attributes"].get("pledge_relationship_start") or ""

    def last_paid(member: dict) -> str:
        attributes = member["attributes"]
        return attributes.get("last_charge_date") or attributes.get("pledge_relationship_start") or ""

    current.sort(key=joined, reverse=True)
    past.sort(key=last_paid, reverse=True)

    # Dates stay out of sponsors.json, which is public.
    result = {
        "current": [to_sponsor(m, users) for m in current],
        "past": [to_sponsor(m, users) for m in past],
    }
    with open(OUTPUT_FILE, "w", encoding="utf-8") as file:
        json.dump(result, file, ensure_ascii=False, indent=2)
        file.write("\n")

    print(f"Wrote {len(current)} current and {len(past)} past sponsors to {OUTPUT_FILE}.")


if __name__ == "__main__":
    main()
