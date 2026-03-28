package com.example.fitlife.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlife.data.local.DatabaseProvider
import com.example.fitlife.data.local.dao.CenterCount
import com.example.fitlife.data.local.dao.DayCount
import com.example.fitlife.data.local.dao.MonthCount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class StatsUiState(
    val monthlyVisits: List<MonthCount> = emptyList(),
    val topCenters: List<CenterCount> = emptyList(),
    val dailyVisitsLast30Days: List<DayCount> = emptyList(),
    val isLoading: Boolean = true
)

class StatsViewModel(app: Application) : AndroidViewModel(app) {

    private val visitsDao = DatabaseProvider.get(app).visitsDao()

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()

            val fromCalendar = Calendar.getInstance().apply {
                timeInMillis = now
                add(Calendar.DAY_OF_YEAR, -29)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val toCalendar = Calendar.getInstance().apply {
                timeInMillis = now
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }

            val monthly = visitsDao.countVisitsByMonth()
            val centers = visitsDao.countVisitsByCenter().take(5)
            val daily = visitsDao.countVisitsByDay(
                fromMs = fromCalendar.timeInMillis,
                toMs = toCalendar.timeInMillis
            )

            _uiState.value = StatsUiState(
                monthlyVisits = monthly,
                topCenters = centers,
                dailyVisitsLast30Days = daily,
                isLoading = false
            )
        }
    }
}