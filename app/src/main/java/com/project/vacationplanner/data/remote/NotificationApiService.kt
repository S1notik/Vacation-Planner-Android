package com.project.vacationplanner.data.remote

import com.project.vacationplanner.data.model.*
import retrofit2.http.*

interface NotificationApiService {
    @GET("api/notifications")
    suspend fun getNotifications(): List<NotificationResponse>

    @PATCH("api/notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: String)
}