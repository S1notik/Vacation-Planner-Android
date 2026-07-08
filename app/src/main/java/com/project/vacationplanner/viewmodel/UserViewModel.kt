package com.project.vacationplanner.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.vacationplanner.data.model.UpdateProfileRequest
import com.project.vacationplanner.data.model.UserProfileResponse
import com.project.vacationplanner.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application ) {

    private val repo = UserRepository(application)

    private val _profile = MutableStateFlow<UserProfileResponse?>(null)
    val profile: StateFlow<UserProfileResponse?> = _profile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            repo.getProfile()
                .onSuccess { _profile.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun updateProfile(request: UpdateProfileRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            repo.updateProfile(request)
                .onSuccess { _profile.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }


}