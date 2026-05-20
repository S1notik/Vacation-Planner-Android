package com.project.vacationplanner.data.local.dao

import androidx.room.*
import com.project.vacationplanner.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAll(): Flow<List<NotificationEntity>>

    @Upsert
    suspend fun upsertAll(notifications: List<NotificationEntity>)

    @Query("DELETE FROM notifications")
    suspend fun deleteAll()
}