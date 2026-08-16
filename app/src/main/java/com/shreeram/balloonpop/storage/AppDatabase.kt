package com.shreeram.balloonpop.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shreeram.balloonpop.leaderboard.LeaderboardDao
import com.shreeram.balloonpop.leaderboard.LeaderboardEntry

@Database(entities = [LeaderboardEntry::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun leaderboardDao(): LeaderboardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "balloon_pop_database"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }

        internal val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE leaderboard_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        profileId TEXT NOT NULL,
                        playerName TEXT NOT NULL,
                        score INTEGER NOT NULL,
                        timestamp INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    INSERT INTO leaderboard_new (id, profileId, playerName, score, timestamp)
                    SELECT id, profileId, playerName, score, timestamp
                    FROM leaderboard AS current
                    WHERE NOT EXISTS (
                        SELECT 1 FROM leaderboard AS candidate
                        WHERE candidate.profileId = current.profileId
                          AND (candidate.score > current.score
                               OR (candidate.score = current.score AND candidate.id > current.id))
                    )
                    """.trimIndent()
                )
                database.execSQL("DROP TABLE leaderboard")
                database.execSQL("ALTER TABLE leaderboard_new RENAME TO leaderboard")
                database.execSQL(
                    "CREATE UNIQUE INDEX index_leaderboard_profileId ON leaderboard(profileId)"
                )
            }
        }
    }
}
