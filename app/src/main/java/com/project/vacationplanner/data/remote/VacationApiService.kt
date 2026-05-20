package com.project.vacationplanner.data.remote

import com.project.vacationplanner.data.model.*
import retrofit2.http.*

interface VacationApiService {
    @POST("api/vacations")
    suspend fun createVacation(@Body request: CreateVacationRequest): VacationResponse

    @GET("api/vacations/my")
    suspend fun getMyVacations(): List<VacationResponse>

    @GET("api/vacations/balance")
    suspend fun getVacationBalance(): VacationBalanceResponse

    @GET("api/vacations/team")
    suspend fun getTeamVacations(): List<VacationResponse>

    @PUT("api/vacations/{id}/review")
    suspend fun reviewVacation(
        @Path("id") id: String,
        @Body request: ReviewVacationRequest
    ): VacationResponse

    @DELETE("api/vacations/{id}")
    suspend fun cancelVacation(@Path("id") id: String): VacationResponse
}