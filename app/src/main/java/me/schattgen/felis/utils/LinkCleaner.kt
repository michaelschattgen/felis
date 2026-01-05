package me.schattgen.felis.utils

import android.net.Uri
import android.util.Patterns
import java.util.Locale
import java.util.regex.Matcher
import me.schattgen.felis.data.history.CleanRecord

object LinkCleaner {

    private val genericTrackingParams = setOf(
        "utm_source", "utm_medium", "utm_campaign", "utm_term", "utm_content",
        "fbclid", "gclid", "igshid", "si", "mc_cid", "mc_eid",
        "tag", "ref", "linkcode", "ascsubtag"
    )

    data class CleanUrlResult(
        val originalUrl: String,
        val cleanedUrl: String,
        val removedParamCount: Int
    ) {
        fun toRecord(): CleanRecord =
            CleanRecord(
                originalUrl = originalUrl,
                cleanedUrl = cleanedUrl,
                removedParamCount = removedParamCount
            )
    }

    data class CleanTextResult(
        val cleanedText: String,
        val records: List<CleanRecord>
    )

    fun containsUrl(text: String): Boolean {
        val matcher = Patterns.WEB_URL.matcher(text)
        return matcher.find()
    }

    fun cleanText(text: String): CleanTextResult {
        val matcher = Patterns.WEB_URL.matcher(text)
        val sb = StringBuffer()
        val records = ArrayList<CleanRecord>(4)

        while (matcher.find()) {
            val originalUrl = matcher.group() ?: continue
            val result = cleanUrl(originalUrl)

            if (result.cleanedUrl != result.originalUrl) {
                records.add(result.toRecord())
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(result.cleanedUrl))
        }

        matcher.appendTail(sb)

        return CleanTextResult(
            cleanedText = sb.toString(),
            records = records
        )
    }

    fun cleanUrl(url: String): CleanUrlResult {
        val uri = runCatching { Uri.parse(url) }.getOrNull()
            ?: return CleanUrlResult(url, url, 0)

        val host = uri.host?.lowercase(Locale.ROOT)
            ?: return CleanUrlResult(url, url, 0)

        val cleaned = when {
            "spotify.com" in host -> stripAllQueryParams(uri)
            "instagram.com" in host -> stripAllQueryParams(uri)
            "amazon." in host -> cleanAmazonUrl(uri)
            else -> stripGenericTrackingParams(uri)
        }

        val removedCount = countRemovedQueryParams(uri, cleaned)

        return CleanUrlResult(
            originalUrl = url,
            cleanedUrl = cleaned,
            removedParamCount = removedCount
        )
    }

    private fun stripAllQueryParams(uri: Uri): String =
        uri.buildUpon()
            .clearQuery()
            .build()
            .toString()

    private fun stripGenericTrackingParams(uri: Uri): String {
        val query = uri.query ?: return uri.toString()

        val builder = uri.buildUpon().clearQuery()

        for (name in uri.queryParameterNames) {
            val lower = name.lowercase(Locale.ROOT)
            val isTracking =
                lower in genericTrackingParams || lower.startsWith("utm_")

            if (isTracking) continue

            for (value in uri.getQueryParameters(name)) {
                builder.appendQueryParameter(name, value)
            }
        }

        return builder.build().toString()
    }

    private fun cleanAmazonUrl(uri: Uri): String {
        val path = uri.path ?: return stripGenericTrackingParams(uri)
        val segments = path.split('/').filter { it.isNotBlank() }

        val asin = findAmazonAsin(segments)

        return if (asin != null) {
            Uri.Builder()
                .scheme(uri.scheme)
                .encodedAuthority(uri.encodedAuthority)
                .appendPath("dp")
                .appendPath(asin)
                .build()
                .toString()
        } else {
            stripGenericTrackingParams(uri)
        }
    }

    private fun findAmazonAsin(segments: List<String>): String? {
        for (i in segments.indices) {
            if (segments[i] == "dp" && i + 1 < segments.size) {
                return segments[i + 1]
            }

            if (segments[i] == "gp" &&
                i + 2 < segments.size &&
                segments[i + 1] == "product"
            ) {
                return segments[i + 2]
            }
        }

        return null
    }

    private fun countRemovedQueryParams(originalUri: Uri, cleanedUrl: String): Int {
        val originalQuery = originalUri.query ?: return 0
        if (originalQuery.isBlank()) return 0

        val cleanedUri = runCatching { Uri.parse(cleanedUrl) }.getOrNull() ?: return 0

        val originalCount = originalUri.queryParameterNames.sumOf { name ->
            originalUri.getQueryParameters(name).size
        }

        val cleanedCount = cleanedUri.queryParameterNames.sumOf { name ->
            cleanedUri.getQueryParameters(name).size
        }

        return (originalCount - cleanedCount).coerceAtLeast(0)
    }
}
