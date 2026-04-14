package me.schattgen.felis.data.history

data class DomainUsageItem(
    val domain: String,
    val cleanCount: Long,
    val lastUsedAt: Long,
)
