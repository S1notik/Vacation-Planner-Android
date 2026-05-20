package com.project.vacationplanner.data.repository

import android.content.Context
import com.project.vacationplanner.data.local.AppDatabase
import com.project.vacationplanner.data.local.entity.VacationEntity
import com.project.vacationplanner.data.model.*
import com.project.vacationplanner.data.network.RetrofitClient
import kotlinx.coroutines.flow.Flow

class VacationRepository(private val context: Context) {
    private val api = RetrofitClient.vacationService(context)
    private val dao = AppDatabase.getInstance(context).vacationDao()

    fun getCachedVacations(): Flow<List<VacationEntity>> = dao.getAll()

    suspend fun refreshMyVacations(): Result<List<VacationResponse>> = runCatching {
        val list = api.getMyVacations()
        dao.deleteAll()
        dao.upsertAll(list.map { r ->
            VacationEntity(
                r.id,
                r.startDate,
                r.endDate,
                r.daysCount,
                r.status,
                r.comment,
                r.createdAt,
                r.employeeName
            )
        })
        list
    }

    suspend fun refreshTeamVacations(): Result<List<VacationResponse>> = runCatching {
        val list = api.getTeamVacations()
        dao.deleteAll()
        dao.upsertAll(list.map { r ->
            VacationEntity(
                r.id,
                r.startDate,
                r.endDate,
                r.daysCount,
                r.status,
                r.comment,
                r.createdAt,
                r.employeeName
            )
        })
        list
    }

    suspend fun getVacationBalance(): Result<VacationBalanceResponse> = runCatching {
        api.getVacationBalance()
    }

    suspend fun createVacation(
        startDate: String,
        endDate: String,
        comment: String? = null
    ): Result<VacationResponse> = runCatching {
        api.createVacation(CreateVacationRequest(startDate, endDate, comment))
    }

    suspend fun reviewVacation(
        id: String,
        status: String,
        reason: String? = null
    ): Result<VacationResponse> = runCatching {
        api.reviewVacation(id, ReviewVacationRequest(status, reason))
    }

    suspend fun cancelVacation(id: String): Result<VacationResponse> = runCatching {
        api.cancelVacation(id)
    }
}
