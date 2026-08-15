package com.shreeram.balloonpop

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.shreeram.balloonpop.profile.ProfileRepository
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
class ProfileRepositoryTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: ProfileRepository
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { File(tmpFolder.newFolder(), "test.preferences_pb") }
        )
        repository = ProfileRepository(dataStore)
    }

    @Test
    fun `test add profile`() = runTest(testDispatcher) {
        val profile = repository.addProfile("Alice")
        assertNotNull(profile)
        assertEquals("Alice", profile?.displayName)

        val profiles = repository.profiles.first()
        assertEquals(1, profiles.size)
        assertEquals("Alice", profiles.first().displayName)
    }

    @Test
    fun `test duplicate name validation`() = runTest(testDispatcher) {
        repository.addProfile("Alice")
        val duplicate = repository.addProfile("alice") // case insensitive
        assertNull("Should not allow duplicate names", duplicate)

        val profiles = repository.profiles.first()
        assertEquals(1, profiles.size)
    }

    @Test
    fun `test name trimming`() = runTest(testDispatcher) {
        val profile = repository.addProfile("  Bob  ")
        assertEquals("Bob", profile?.displayName)
    }

    @Test
    fun `test delete profile`() = runTest(testDispatcher) {
        val profile = repository.addProfile("Charlie")
        assertNotNull(profile)
        
        repository.deleteProfile(profile!!.id)
        val profiles = repository.profiles.first()
        assertTrue(profiles.isEmpty())
    }

    @Test
    fun `test update score`() = runTest(testDispatcher) {
        val profile = repository.addProfile("Dave")
        repository.updateProfileScore(profile!!.id, 100, 60f)
        
        val updated = repository.profiles.first().first()
        assertEquals(100, updated.bestScore)
        assertEquals(100, updated.lastScore)
        assertEquals(60f, updated.lastGameDurationSeconds)
        
        // Update with lower score shouldn't change bestScore
        repository.updateProfileScore(profile.id, 50, 30f)
        val final = repository.profiles.first().first()
        assertEquals(100, final.bestScore)
        assertEquals(50, final.lastScore)
    }
}
