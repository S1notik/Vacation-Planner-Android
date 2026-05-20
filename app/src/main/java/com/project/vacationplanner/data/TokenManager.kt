package com.project.vacationplanner.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

object TokenManager {

    private val ACCESS_TOKEN = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    private val USER_ROLE = stringPreferencesKey("user_role")

    // Save tokens after login/register
    suspend fun saveTokens(
        context: Context,
        accessToken: String,
        refreshToken: String,
        role: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
            prefs[REFRESH_TOKEN] = refreshToken
            prefs[USER_ROLE] = role
        }
    }

    // Get access token
    suspend fun getAccessToken(context: Context): String? {
        return context.dataStore.data
            .map { it[ACCESS_TOKEN] }
            .first()
    }

    // Get refresh token
    suspend fun getRefreshToken(context: Context): String? {
        return context.dataStore.data
            .map { it[REFRESH_TOKEN] }
            .first()
    }

    // Get user role
    suspend fun getRole(context: Context): String? {
        return context.dataStore.data
            .map { it[USER_ROLE] }
            .first()
    }

    // Clear tokens on logout
    suspend fun clearTokens(context: Context) {
        context.dataStore.edit { it.clear() }
    }

    // Check if user is logged in
    suspend fun isLoggedIn(context: Context): Boolean {
        return getAccessToken(context) != null
    }
}