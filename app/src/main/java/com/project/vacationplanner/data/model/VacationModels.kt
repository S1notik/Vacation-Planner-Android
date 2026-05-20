package com.project.vacationplanner.data.model

data class CreateVacationRequest(
    val startDate: String,
    val endDate: String,
    val comment: String? = null
)

data class ReviewVacationRequest(
    val status: String,
    val reason: String? = null
)

data class VacationResponse(
    val id: String,
    val startDate: String,
    val endDate: String,
    val daysCount: Int,
    val status: String,
    val comment: String?,
    val createdAt: String
)

data class VacationBalanceResponse(
    val totalDays: Int,
    val usedDays: Int,
    val remainingDays: Int,
    val year: Int
)
