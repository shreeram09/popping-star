package com.shreeram.balloonpop.leaderboard

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaderboardDao {
    @Insert
    suspend fun insertEntry(entry: LeaderboardEntry)

    @Query("SELECT * FROM leaderboard ORDER BY score DESC LIMIT 10")
    fun getTopScores(): Flow<List<LeaderboardEntry>>

    @Query("DELETE FROM leaderboard WHERE profileId = :profileId")
    suspend fun deleteScoresForProfile(profileId: String)

    @Query("DELETE FROM leaderboard")
    suspend fun clearAll()
}
