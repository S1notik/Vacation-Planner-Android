package com.project.vacationplanner.data.remote

import com.project.vacationplanner.data.model.UpdateProfileRequest
import com.project.vacationplanner.data.model.UserProfileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserApiService {
    @GET("api/users/me")
    suspend fun getProfile(): UserProfileResponse

    @PATCH("api/users/me")
    suspend fun update(@Body request: UpdateProfileRequest): UserProfileResponse
}