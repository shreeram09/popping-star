package com.shreeram.balloonpop.leaderboard

import kotlinx.coroutines.flow.Flow

class LeaderboardRepository(private val leaderboardDao: LeaderboardDao) {
    val topScores: Flow<List<LeaderboardEntry>> = leaderboardDao.getTopScores()

    suspend fun addScore(entry: LeaderboardEntry) {
        leaderboardDao.insertEntry(entry)
    }

    suspend fun deleteScoresForProfile(profileId: String) {
        leaderboardDao.deleteScoresForProfile(profileId)
    }

    suspend fun clearAll() {
        leaderboardDao.clearAll()
    }
}
