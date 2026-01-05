package me.schattgen.felis

import android.app.Application
import androidx.room.Room
import me.schattgen.felis.data.FelisDatabase
import me.schattgen.felis.data.history.CleanHistoryRepository

class FelisApp : Application() {

    val database: FelisDatabase by lazy {
        Room.databaseBuilder(this, FelisDatabase::class.java, "felis.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    val cleanHistoryRepository: CleanHistoryRepository by lazy {
        CleanHistoryRepository(database.cleanEventDao())
    }
}