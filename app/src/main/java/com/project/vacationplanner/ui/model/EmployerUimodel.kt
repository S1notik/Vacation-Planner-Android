package com.project.vacationplanner.ui.model

import com.project.vacationplanner.ui.enums.VacationStatus

data class EmployerStats(
    val employeesCount: Int = 0,
    val pendingCount: Int = 0,
    val approvedCount: Int = 0,
    val totalDays: Int = 0,
)

data class VacationRequestUi(
    val id: String = "",
    val employeeName: String = "",
    val initials: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val workDays: Int = 0,
    val isNew: Boolean = false,
    val status: String = "",
)

data class TeamMemberUi(
    val id: String = "",
    val name: String = "",
    val initials: String = "",
    val position: String = "",
    val usedDays: Int = 0,
    val totalDays: Int = 0,
)

data class RecentActivityUi(
    val employeeName: String = "",
    val initials: String = "",
    val dateRange: String = "",
    val daysCount: Int = 0,
)


data class StatisticsUiState(
    val userName: String = "",
    val pendingCount: Int = 0,
    val approvedCount: Int = 0,
    val rejectedCount: Int = 0,
    val avgVacationDays: Int = 0,
    val totalRequests: Int = 0,
    val remainingDays: Int = 0,
)


data class EmployeeVacationStats(
    val totalDays: Int = 0,
    val usedDays: Int = 0,
    val leftDays: Int = 0,
)

data class MyVacationRequestUi(
    val id: String = "",
    val dateRange: String = "",
    val workDays: Int = 0,
    val createdDate: String = "",
    val status: VacationStatus = VacationStatus.PENDING,
)