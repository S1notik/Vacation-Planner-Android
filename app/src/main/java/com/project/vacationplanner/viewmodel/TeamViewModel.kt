package com.project.vacationplanner.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.vacationplanner.data.TokenManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.project.vacationplanner.data.repository.TeamRepository
import com.project.vacationplanner.data.model.TeamMemberResponse
import com.project.vacationplanner.ui.model.TeamMemberUi

class TeamViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = TeamRepository(application)

    private val _teamMembers = MutableStateFlow<List<TeamMemberUi>>(emptyList())
    val teamMembers: StateFlow<List<TeamMemberUi>> = _teamMembers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _joinSuccess = MutableStateFlow(false)
    val joinSuccess: StateFlow<Boolean> = _joinSuccess.asStateFlow()

    private val _calendarData = MutableStateFlow<Map<Int, List<String>>>(emptyMap())
    val calendarData: StateFlow<Map<Int, List<String>>> = _calendarData.asStateFlow()

    fun loadTeamMembers() {
        viewModelScope.launch {
            _isLoading.value = true
            repo.getTeamMembers()
                .onSuccess { _teamMembers.value = it.map { m -> m.toUi() } }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun joinTeam(inviteCode: String) {
        viewModelScope.launch {
            repo.joinTeam(inviteCode)
                .onSuccess { _joinSuccess.value = true }
                .onFailure { _error.value = it.message }
        }
    }

    fun createTeam(name: String) {
        viewModelScope.launch {
            repo.createTeam(name)
                .onSuccess { response ->
                    TokenManager.saveInviteCode(getApplication(), response.inviteCode)
                    loadTeamMembers()
                }
                .onFailure { _error.value = it.message }
        }
    }

    private fun TeamMemberResponse.toUi() = TeamMemberUi(
        id = id,
        initials = name.split(" ").take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString(""),
        name = name,
        position = if (role == "EMPLOYER") "Работодатель" else "Сотрудник",
        usedDays = 0,
        totalDays = totalDays
    )


    fun loadCalendar(year: Int, month: Int) {
        viewModelScope.launch {
            repo.getTeamCalendar().onSuccess { list ->
                val map = mutableMapOf<Int, MutableList<String>>()
                list.forEach { member ->
                    member.vacations
                        .filter { it.status == "APPROVED" }
                        .forEach { period ->
                            val start = period.startDate.split("-")
                            val end = period.endDate.split("-")
                            if (start.size == 3 && end.size == 3) {
                                val startYear = start[0].toIntOrNull() ?: return@forEach
                                val startMonth = start[1].toIntOrNull() ?: return@forEach
                                val startDay = start[2].toIntOrNull() ?: return@forEach
                                val endYear = end[0].toIntOrNull() ?: return@forEach
                                val endMonth = end[1].toIntOrNull() ?: return@forEach
                                val endDay = end[2].toIntOrNull() ?: return@forEach
                                // Перебираем дни в текущем месяце
                                for (day in 1..31) {
                                    val inRange = when {
                                        startYear == endYear && startMonth == endMonth ->
                                            startYear == year && startMonth == month && day in startDay..endDay
                                        startYear == year && startMonth == month ->
                                            day >= startDay
                                        endYear == year && endMonth == month ->
                                            day <= endDay
                                        else -> false
                                    }
                                    if (inRange) {
                                        map.getOrPut(day) { mutableListOf() }.add(member.employeeName)
                                    }
                                }
                            }
                        }
                }
                android.util.Log.d("Calendar", "Map: $map")
                _calendarData.value = map
            }
        }
    }

}