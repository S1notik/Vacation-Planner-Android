package com.project.vacationplanner.data.model

data class CreateTeamRequest(
    val name: String
)

data class JoinTeamRequest(
    val inviteCode: String
)

data class CreateTeamResponse(
    val id: String,
    val name: String,
    val inviteCode: String,
    val inviteQrUrl: String,
    val createdAt: String
)

data class JoinTeamResponse(
    val teamId: String,
    val teamName: String,
    val employerName: String,
    val joinedAt: String
)

data class TeamMemberResponse(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val joinedAt: String,
    val totalDays: Int = 28,
    val usedDays: Int = 0

)