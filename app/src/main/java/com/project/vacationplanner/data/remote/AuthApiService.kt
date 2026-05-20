package com.project.vacationplanner.data.remote

import com.project.vacationplanner.data.model.*
import retrofit2.http.*

interface AuthApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/logout")
    suspend fun logout()

    @POST("api/auth/refresh")
    suspend fun refresh(): AuthResponse
}