package me.schattgen.felis.data.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CleanEventItemDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(event: CleanEventItemEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(events: List<CleanEventItemEntity>)

    @Query("SELECT * FROM clean_event_items ORDER BY createdAt DESC")
    fun observeAllEvents(): Flow<List<CleanEventItemEntity>>

    @Query("SELECT * FROM clean_event_items WHERE createdAt >= :fromEpochMs ORDER BY createdAt DESC")
    fun observeEventsSince(fromEpochMs: Long): Flow<List<CleanEventItemEntity>>

    @Query("SELECT COUNT(*) FROM clean_event_items")
    fun observeTotalCleans(): Flow<Long>

    @Query("SELECT COALESCE(SUM(removedParamCount), 0) FROM clean_event_items")
    fun observeTotalRemovedParams(): Flow<Long>

    @Query("SELECT COUNT(*) FROM clean_event_items WHERE createdAt >= :fromEpochMs")
    fun observeCleansSince(fromEpochMs: Long): Flow<Long>

    @Query("SELECT COALESCE(SUM(removedParamCount), 0) FROM clean_event_items WHERE createdAt >= :fromEpochMs")
    fun observeRemovedParamsSince(fromEpochMs: Long): Flow<Long>

    @Query(
        """
        SELECT cleanedDomain AS domain, COUNT(*) AS cleanCount, MAX(createdAt) AS lastUsedAt
        FROM clean_event_items
        WHERE cleanedDomain != ''
        GROUP BY cleanedDomain
        ORDER BY cleanCount DESC, lastUsedAt DESC
        LIMIT :limit
        """
    )
    fun observeTopDomains(limit: Int): Flow<List<DomainUsageItem>>

    @Query(
        """
        SELECT cleanedDomain AS domain, COUNT(*) AS cleanCount, MAX(createdAt) AS lastUsedAt
        FROM clean_event_items
        WHERE cleanedDomain != ''
        GROUP BY cleanedDomain
        ORDER BY cleanCount DESC, lastUsedAt DESC
        """
    )
    fun observeAllDomains(): Flow<List<DomainUsageItem>>
}
