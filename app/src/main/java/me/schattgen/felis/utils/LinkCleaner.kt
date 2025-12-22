package me.schattgen.felis.utils

import android.net.Uri
import android.util.Patterns
import java.util.Locale
import java.util.regex.Matcher
import kotlin.collections.filter
import kotlin.collections.forEach
import kotlin.collections.indices
import kotlin.let
import kotlin.runCatching
import kotlin.text.contains
import kotlin.text.isNotEmpty
import kotlin.text.lowercase
import kotlin.text.split
import kotlin.text.startsWith

object LinkCleaner {

    private val genericTrackingParams = setOf(
        // UTM and similar
        "utm_source", "utm_medium", "utm_campaign", "utm_term", "utm_content",
        // Common trackers
        "fbclid", "gclid", "igshid", "si", "mc_cid", "mc_eid",
        // Amazon-ish
        "tag", "ref", "linkcode", "ascsubtag"
    )

    fun cleanSharedText(text: String): String {
        val matcher = Patterns.WEB_URL.matcher(text)
        val sb = StringBuffer()

        while (matcher.find()) {
            val originalUrl = matcher.group()
            val cleanedUrl = cleanSingleUrl(originalUrl)
            matcher.appendReplacement(sb, Matcher.quoteReplacement(cleanedUrl))
        }

        matcher.appendTail(sb)
        return sb.toString()
    }

    fun cleanSingleUrl(url: String): String {
        val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return url
        val host = uri.host?.lowercase(Locale.ROOT) ?: return url

        return when {
            "spotify.com" in host -> stripAllQueryParams(uri)
            "instagram.com" in host -> stripAllQueryParams(uri)
            "amazon." in host -> cleanAmazonUrl(uri)
            else -> stripGenericTrackingParams(uri)
        }
    }

    private fun stripAllQueryParams(uri: Uri): String =
        uri.buildUpon().clearQuery().build().toString()

    private fun stripGenericTrackingParams(uri: Uri): String {
        if (uri.query == null) return uri.toString()

        val builder = uri.buildUpon().clearQuery()

        uri.queryParameterNames
            .filter { name ->
                val lower = name.lowercase(Locale.ROOT)
                !genericTrackingParams.contains(lower) && !lower.startsWith("utm_")
            }
            .forEach { name ->
                uri.getQueryParameters(name).forEach { value ->
                    builder.appendQueryParameter(name, value)
                }
            }

        return builder.build().toString()
    }

    private fun cleanAmazonUrl(uri: Uri): String {
        val path = uri.path ?: return stripGenericTrackingParams(uri)
        val segments = path.split("/").filter { it.isNotEmpty() }
        var asin: String? = null

        for (i in segments.indices) {
            when {
                segments[i] == "dp" && i + 1 < segments.size -> {
                    asin = segments[i + 1]
                    break
                }
                segments[i] == "gp" && i + 2 < segments.size && segments[i + 1] == "product" -> {
                    asin = segments[i + 2]
                    break
                }
            }
        }

        return asin?.let {
            "${uri.scheme}://${uri.host}/dp/$it"
        } ?: stripGenericTrackingParams(uri)
    }
}
