package me.schattgen.felis.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.schattgen.felis.R
import me.schattgen.felis.data.history.CleanEventSource
import me.schattgen.felis.data.history.CleanEventItemEntity
import me.schattgen.felis.utils.UrlParamAnalytics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    entries: List<CleanEventItemEntity>,
    onEntryClick: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.screen_history_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.common_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (entries.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.screen_history_empty),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = entries, key = { it.id }) { item ->
                    HistoryEntryCard(item = item, onEntryClick = onEntryClick)
                }
            }
        }
    }
}

@Composable
private fun HistoryEntryCard(
    item: CleanEventItemEntity,
    onEntryClick: (String) -> Unit,
) {
    var expanded by rememberSaveable(item.id) { mutableStateOf(false) }
    val removedParams = UrlParamAnalytics.removedParams(item.originalUrl, item.cleanedUrl)

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onEntryClick(item.cleanedUrl) }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.cleanedDomain.ifBlank { "Unknown domain" },
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.cleanedUrl,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${item.removedParamCount} params • ${item.source.toUiLabel()} • ${formatDateTime(item.createdAt)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        painter = painterResource(
                            id = if (expanded) R.drawable.outline_expand_less_24 else R.drawable.outline_expand_more_24
                        ),
                        contentDescription = stringResource(
                            if (expanded) R.string.history_collapse_details else R.string.history_expand_details
                        ),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (expanded) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Original",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.originalUrl,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Removed parameters",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    if (removedParams.isEmpty()) {
                        Text(
                            text = "No removable parameters detected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        removedParams.forEach { removed ->
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                                    Text(
                                        text = removed.name,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = removed.value,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun CleanEventSource.toUiLabel(): String = when (this) {
    CleanEventSource.Manual -> "Manual"
    CleanEventSource.ShareSheet -> "Share sheet"
    CleanEventSource.ContextMenu -> "Context menu"
    CleanEventSource.Clipboard -> "Clipboard"
    CleanEventSource.Unknown -> "Unknown"
}
