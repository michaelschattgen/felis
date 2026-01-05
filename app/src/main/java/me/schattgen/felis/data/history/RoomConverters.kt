package me.schattgen.felis.data.history

import androidx.room.TypeConverter

class RoomConverters {
    @TypeConverter
    fun fromSource(value: CleanEventSource): String = value.name

    @TypeConverter
    fun toSource(value: String): CleanEventSource =
        runCatching { CleanEventSource.valueOf(value) }.getOrElse { CleanEventSource.Unknown }
}