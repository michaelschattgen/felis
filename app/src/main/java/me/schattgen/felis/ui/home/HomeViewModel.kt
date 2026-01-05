package me.schattgen.felis.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import me.schattgen.felis.FelisApp
import me.schattgen.felis.data.history.Statistics

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as FelisApp).cleanHistoryRepository

    val homeStats: StateFlow<Statistics> =
        repo.observeHomeStats(System.currentTimeMillis())
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = Statistics(
                    totalCleans = 0,
                    totalRemovedParams = 0,
                )
            )
}