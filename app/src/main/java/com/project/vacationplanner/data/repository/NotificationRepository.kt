package com.project.vacationplanner.data.repository

import android.content.Context
import com.project.vacationplanner.data.local.AppDatabase
import com.project.vacationplanner.data.local.entity.NotificationEntity
import com.project.vacationplanner.data.model.NotificationResponse
import com.project.vacationplanner.data.network.RetrofitClient
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val context: Context) {
    private val api = RetrofitClient.notificationService(context)
    private val dao = AppDatabase.getInstance(context).notificationDao()

    fun getCachedNotifications(): Flow<List<NotificationEntity>> = dao.getAll()

    suspend fun refreshNotifications(): Result<List<NotificationResponse>> = runCatching {
        val list = api.getNotifications()
        dao.deleteAll()
        dao.upsertAll(list.map { n ->
            NotificationEntity(n.id, n.type, n.message, n.read, n.createdAt)
        })
        list
    }

    suspend fun markAsRead(id: String): Result<Unit> = runCatching {
        api.markAsRead(id)
    }

    private fun NotificationResponse.toEntity() = NotificationEntity(
        id = id,
        type = type,
        message = message,
        isRead = read,
        createdAt = createdAt
    )
}