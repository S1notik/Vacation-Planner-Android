package com.project.vacationplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.project.vacationplanner.data.repository.VacationRepository
import com.project.vacationplanner.data.TokenManager
import com.project.vacationplanner.ui.model.StatisticsUiState

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val vacationRepo = VacationRepository(application)

    private val _state = MutableStateFlow(StatisticsUiState())
    val state: StateFlow<StatisticsUiState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            val userName = TokenManager.getName(getApplication()) ?: ""
            vacationRepo.refreshTeamVacations()
                .onSuccess { list ->
                    val pending = list.count { it.status == "PENDING" }
                    val approved = list.count { it.status == "APPROVED" }
                    val rejected = list.count { it.status == "REJECTED" }
                    val avg = if (list.isNotEmpty()) list.sumOf { it.daysCount } / list.size else 0
                    _state.value = StatisticsUiState(
                        userName = userName,
                        pendingCount = pending,
                        approvedCount = approved,
                        rejectedCount = rejected,
                        avgVacationDays = avg,
                        totalRequests = list.size,
                        remainingDays = 0
                    )
                }
                .onFailure { error ->
                    android.util.Log.e("StatisticsVM", "Error: ${error.message}")
                }
        }
    }
}