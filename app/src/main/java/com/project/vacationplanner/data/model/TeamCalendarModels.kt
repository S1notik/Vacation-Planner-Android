package com.project.vacationplanner.data.model

data class TeamCalendarResponse(
    val employeeId: String,
    val employeeName: String,
    val vacations: List<VacationPeriod>
)

data class VacationPeriod(
    val startDate: String,
    val endDate: String,
    val daysCount: Int,
    val status: String
)