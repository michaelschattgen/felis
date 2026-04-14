package me.schattgen.felis.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.schattgen.felis.data.history.CleanEventItemDao
import me.schattgen.felis.data.history.CleanEventItemEntity
import me.schattgen.felis.data.history.RoomConverters

@Database(
    entities = [CleanEventItemEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class FelisDatabase : RoomDatabase() {
    abstract fun cleanEventDao(): CleanEventItemDao
}
