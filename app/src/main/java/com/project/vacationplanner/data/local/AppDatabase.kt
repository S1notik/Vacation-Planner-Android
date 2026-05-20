package com.project.vacationplanner.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.vacationplanner.data.local.dao.VacationDao
import com.project.vacationplanner.data.local.dao.NotificationDao
import com.project.vacationplanner.data.local.entity.VacationEntity
import com.project.vacationplanner.data.local.entity.NotificationEntity

@Database(
    entities = [VacationEntity::class, NotificationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vacationDao(): VacationDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vacation_planner_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}