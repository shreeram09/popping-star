package com.shreeram.balloonpop.profile

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ProfileRepository(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val PROFILES_LIST = stringPreferencesKey("profiles_list")
    }

    val profiles: Flow<List<UserProfile>> = dataStore.data.map { prefs ->
        val json = prefs[PROFILES_LIST] ?: "[]"
        try {
            Json.decodeFromString<List<UserProfile>>(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addProfile(name: String): UserProfile? {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return null

        val currentProfiles = profiles.first()
        val existingProfile = currentProfiles.find { it.displayName.equals(trimmedName, ignoreCase = true) }
        if (existingProfile != null) {
            return existingProfile
        }

        val newProfile = UserProfile(displayName = trimmedName)
        val updatedProfiles = currentProfiles + newProfile
        dataStore.edit { prefs ->
            prefs[PROFILES_LIST] = Json.encodeToString(updatedProfiles)
        }
        return newProfile
    }

    suspend fun updateProfileScore(id: String, score: Int, duration: Float) {
        val currentProfiles = profiles.first()
        val updatedProfiles = currentProfiles.map {
            if (it.id == id) {
                it.copy(
                    bestScore = maxOf(it.bestScore, score),
                    lastScore = score,
                    lastGameDurationSeconds = duration
                )
            } else it
        }
        dataStore.edit { prefs ->
            prefs[PROFILES_LIST] = Json.encodeToString(updatedProfiles)
        }
    }

    suspend fun deleteProfile(id: String) {
        val currentProfiles = profiles.first()
        val updatedProfiles = currentProfiles.filter { it.id != id }
        dataStore.edit { prefs ->
            prefs[PROFILES_LIST] = Json.encodeToString(updatedProfiles)
        }
    }

    suspend fun clearAllData() {
        dataStore.edit { it.clear() }
    }
}
