package me.schattgen.felis

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import me.schattgen.felis.data.history.CleanEventSource
import me.schattgen.felis.utils.LinkCleaner

class ProcessTextCleanActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action != Intent.ACTION_PROCESS_TEXT) {
            finish()
            return
        }

        val input = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
        if (input.isNullOrBlank()) {
            finish()
            return
        }

        val cleanResult = LinkCleaner.cleanText(input)

        val repo = (application as FelisApp).cleanHistoryRepository

        lifecycleScope.launch {
            if (cleanResult.records.isNotEmpty()) {
                repo.recordCleanItems(cleanResult.records, CleanEventSource.ContextMenu)
            }

            val out = Intent().putExtra(Intent.EXTRA_PROCESS_TEXT, cleanResult.cleanedText)
            setResult(Activity.RESULT_OK, out)
            finish()
        }
    }
}
