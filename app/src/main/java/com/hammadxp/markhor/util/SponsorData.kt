package com.hammadxp.markhor.util

import kotlinx.serialization.Serializable

/**
 * Shape of `sponsors.json` at the repository root, written by `.github/scripts/sync_patreon_sponsors.py`.
 *
 * Example: `{ "current": [{ "name": "Jane", "url": "https://www.patreon.com/jane", "avatar": "https://..." }],
 * "past": [] }`
 */
@Serializable
data class SponsorList(val current: List<Sponsor> = emptyList(), val past: List<Sponsor> = emptyList())

@Serializable data class Sponsor(val name: String, val url: String? = null, val avatar: String? = null)
