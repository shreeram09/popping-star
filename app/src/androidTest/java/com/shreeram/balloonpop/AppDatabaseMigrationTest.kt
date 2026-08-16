package com.shreeram.balloonpop

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shreeram.balloonpop.storage.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    private val databaseName = "migration-test.db"

    @Before
    fun setUp() {
        context.deleteDatabase(databaseName)
    }

    @After
    fun tearDown() {
        context.deleteDatabase(databaseName)
    }

    @Test
    fun migrationFromVersion1RetainsOnlyEachProfilesHighestScore() = runBlocking {
        context.openOrCreateDatabase(databaseName, android.content.Context.MODE_PRIVATE, null).use { database ->
            database.execSQL(
                "CREATE TABLE leaderboard (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, profileId TEXT NOT NULL, playerName TEXT NOT NULL, score INTEGER NOT NULL, timestamp INTEGER NOT NULL)"
            )
            database.execSQL("INSERT INTO leaderboard VALUES (1, 'alice', 'Alice', 10, 1000)")
            database.execSQL("INSERT INTO leaderboard VALUES (2, 'alice', 'Alice', 25, 2000)")
            database.execSQL("INSERT INTO leaderboard VALUES (3, 'bob', 'Bob', 15, 3000)")
            database.version = 1
        }

        val migratedDatabase = Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .allowMainThreadQueries()
            .build()

        val entries = migratedDatabase.leaderboardDao().getTopScores().first()

        assertEquals(2, entries.size)
        assertEquals(listOf("alice", "bob"), entries.map { it.profileId })
        assertEquals(listOf(25, 15), entries.map { it.score })
        assertTrue(entries.none { it.profileId == "alice" && it.score == 10 })

        migratedDatabase.close()
    }
}