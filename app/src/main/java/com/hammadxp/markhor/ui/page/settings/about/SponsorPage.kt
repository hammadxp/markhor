package com.hammadxp.markhor.ui.page.settings.about

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hammadxp.markhor.R
import com.hammadxp.markhor.ui.common.AsyncImageImpl
import com.hammadxp.markhor.ui.component.BackButton
import com.hammadxp.markhor.ui.component.PreferenceItem
import com.hammadxp.markhor.ui.component.PreferenceSubtitle
import com.hammadxp.markhor.util.PATREON_URL
import com.hammadxp.markhor.util.PreferenceUtil.updateInt
import com.hammadxp.markhor.util.SHOW_SPONSOR_MSG
import com.hammadxp.markhor.util.Sponsor
import com.hammadxp.markhor.util.SponsorUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val DEVELOPER_AVATAR_URL = "https://github.com/hammadxp.png"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SponsorsPage(onNavigateBack: () -> Unit) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState(),
            canScroll = { true },
        )
    val uriHandler = LocalUriHandler.current
    val currentSponsors = remember { mutableStateListOf<Sponsor>() }
    val pastSponsors = remember { mutableStateListOf<Sponsor>() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            // Opening this page resets the Settings reminder card counter.
            SHOW_SPONSOR_MSG.updateInt(0)
            SponsorUtil.getSponsors().onSuccess {
                currentSponsors.addAll(it.current)
                pastSponsors.addAll(it.past)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.sponsors)) },
                navigationIcon = { BackButton { onNavigateBack() } },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { values ->
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = values) {
            item { DeveloperMessageCard(onSponsorClick = { uriHandler.openUri(PATREON_URL) }) }

            // Every supporter gets the same thanks, so the only split is current vs past.
            sponsorSection(R.string.current_sponsors, currentSponsors) { uriHandler.openUri(it) }
            sponsorSection(R.string.past_sponsors, pastSponsors) { uriHandler.openUri(it) }
        }
    }
}

private fun LazyListScope.sponsorSection(
    @StringRes titleRes: Int,
    sponsors: List<Sponsor>,
    onOpenUrl: (String) -> Unit,
) {
    if (sponsors.isEmpty()) return

    item { PreferenceSubtitle(text = "${stringResource(titleRes)} (${sponsors.size})") }

    // No item keys: two supporters can share a display name.
    items(items = sponsors) { sponsor ->
        PreferenceItem(
            title = sponsor.name,
            leadingIcon = { SponsorAvatar(sponsor.avatar) },
            onClick = { sponsor.url?.let(onOpenUrl) },
        )
    }
}

@Composable
private fun SponsorAvatar(avatarUrl: String?) {
    val modifier = Modifier.padding(start = 8.dp, end = 16.dp).size(40.dp).clip(CircleShape)
    if (avatarUrl == null) {
        Icon(
            imageVector = Icons.Outlined.AccountCircle,
            contentDescription = null,
            modifier = modifier,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }

    AsyncImageImpl(
        model = avatarUrl,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun DeveloperMessageCard(onSponsorClick: () -> Unit) {
    Surface(
        shape = CardDefaults.shape,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
            Text(
                modifier = Modifier.padding(bottom = 4.dp).align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.msg_from_developer),
                style = MaterialTheme.typography.labelLarge,
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                AsyncImageImpl(
                    model = DEVELOPER_AVATAR_URL,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).aspectRatio(1f, true).clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Column {
                    Conversation(
                        modifier = Modifier.padding(bottom = 12.dp),
                        text = stringResource(id = R.string.sponsor_msg),
                    )
                    Conversation(text = stringResource(R.string.sponsor_msg2))
                }
            }

            Button(
                onClick = onSponsorClick,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                modifier = Modifier.align(Alignment.End),
            ) {
                Icon(
                    modifier = Modifier.padding(end = 8.dp).size(ButtonDefaults.IconSize),
                    imageVector = Icons.Outlined.VolunteerActivism,
                    contentDescription = null,
                )
                Text(text = stringResource(id = R.string.sponsor))
            }
        }
    }
}

@Composable
fun Conversation(modifier: Modifier = Modifier, text: String) {
    Row(
        modifier =
            modifier
                .padding(horizontal = 12.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
@Preview
fun SponsorPagePreview() {
    SponsorsPage {}
}
