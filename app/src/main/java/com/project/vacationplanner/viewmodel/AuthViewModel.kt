package com.project.vacationplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.vacationplanner.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// UI состояние экрана
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val role: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)

    val pendingInviteCode = MutableStateFlow<String?>(null)
    val uiState: StateFlow<AuthUiState> = _uiState

    val userRole = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            userRole.value = repository.getRole()
        }
    }

    fun register(email: String, password: String, name: String, role: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.register(email, password, name, role)
                .onSuccess { response ->
                    _uiState.value = AuthUiState.Success(response.role)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Ошибка регистрации")
                }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.login(email, password)
                .onSuccess { response ->
                    _uiState.value = AuthUiState.Success(response.role)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Ошибка входа")
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = AuthUiState.Idle
        }
    }

    fun checkAuth() {
        viewModelScope.launch {
            val loggedIn = repository.isLoggedIn()
            if (loggedIn) {
                val role = repository.getRole() ?: "EMPLOYEE"
                _uiState.value = AuthUiState.Success(role)
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}