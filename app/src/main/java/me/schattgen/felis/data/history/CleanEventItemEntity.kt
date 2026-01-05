package me.schattgen.felis.data.history

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "clean_event_items",
    indices = [
        Index(value = ["createdAt"]),
        Index(value = ["source"]),
    ]
)
data class CleanEventItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val originalUrl: String,
    val cleanedUrl: String,

    val removedParamCount: Int,

    val createdAt: Long,

    val source: CleanEventSource,
)
