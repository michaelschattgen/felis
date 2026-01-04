package me.schattgen.felis.utils

import android.content.Context
import android.content.Intent

object ShareUtils {

    fun shareText(
        context: Context,
        text: String,
        subject: String? = null,
        type: String? = "text/plain",
        chooserTitle: String? = null
    ) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, text)
            if (!subject.isNullOrBlank()) {
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }
            this.type = type ?: "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(sendIntent, chooserTitle)
            .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

        context.startActivity(chooser)
    }
}
