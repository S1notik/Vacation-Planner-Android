package com.project.vacationplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
                .onSuccess { loadTeamMembers() }
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
        totalDays = 28
    )
}