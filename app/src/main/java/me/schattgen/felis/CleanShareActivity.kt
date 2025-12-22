package me.schattgen.felis

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import me.schattgen.felis.utils.LinkCleaner
import kotlin.apply
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

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, cleanedText)
            val subject = intent.getStringExtra(Intent.EXTRA_SUBJECT)
            if (!subject.isNullOrBlank()) {
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }
        }

        sendIntent.setType(type)

        val chooser = Intent.createChooser(
            sendIntent,
            getString(R.string.share_cleaned_link)
        )

        startActivity(chooser)
        finish()
    }
}
