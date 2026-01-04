package me.schattgen.felis

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
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

        val cleanedText = LinkCleaner.cleanSharedText(sharedText)

        ShareUtils.shareText(this, cleanedText, chooserTitle = getString(R.string.share_cleaned_link))

        finish()
    }
}
