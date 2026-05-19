package me.schattgen.felis.ui.home

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import me.schattgen.felis.R
import me.schattgen.felis.data.history.CleanEventSource
import me.schattgen.felis.ui.home.components.AboutSheet
import me.schattgen.felis.utils.ClipboardUtils.copyText
import me.schattgen.felis.ui.components.dialogs.ManualInputDialog
import me.schattgen.felis.utils.ClipboardUtils.readText
import me.schattgen.felis.utils.LinkCleaner
import me.schattgen.felis.utils.LinkCleaner.containsUrl
import me.schattgen.felis.utils.ShareUtils.shareText

private object HomeRouteDestination {
    const val Main = "main"
    const val Domains = "domains"
    const val Params = "params"
    const val History = "history"
}

@Composable
fun HomeRoute() {
    val context = LocalContext.current
    val vm: HomeViewModel = viewModel()
    val stats by vm.homeStats.collectAsStateWithLifecycle()
    val topDomains by vm.topDomains.collectAsStateWithLifecycle()
    val topRemovedParams by vm.topRemovedParams.collectAsStateWithLifecycle()
    val allDomains by vm.allDomains.collectAsStateWithLifecycle()
    val allRemovedParams by vm.allRemovedParams.collectAsStateWithLifecycle()
    val historyEntries by vm.historyEvents.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var aboutOpen by remember { mutableStateOf(false) }
    var manualInputOpen by rememberSaveable { mutableStateOf(false) }
    var manualInput by rememberSaveable { mutableStateOf("") }
    var manualInputErrorResId by rememberSaveable { mutableStateOf<Int?>(null) }
    val navController = rememberNavController()

    val snackbarCleaned = stringResource(R.string.snackbar_cleaned)

    NavHost(
        navController = navController,
        startDestination = HomeRouteDestination.Main,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(220),
            ) + fadeIn(animationSpec = tween(220))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(220),
            ) + fadeOut(animationSpec = tween(220))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(220),
            ) + fadeIn(animationSpec = tween(220))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(220),
            ) + fadeOut(animationSpec = tween(220))
        },
    ) {
        composable(HomeRouteDestination.Main) {
            HomeScreen(
                snackbarHostState = snackbarHostState,
                stats = stats,
                topDomains = topDomains,
                topRemovedParams = topRemovedParams,
                onCleanClick = {
                    val clipboardText = readText(context)

                    val containsUrl = clipboardText?.let { containsUrl(it) }
                    if (containsUrl == false || clipboardText == null) {
                        manualInput = ""
                        manualInputErrorResId = null
                        manualInputOpen = true
                        return@HomeScreen
                    }

                    val cleanResult = LinkCleaner.cleanText(clipboardText)

                    scope.launch {
                        if (cleanResult.records.isNotEmpty()) {
                            vm.recordCleanItems(cleanResult.records, CleanEventSource.Clipboard)
                            shareText(context, cleanResult.cleanedText)
                            snackbarHostState.showSnackbar(snackbarCleaned)
                        } else {
                            snackbarHostState.showSnackbar(context.getString(R.string.home_no_changes_found))
                        }
                    }
                },
                onParamsStatClick = { navController.navigate(HomeRouteDestination.Params) },
                onAboutClick = { aboutOpen = true },
                onHistoryClick = { navController.navigate(HomeRouteDestination.History) },
                onShowAllDomainsClick = { navController.navigate(HomeRouteDestination.Domains) },
                onShowAllParamsClick = { navController.navigate(HomeRouteDestination.Params) },
            )
        }

        composable(HomeRouteDestination.Domains) {
            DomainsScreen(
                domains = allDomains,
                onBackClick = { navController.navigateUp() },
            )
        }

        composable(HomeRouteDestination.History) {
            HistoryScreen(
                entries = historyEntries,
                onEntryClick = { cleanedUrl ->
                    copyText(
                        context = context,
                        text = cleanedUrl,
                        label = context.getString(R.string.clipboard_label),
                    )
                    scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.snackbar_copied)) }
                },
                onBackClick = { navController.navigateUp() },
            )
        }

        composable(HomeRouteDestination.Params) {
            RemovedParamsScreen(
                items = allRemovedParams,
                onBackClick = { navController.navigateUp() },
            )
        }
    }

    if (manualInputOpen) {
        ManualInputDialog(
            open = manualInputOpen,
            value = manualInput,
            onValueChange = {
                manualInput = it
                manualInputErrorResId = null
            },
            errorResId = manualInputErrorResId,
            onDismiss = {
                manualInputErrorResId = null
                manualInputOpen = false
            },
            onConfirm = {
                val input = manualInput.trim()
                if (input.isBlank()) {
                    manualInputErrorResId = R.string.dialog_manual_input_error_empty
                    return@ManualInputDialog
                }

                if (!containsUrl(input)) {
                    manualInputErrorResId = R.string.dialog_manual_input_error_no_url
                    return@ManualInputDialog
                }

                val cleanResult = LinkCleaner.cleanText(input)

                scope.launch {
                    if (cleanResult.records.isNotEmpty()) {
                        vm.recordCleanItems(cleanResult.records, CleanEventSource.Manual)
                        shareText(context, cleanResult.cleanedText)
                        snackbarHostState.showSnackbar(snackbarCleaned)
                    } else {
                        snackbarHostState.showSnackbar(context.getString(R.string.home_no_changes_found))
                    }
                }

                manualInputErrorResId = null
                manualInputOpen = false
            }
        )
    }

    if (aboutOpen) {
        AboutSheet(onDismiss = { aboutOpen = false })
    }
}
