package com.project.vacationplanner.data.model

data class NotificationResponse(
    val id: String,
    val type: String,
    val message: String,
    val read: Boolean,
    val createdAt: String
)