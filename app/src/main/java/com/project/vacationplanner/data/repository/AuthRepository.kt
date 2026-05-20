package com.project.vacationplanner.data.repository

import android.content.Context
import com.project.vacationplanner.data.model.AuthResponse
import com.project.vacationplanner.data.model.LoginRequest
import com.project.vacationplanner.data.model.RegisterRequest
import com.project.vacationplanner.data.network.RetrofitClient
import com.project.vacationplanner.data.TokenManager

class AuthRepository(private val context: Context) {

    private val api = RetrofitClient.authService(context)

    suspend fun register(
        email: String,
        password: String,
        name: String,
        role: String
    ): Result<AuthResponse> = runCatching {
        val response = api.register(RegisterRequest(email, password, name, role))
        TokenManager.saveTokens(context, response.accessToken, response.refreshToken, response.role,
            name, email)
        response
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> = runCatching {
        // Очищаем старый токен перед логином
        TokenManager.clearTokens(context)
        val response = api.login(LoginRequest(email, password))
        TokenManager.saveTokens(context, response.accessToken, response.refreshToken, response.role,
            response.name, email = email)
        response
    }

    suspend fun logout(): Result<Unit> = runCatching {
        api.logout()
        TokenManager.clearTokens(context)
    }

    suspend fun isLoggedIn(): Boolean {
        return TokenManager.isLoggedIn(context)
    }

    suspend fun getRole(): String? {
        return TokenManager.getRole(context)
    }
}