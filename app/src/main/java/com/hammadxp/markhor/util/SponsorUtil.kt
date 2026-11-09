package com.hammadxp.markhor.util

import android.util.Log
import androidx.annotation.CheckResult
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

const val PATREON_URL = "https://patreon.com/hammadxp"

// INFO: The list is a public file in the repo, so the app needs no API key. It is
// updated by hand or by a GitHub Action reading Patreon. While the repo is private,
// this request fails and the Sponsors page simply shows no names.
private const val SPONSORS_JSON_URL =
    "https://raw.githubusercontent.com/hammadxp/markhor/main/sponsors.json"

object SponsorUtil {
    private const val TAG = "SponsorUtil"

    private val request = Request.Builder().url(SPONSORS_JSON_URL).build()
    private val client = OkHttpClient()
    private val jsonFormat = Json { ignoreUnknownKeys = true }
    private var sponsorList: SponsorList? = null

    @CheckResult
    fun getSponsors(): Result<SponsorList> =
        client
            .runCatching {
                sponsorList
                    ?: newCall(request).execute().use { response ->
                        check(response.isSuccessful) { "HTTP ${response.code}" }
                        jsonFormat.decodeFromString<SponsorList>(response.body.string()).also {
                            sponsorList = it
                        }
                    }
            }
            .onFailure { Log.w(TAG, "Could not load sponsors", it) }
}
