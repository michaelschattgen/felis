package me.schattgen.felis

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import me.schattgen.felis.data.history.CleanEventSource
import me.schattgen.felis.utils.LinkCleaner
import me.schattgen.felis.utils.ShareUtils
import kotlin.text.isNullOrBlank
import kotlin.text.startsWith

class CleanShareActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val action = intent?.action
        var type = intent?.type

        if (action != Intent.ACTION_SEND || type?.startsWith("text/") != true) {
            finish()
            return
        }

        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText.isNullOrBlank()) {
            finish()
            return
        }

        val result = LinkCleaner.cleanText(sharedText)

        val historyRepository = (application as FelisApp).cleanHistoryRepository

        lifecycleScope.launch {
            if (result.records.isNotEmpty()) {
                historyRepository.recordCleanItems(result.records, CleanEventSource.ShareSheet)
            }

            ShareUtils.shareText(
                this@CleanShareActivity,
                result.cleanedText,
                chooserTitle = getString(R.string.share_cleaned_link)
            )
        }

        finish()
    }
}
