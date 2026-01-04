package me.schattgen.felis.ui.home

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import me.schattgen.felis.R
import me.schattgen.felis.ui.home.components.AboutSheet
import me.schattgen.felis.ui.theme.components.dialogs.ManualInputDialog
import me.schattgen.felis.utils.ClipboardUtils.readText
import me.schattgen.felis.utils.LinkCleaner
import me.schattgen.felis.utils.LinkCleaner.containsUrl
import me.schattgen.felis.utils.ShareUtils.shareText

@Composable
fun HomeRoute() {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var aboutOpen by remember { mutableStateOf(false) }
    var manualInputOpen by rememberSaveable { mutableStateOf(false) }
    var manualInput by rememberSaveable { mutableStateOf("") }

    val snackbarCleaned = stringResource(R.string.snackbar_cleaned)

    HomeScreen(
        snackbarHostState = snackbarHostState,
        onCleanClick = {
            val clipboardText = readText(context)

            val containsUrl = clipboardText?.let { containsUrl(it) }
            if (containsUrl == false || clipboardText == null) {
                manualInput = ""
                manualInputOpen = true
                return@HomeScreen
            }

            val cleaned = LinkCleaner.cleanSharedText(clipboardText)
            shareText(context, cleaned)

            scope.launch { snackbarHostState.showSnackbar(snackbarCleaned) }

        },
        onAboutClick = { aboutOpen = true }
    )

    if (manualInputOpen) {
        ManualInputDialog(
            open = manualInputOpen,
            value = manualInput,
            onValueChange = { manualInput = it },
            onDismiss = { manualInputOpen = false },
            onConfirm = {
                val cleaned = LinkCleaner.cleanSharedText(manualInput.trim())
                shareText(context, cleaned)
                manualInputOpen = false
                scope.launch { snackbarHostState.showSnackbar(snackbarCleaned) }
            }
        )
    }

    if (aboutOpen) {
        AboutSheet(onDismiss = { aboutOpen = false })
    }
}