package com.shreeram.balloonpop.profile

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserProfile(
    val id: String = UUID.randomUUID().toString(),
    val displayName: String,
    val bestScore: Int = 0,
    val lastScore: Int = 0,
    val lastGameDurationSeconds: Float = 0f
)
