package com.shreeram.balloonpop.leaderboard

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leaderboard")
data class LeaderboardEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: String,
    val playerName: String,
    val score: Int,
    val timestamp: Long = System.currentTimeMillis()
)
