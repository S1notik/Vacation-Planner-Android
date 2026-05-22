package com.project.vacationplanner.data.remote

import com.project.vacationplanner.data.model.*
import retrofit2.http.*

interface TeamApiService {
    @POST("api/teams")
    suspend fun createTeam(@Body request: CreateTeamRequest): CreateTeamResponse

    @POST("api/teams/join")
    suspend fun joinTeam(@Body request: JoinTeamRequest): JoinTeamResponse

    @GET("api/teams/members")
    suspend fun getTeamMembers(): List<TeamMemberResponse>

    @GET("api/teams/info")
    suspend fun getTeamInfo(): TeamInfoResponse
}