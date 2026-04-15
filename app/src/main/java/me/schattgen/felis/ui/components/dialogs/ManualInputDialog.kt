package me.schattgen.felis.ui.components.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ManualInputDialog(
    open: Boolean,
    value: String,
    errorResId: Int?,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!open) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        ManualInputDialogCard(
            value = value,
            errorResId = errorResId,
            onValueChange = onValueChange,
            onDismiss = onDismiss,
            onConfirm = onConfirm
        )
    }
}
