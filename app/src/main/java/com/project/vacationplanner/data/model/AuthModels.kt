package com.project.vacationplanner.data.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val role: String,
    val jobTitle: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val role: String,
    val name: String = ""
)