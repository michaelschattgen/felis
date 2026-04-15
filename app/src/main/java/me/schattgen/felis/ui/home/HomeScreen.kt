package me.schattgen.felis.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.schattgen.felis.R
import me.schattgen.felis.data.history.DomainUsageItem
import me.schattgen.felis.data.history.RemovedParamUsageItem
import me.schattgen.felis.data.history.Statistics
import me.schattgen.felis.ui.components.cards.StatisticCard
import me.schattgen.felis.ui.components.helpers.ShapeHelpers.getGroupedShape
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    stats: Statistics,
    topDomains: List<DomainUsageItem>,
    topRemovedParams: List<RemovedParamUsageItem>,
    onCleanClick: () -> Unit,
    onParamsStatClick: () -> Unit,
    onAboutClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onShowAllDomainsClick: () -> Unit,
    onShowAllParamsClick: () -> Unit,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing.only(
                WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
            ),
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                )
            }
        ) { inner ->
            Column(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        StatisticCard(
                            iconRes = R.drawable.outline_bar_chart_24,
                            title = "URLs cleaned",
                            value = stats.totalCleans.toString(),
                            shape = getGroupedShape(isTop = true, isBottom = true, isStart = true, isEnd = false),
                            modifier = Modifier.weight(1f)
                        )

                        StatisticCard(
                            iconRes = R.drawable.outline_cleaning_services_24,
                            title = "Parameters removed",
                            value = stats.totalRemovedParams.toString(),
                            shape = getGroupedShape(isTop = true, isBottom = true, isStart = false, isEnd = true),
                            onClick = onParamsStatClick,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        shape = RoundedCornerShape(32.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        tonalElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.home_title),
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Text(
                                text = stringResource(R.string.home_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = onCleanClick,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.home_clean_button))
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        tonalElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.top_domains_title),
                                style = MaterialTheme.typography.titleLarge
                            )

                            if (topDomains.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.top_domains_empty),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                topDomains.forEachIndexed { index, item ->
                                    DomainUsageRow(item = item)
                                    if (index < topDomains.lastIndex) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                    }
                                }

                                TextButton(
                                    onClick = onShowAllDomainsClick,
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(stringResource(R.string.top_domains_show_all))
                                }
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        tonalElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.top_params_title),
                                style = MaterialTheme.typography.titleLarge
                            )

                            if (topRemovedParams.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.top_params_empty),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                topRemovedParams.forEachIndexed { index, item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item.paramName,
                                            style = MaterialTheme.typography.bodyLarge,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = item.removedCount.toString(),
                                            style = MaterialTheme.typography.headlineSmall,
                                            modifier = Modifier.padding(start = 16.dp)
                                        )
                                    }

                                    if (index < topRemovedParams.lastIndex) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                    }
                                }

                                TextButton(
                                    onClick = onShowAllParamsClick,
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(stringResource(R.string.top_params_show_all))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onHistoryClick) {
                        Icon(
                            painter = painterResource(R.drawable.outline_history_24),
                            contentDescription = stringResource(R.string.home_history_icon_desc),
                        )
                        Text(
                            text = stringResource(R.string.home_history_button),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .height(24.dp)
                            .width(1.dp)
                            .clip(RoundedCornerShape(50))
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.fillMaxSize()
                        ) {}
                    }

                    TextButton(onClick = onAboutClick) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.home_about_icon_desc)
                        )
                        Text(
                            text = stringResource(R.string.home_about_button),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DomainUsageRow(item: DomainUsageItem) {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withZone(ZoneId.systemDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.domain,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Last cleaned ${formatter.format(Instant.ofEpochMilli(item.lastUsedAt))}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = item.cleanCount.toString(),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
