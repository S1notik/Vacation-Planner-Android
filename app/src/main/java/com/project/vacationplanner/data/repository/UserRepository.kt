package com.project.vacationplanner.data.repository

import android.content.Context
import com.project.vacationplanner.data.model.UpdateProfileRequest
import com.project.vacationplanner.data.model.UserProfileResponse
import com.project.vacationplanner.data.network.RetrofitClient

class UserRepository(private val context: Context) {

    private val api = RetrofitClient.userService(context)

    suspend fun getProfile(): Result<UserProfileResponse> = runCatching {
        api.getProfile()
    }

    suspend fun updateProfile(request: UpdateProfileRequest): Result<UserProfileResponse> =
        runCatching {
            api.update(request)
        }
}