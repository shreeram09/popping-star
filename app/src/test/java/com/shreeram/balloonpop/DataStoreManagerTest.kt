package com.shreeram.balloonpop

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.shreeram.balloonpop.settings.BackgroundMode
import com.shreeram.balloonpop.settings.OrientationMode
import com.shreeram.balloonpop.settings.ThemeMode
import com.shreeram.balloonpop.storage.DataStoreManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreManagerTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var manager: DataStoreManager
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { File(tmpFolder.newFolder(), "settings.preferences_pb") }
        )
        manager = DataStoreManager(dataStore)
    }

    @Test
    fun `test initial settings`() = runTest(testDispatcher) {
        val settings = manager.settings.first()
        assertTrue(settings.soundEnabled)
        assertEquals(ThemeMode.SYSTEM, settings.themeMode)
        assertEquals(BackgroundMode.DEFAULT, settings.backgroundMode)
    }

    @Test
    fun `test update settings`() = runTest(testDispatcher) {
        manager.updateSettings { it.copy(soundEnabled = false, themeMode = ThemeMode.DARK) }
        
        val updated = manager.settings.first()
        assertFalse(updated.soundEnabled)
        assertEquals(ThemeMode.DARK, updated.themeMode)
    }

    @Test
    fun `test invalid persisted enum values fall back to defaults`() = runTest(testDispatcher) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("theme_mode")] = "INVALID"
            preferences[stringPreferencesKey("orientation_mode")] = "SIDEWAYS"
            preferences[stringPreferencesKey("background_mode")] = "VIDEO"
        }

        val settings = manager.settings.first()

        assertEquals(ThemeMode.SYSTEM, settings.themeMode)
        assertEquals(OrientationMode.SYSTEM, settings.orientationMode)
        assertEquals(BackgroundMode.DEFAULT, settings.backgroundMode)
    }

    @Test
    fun `test profile id persistence`() = runTest(testDispatcher) {
        manager.setCurrentProfileId("test-uuid")
        assertEquals("test-uuid", manager.currentProfileId.first())
        
        manager.setCurrentProfileId(null)
        assertNull(manager.currentProfileId.first())
    }

    @Test
    fun `test sessions are isolated by profile`() = runTest(testDispatcher) {
        manager.saveSession("alice-id", 10, 4, 12f)
        manager.saveSession("bob-id", 2, 3, 5f)

        assertEquals(Triple(10, 4, 12f), manager.sessionState("alice-id").first())
        assertEquals(Triple(2, 3, 5f), manager.sessionState("bob-id").first())
    }

    @Test
    fun `test clearing one profile session does not clear another`() = runTest(testDispatcher) {
        manager.saveSession("alice-id", 10, 4, 12f)
        manager.saveSession("bob-id", 2, 3, 5f)

        manager.clearSession("alice-id")

        assertNull(manager.sessionState("alice-id").first())
        assertEquals(Triple(2, 3, 5f), manager.sessionState("bob-id").first())
    }

    @Test
    fun `test clear all removes settings profile and sessions`() = runTest(testDispatcher) {
        manager.setCurrentProfileId("alice-id")
        manager.updateSettings { it.copy(themeMode = ThemeMode.DARK) }
        manager.saveSession("alice-id", 10, 4, 12f)

        manager.clearAll()

        assertNull(manager.currentProfileId.first())
        assertEquals(ThemeMode.SYSTEM, manager.settings.first().themeMode)
        assertNull(manager.sessionState("alice-id").first())
    }

    @Test
    fun `test reset preferences preserves profile and session`() = runTest(testDispatcher) {
        manager.setCurrentProfileId("alice-id")
        manager.updateSettings { it.copy(themeMode = ThemeMode.DARK) }
        manager.saveSession("alice-id", 10, 4, 12f)

        manager.resetPreferences()

        assertEquals("alice-id", manager.currentProfileId.first())
        assertEquals(ThemeMode.SYSTEM, manager.settings.first().themeMode)
        assertEquals(Triple(10, 4, 12f), manager.sessionState("alice-id").first())
    }
}
