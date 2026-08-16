package com.shreeram.balloonpop.leaderboard

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "leaderboard",
    indices = [Index(value = ["profileId"], unique = true)]
)
data class LeaderboardEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: String,
    val playerName: String,
    val score: Int,
    val timestamp: Long = System.currentTimeMillis()
)
