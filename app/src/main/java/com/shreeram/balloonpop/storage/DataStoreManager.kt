package com.shreeram.balloonpop.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.shreeram.balloonpop.settings.AppSettings
import com.shreeram.balloonpop.settings.BackgroundMode
import com.shreeram.balloonpop.settings.ThemeMode
import com.shreeram.balloonpop.settings.OrientationMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val CURRENT_PROFILE_ID = stringPreferencesKey("current_profile_id")
        private val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val ORIENTATION_MODE = stringPreferencesKey("orientation_mode")
        private val BACKGROUND_MODE = stringPreferencesKey("background_mode")
        private val BACKGROUND_COLOR = intPreferencesKey("background_color")
        private val BACKGROUND_IMAGE_URI = stringPreferencesKey("background_image_uri")
        
        private fun sessionScoreKey(profileId: String) =
            intPreferencesKey("session_${profileId}_score")

        private fun sessionLivesKey(profileId: String) =
            intPreferencesKey("session_${profileId}_lives")

        private fun sessionElapsedTimeKey(profileId: String) =
            floatPreferencesKey("session_${profileId}_elapsed_time")
    }

    val currentProfileId: Flow<String?> = dataStore.data.map { it[CURRENT_PROFILE_ID] }

    suspend fun setCurrentProfileId(id: String?) {
        dataStore.edit { prefs ->
            if (id == null) prefs.remove(CURRENT_PROFILE_ID)
            else prefs[CURRENT_PROFILE_ID] = id
        }
    }

    val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        prefs.toAppSettings()
    }

    suspend fun updateSettings(update: (AppSettings) -> AppSettings) {
        dataStore.edit { prefs ->
            val current = prefs.toAppSettings()
            val updated = update(current)
            prefs[SOUND_ENABLED] = updated.soundEnabled
            prefs[THEME_MODE] = updated.themeMode.name
            prefs[ORIENTATION_MODE] = updated.orientationMode.name
            prefs[BACKGROUND_MODE] = updated.backgroundMode.name
            prefs[BACKGROUND_COLOR] = updated.backgroundColor
            if (updated.backgroundImageUri == null) prefs.remove(BACKGROUND_IMAGE_URI)
            else prefs[BACKGROUND_IMAGE_URI] = updated.backgroundImageUri!!
        }
    }

    suspend fun saveSession(profileId: String, score: Int, lives: Int, elapsedTime: Float) {
        dataStore.edit { prefs ->
            prefs[sessionScoreKey(profileId)] = score
            prefs[sessionLivesKey(profileId)] = lives
            prefs[sessionElapsedTimeKey(profileId)] = elapsedTime
        }
    }

    suspend fun clearSession(profileId: String) {
        dataStore.edit { prefs ->
            prefs.remove(sessionScoreKey(profileId))
            prefs.remove(sessionLivesKey(profileId))
            prefs.remove(sessionElapsedTimeKey(profileId))
        }
    }

    suspend fun resetPreferences() {
        dataStore.edit { prefs ->
            prefs.remove(SOUND_ENABLED)
            prefs.remove(THEME_MODE)
            prefs.remove(ORIENTATION_MODE)
            prefs.remove(BACKGROUND_MODE)
            prefs.remove(BACKGROUND_COLOR)
            prefs.remove(BACKGROUND_IMAGE_URI)
        }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }

    fun sessionState(profileId: String): Flow<Triple<Int, Int, Float>?> = dataStore.data.map { prefs ->
        val score = prefs[sessionScoreKey(profileId)]
        val lives = prefs[sessionLivesKey(profileId)]
        val time = prefs[sessionElapsedTimeKey(profileId)]
        if (score != null && lives != null && time != null) {
            Triple(score, lives, time)
        } else null
    }

    private fun Preferences.toAppSettings() = AppSettings(
        soundEnabled = this[SOUND_ENABLED] ?: true,
        themeMode = enumValueOrDefault(this[THEME_MODE], ThemeMode.SYSTEM),
        orientationMode = enumValueOrDefault(this[ORIENTATION_MODE], OrientationMode.SYSTEM),
        backgroundMode = enumValueOrDefault(this[BACKGROUND_MODE], BackgroundMode.DEFAULT),
        backgroundColor = this[BACKGROUND_COLOR] ?: 0xFFFFFFFF.toInt(),
        backgroundImageUri = this[BACKGROUND_IMAGE_URI]
    )

    private inline fun <reified T : Enum<T>> enumValueOrDefault(value: String?, default: T): T =
        value?.let { runCatching { enumValueOf<T>(it) }.getOrNull() } ?: default
}
