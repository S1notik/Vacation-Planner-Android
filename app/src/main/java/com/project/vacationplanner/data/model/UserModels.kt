package com.project.vacationplanner.data.model

data class UserModels(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val phone: String? = null,
    val jobTitle: String? = null,
    val avatarUrl: String? = null
)

data class UserProfileResponse(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val phone: String? = null,
    val jobTitle: String? = null,
    val avatarUrl: String? = null
)

data class UpdateProfileRequest(
    val name: String? = null,
    val phone: String? = null,
    val jobTitle: String? = null,
    val avatarUrl: String? = null
)