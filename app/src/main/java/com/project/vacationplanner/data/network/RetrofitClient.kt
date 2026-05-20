package com.project.vacationplanner.data.network

import android.content.Context
import com.project.vacationplanner.data.TokenManager
import com.project.vacationplanner.data.remote.AuthApiService
import com.project.vacationplanner.data.remote.NotificationApiService
import com.project.vacationplanner.data.remote.TeamApiService
import com.project.vacationplanner.data.remote.VacationApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private fun getClient(context: Context): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val token = runBlocking { TokenManager.getAccessToken(context) }
            val request = if (token != null) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                chain.request()
            }
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    private fun getRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun authService(context: Context): AuthApiService =
        getRetrofit(context).create(AuthApiService::class.java)

    fun teamService(context: Context): TeamApiService =
        getRetrofit(context).create(TeamApiService::class.java)

    fun vacationService(context: Context): VacationApiService =
        getRetrofit(context).create(VacationApiService::class.java)

    fun notificationService(context: Context): NotificationApiService =
        getRetrofit(context).create(NotificationApiService::class.java)
}

