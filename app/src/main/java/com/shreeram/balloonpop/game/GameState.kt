package com.shreeram.balloonpop.game

enum class GameStatus {
    IDLE,
    RUNNING,
    PAUSED,
    GAME_OVER
}

data class GameState(
    val balloons: List<Balloon> = emptyList(),
    val score: Int = 0,
    val lives: Int = 5,
    val status: GameStatus = GameStatus.IDLE,
    val elapsedTimeSeconds: Float = 0f,
    val difficultyMultiplier: Float = 1.0f
)
