package com.project.vacationplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.project.vacationplanner.data.repository.VacationRepository
import com.project.vacationplanner.ui.model.*
import com.project.vacationplanner.ui.enums.VacationStatus
import com.project.vacationplanner.data.model.VacationResponse

class VacationViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = VacationRepository(application)

    // Employee
    private val _myVacations = MutableStateFlow<List<MyVacationRequestUi>>(emptyList())
    val myVacations: StateFlow<List<MyVacationRequestUi>> = _myVacations.asStateFlow()

    private val _balance = MutableStateFlow(EmployeeVacationStats())
    val balance: StateFlow<EmployeeVacationStats> = _balance.asStateFlow()

    // Employer
    private val _teamVacations = MutableStateFlow<List<VacationRequestUi>>(emptyList())
    val teamVacations: StateFlow<List<VacationRequestUi>> = _teamVacations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadMyVacations() {
        viewModelScope.launch {
            repo.refreshMyVacations()
                .onSuccess { list ->
                    _myVacations.value = list.map { r -> r.toMyRequestUi() }
                }
                .onFailure { _error.value = it.message }
        }
    }

    fun loadBalance() {
        viewModelScope.launch {
            repo.getVacationBalance()
                .onSuccess {
                    _balance.value = EmployeeVacationStats(
                        totalDays = it.totalDays,
                        usedDays = it.usedDays,
                        leftDays = it.remainingDays
                    )
                }
                .onFailure { _error.value = it.message }
        }
    }

    fun loadTeamVacations() {
        viewModelScope.launch {
            repo.refreshTeamVacations()
                .onSuccess { list ->
                    _teamVacations.value = list.map { r -> r.toRequestUi() }
                }
                .onFailure { _error.value = it.message }
        }
    }

    fun createVacation(startDate: String, endDate: String) {
        viewModelScope.launch {
            repo.createVacation(startDate, endDate)
                .onSuccess { loadMyVacations() }
                .onFailure { _error.value = it.message }
        }
    }

    fun cancelVacation(id: String) {
        viewModelScope.launch {
            repo.cancelVacation(id)
                .onSuccess { loadMyVacations() }
                .onFailure { _error.value = it.message }
        }
    }

    fun approveVacation(id: String) {
        viewModelScope.launch {
            repo.reviewVacation(id, "APPROVED")
                .onSuccess { loadTeamVacations() }
                .onFailure { _error.value = it.message }
        }
    }

    fun rejectVacation(id: String) {
        viewModelScope.launch {
            repo.reviewVacation(id, "REJECTED")
                .onSuccess { loadTeamVacations() }
                .onFailure { _error.value = it.message }
        }
    }

    private fun VacationResponse.toMyRequestUi() = MyVacationRequestUi(
        id = id,
        dateRange = "$startDate — $endDate",
        workDays = daysCount,
        createdDate = createdAt.take(10).replace("-", "."),
        status = runCatching { VacationStatus.valueOf(status) }.getOrElse { VacationStatus.PENDING }
    )

    private fun VacationResponse.toRequestUi() = VacationRequestUi(
        id = id,
        initials = (employeeName ?: "").split(" ")
            .take(2).mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString(""),
        employeeName = employeeName ?: "Сотрудник",
        startDate = startDate,
        endDate = endDate,
        workDays = daysCount,
        isNew = status == "PENDING"
    )
}