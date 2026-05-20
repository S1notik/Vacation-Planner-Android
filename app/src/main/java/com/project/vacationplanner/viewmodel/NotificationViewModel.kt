package com.project.vacationplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.project.vacationplanner.data.repository.NotificationRepository
import com.project.vacationplanner.data.model.NotificationResponse

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = NotificationRepository(application)

    private val _notifications = MutableStateFlow<List<NotificationResponse>>(emptyList())
    val notifications: StateFlow<List<NotificationResponse>> = _notifications.asStateFlow()

    val unreadCount: StateFlow<Int> = _notifications
        .map { it.count { n -> !n.read } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun loadNotifications() {
        viewModelScope.launch {
            repo.refreshNotifications()
                .onSuccess { _notifications.value = it }
                .onFailure { }
        }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            repo.markAsRead(id)
                .onSuccess { loadNotifications() }
                .onFailure { }
        }
    }
}