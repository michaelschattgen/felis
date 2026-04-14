package me.schattgen.felis.data.history

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import me.schattgen.felis.utils.DomainUtils

class CleanHistoryRepository(
    private val dao: CleanEventItemDao
) {
    fun observeAllEvents(): Flow<List<CleanEventItemEntity>> = dao.observeAllEvents()

    fun observeLast30DaysEvents(nowEpochMs: Long): Flow<List<CleanEventItemEntity>> {
        val from = nowEpochMs - 30L * 24L * 60L * 60L * 1000L
        return dao.observeEventsSince(from)
    }

    fun observeHomeStats(nowEpochMs: Long): Flow<Statistics> {
        return combine(
            dao.observeTotalCleans(),
            dao.observeTotalRemovedParams(),
        ) { totalCleans, totalRemovedParams ->
            Statistics(
                totalCleans = totalCleans,
                totalRemovedParams = totalRemovedParams,
            )
        }
    }

    fun observeTopDomains(limit: Int): Flow<List<DomainUsageItem>> = dao.observeTopDomains(limit)

    fun observeAllDomains(): Flow<List<DomainUsageItem>> = dao.observeAllDomains()

    suspend fun recordCleanItem(
        originalUrl: String,
        cleanedUrl: String,
        removedParamCount: Int,
        origin: CleanEventSource,
        createdAt: Long = System.currentTimeMillis()
    ) {
        dao.insert(
            CleanEventItemEntity(
                originalUrl = originalUrl,
                cleanedUrl = cleanedUrl,
                cleanedDomain = DomainUtils.domainFromUrl(cleanedUrl),
                removedParamCount = removedParamCount,
                createdAt = createdAt,
                source = origin,
            )
        )
    }

    suspend fun recordCleanItems(
        cleans: List<CleanRecord>,
        origin: CleanEventSource,
        createdAt: Long = System.currentTimeMillis()
    ) {
        dao.insertAll(
            cleans.map {
                CleanEventItemEntity(
                    originalUrl = it.originalUrl,
                    cleanedUrl = it.cleanedUrl,
                    cleanedDomain = DomainUtils.domainFromUrl(it.cleanedUrl),
                    removedParamCount = it.removedParamCount,
                    createdAt = createdAt,
                    source = origin,
                )
            }
        )
    }
}

data class CleanRecord(
    val originalUrl: String,
    val cleanedUrl: String,
    val removedParamCount: Int
)
