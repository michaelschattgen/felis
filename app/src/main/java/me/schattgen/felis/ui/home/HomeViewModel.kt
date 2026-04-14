package me.schattgen.felis.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import me.schattgen.felis.FelisApp
import me.schattgen.felis.data.history.CleanEventSource
import me.schattgen.felis.data.history.CleanEventItemEntity
import me.schattgen.felis.data.history.CleanRecord
import me.schattgen.felis.data.history.DomainUsageItem
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

    val topDomains: StateFlow<List<DomainUsageItem>> =
        repo.observeTopDomains(limit = 5)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    val allDomains: StateFlow<List<DomainUsageItem>> =
        repo.observeAllDomains()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    val historyEvents: StateFlow<List<CleanEventItemEntity>> =
        repo.observeAllEvents()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    fun recordCleanItems(cleans: List<CleanRecord>, source: CleanEventSource) {
        viewModelScope.launch {
            repo.recordCleanItems(cleans = cleans, origin = source)
        }
    }
}
