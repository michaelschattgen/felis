package me.schattgen.felis

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
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

        val cleaned = LinkCleaner.cleanSharedText(input)

        val result = Intent().putExtra(Intent.EXTRA_PROCESS_TEXT, cleaned)
        setResult(Activity.RESULT_OK, result)
        finish()
    }
}
