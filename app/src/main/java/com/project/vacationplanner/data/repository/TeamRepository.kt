package com.project.vacationplanner.data.repository

import android.content.Context
import com.project.vacationplanner.data.model.*
import com.project.vacationplanner.data.network.RetrofitClient

class TeamRepository(private val context: Context) {
    private val api = RetrofitClient.teamService(context)

    suspend fun createTeam(name: String): Result<CreateTeamResponse> = runCatching {
        api.createTeam(CreateTeamRequest(name))
    }

    suspend fun joinTeam(inviteCode: String): Result<JoinTeamResponse> = runCatching {
        api.joinTeam(JoinTeamRequest(inviteCode))
    }

    suspend fun getTeamMembers(): Result<List<TeamMemberResponse>> = runCatching {
        api.getTeamMembers()
    }

    suspend fun getTeamInfo(): Result<TeamInfoResponse> = runCatching {
        api.getTeamInfo()
    }
}