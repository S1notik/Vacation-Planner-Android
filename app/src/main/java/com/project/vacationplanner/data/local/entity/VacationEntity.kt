package com.project.vacationplanner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vacations")
data class VacationEntity(
    @PrimaryKey val id: String,
    val startDate: String,
    val endDate: String,
    val daysCount: Int,
    val status: String,
    val comment: String?,
    val createdAt: String,
    val employeeName: String?
)