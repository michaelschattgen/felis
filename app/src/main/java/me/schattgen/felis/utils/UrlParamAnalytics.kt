package me.schattgen.felis.utils

import android.net.Uri
import java.util.Locale
import me.schattgen.felis.data.history.CleanEventItemEntity
import me.schattgen.felis.data.history.RemovedParamUsageItem

data class RemovedParamItem(
    val name: String,
    val value: String,
)

object UrlParamAnalytics {

    fun removedParams(originalUrl: String, cleanedUrl: String): List<RemovedParamItem> {
        val originalUri = runCatching { Uri.parse(originalUrl) }.getOrNull() ?: return emptyList()
        val cleanedUri = runCatching { Uri.parse(cleanedUrl) }.getOrNull() ?: return emptyList()

        val cleanedCounts = mutableMapOf<String, Int>()
        for (name in cleanedUri.queryParameterNames) {
            val key = name.lowercase(Locale.ROOT)
            for (value in cleanedUri.getQueryParameters(name)) {
                val token = "$key\u0000$value"
                cleanedCounts[token] = (cleanedCounts[token] ?: 0) + 1
            }
        }

        val removed = mutableListOf<RemovedParamItem>()
        for (name in originalUri.queryParameterNames) {
            val key = name.lowercase(Locale.ROOT)
            for (value in originalUri.getQueryParameters(name)) {
                val token = "$key\u0000$value"
                val remaining = cleanedCounts[token] ?: 0
                if (remaining > 0) {
                    cleanedCounts[token] = remaining - 1
                } else {
                    removed += RemovedParamItem(name = name, value = value)
                }
            }
        }

        return removed
    }

    fun removedParamCounts(
        events: List<CleanEventItemEntity>,
        limit: Int? = null,
    ): List<RemovedParamUsageItem> {
        val counts = linkedMapOf<String, Long>()

        for (event in events) {
            val removed = removedParams(event.originalUrl, event.cleanedUrl)
            for (item in removed) {
                val key = item.name.lowercase(Locale.ROOT)
                counts[key] = (counts[key] ?: 0L) + 1L
            }
        }

        val sorted = counts.entries
            .sortedByDescending { it.value }

        val entries = if (limit != null) sorted.take(limit) else sorted

        return entries.map { RemovedParamUsageItem(paramName = it.key, removedCount = it.value) }
    }
}
