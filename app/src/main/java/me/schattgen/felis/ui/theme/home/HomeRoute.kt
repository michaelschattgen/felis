package me.schattgen.felis.ui.theme.home

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import me.schattgen.felis.R
import me.schattgen.felis.ui.theme.home.components.AboutSheet
import me.schattgen.felis.utils.ClipboardUtils
import me.schattgen.felis.utils.LinkCleaner

@Composable
fun HomeRoute() {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }
    var aboutOpen by remember { mutableStateOf(false) }

    val snackbarCleaned = stringResource(R.string.snackbar_cleaned)
    val snackbarCopied = stringResource(R.string.snackbar_copied)
    val clipboardLabel = stringResource(R.string.clipboard_label)

    HomeScreen(
        snackbarHostState = snackbarHostState,
        input = input,
        output = output,
        onInputChange = { input = it },
        onCleanClick = {
            output = LinkCleaner.cleanSharedText(input)
            scope.launch { snackbarHostState.showSnackbar(snackbarCleaned) }
        },
        onCopyClick = {
            ClipboardUtils.copyText(context, output, clipboardLabel)
            scope.launch { snackbarHostState.showSnackbar(snackbarCopied) }
        },
        onAboutClick = { aboutOpen = true }
    )

    if (aboutOpen) {
        AboutSheet(onDismiss = { aboutOpen = false })
    }
}