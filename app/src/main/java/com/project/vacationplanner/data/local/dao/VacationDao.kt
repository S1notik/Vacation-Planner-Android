package com.project.vacationplanner.data.local.dao

import androidx.room.*
import com.project.vacationplanner.data.local.entity.VacationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VacationDao {
    @Query("SELECT * FROM vacations")
    fun getAll(): Flow<List<VacationEntity>>

    @Upsert
    suspend fun upsertAll(vacations: List<VacationEntity>)

    @Query("DELETE FROM vacations")
    suspend fun deleteAll()
}