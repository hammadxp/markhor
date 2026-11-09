package com.hammadxp.markhor.database.backup

import com.hammadxp.markhor.database.objects.CommandTemplate
import com.hammadxp.markhor.database.objects.DownloadedVideoInfo
import com.hammadxp.markhor.database.objects.OptionShortcut
import kotlinx.serialization.Serializable

@Serializable
data class Backup(
    val templates: List<CommandTemplate>? = null,
    val shortcuts: List<OptionShortcut>? = null,
    val downloadHistory: List<DownloadedVideoInfo>? = null,
)
