package me.schattgen.felis.utils

import android.net.Uri
import java.util.Locale

object DomainUtils {

    fun domainFromUrl(url: String): String {
        val host = runCatching { Uri.parse(url).host }.getOrNull()
            ?.lowercase(Locale.ROOT)
            ?.removePrefix("www.")
            ?: return ""

        return host
    }
}
