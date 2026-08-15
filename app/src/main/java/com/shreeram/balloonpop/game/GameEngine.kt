package com.shreeram.balloonpop.game

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class GameEngine {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var screenWidth = 0f
    private var screenHeight = 0f

    private var spawnTimer = 0f
    private val baseSpawnInterval = 1.5f

    var onPop: (() -> Unit)? = null

    fun setDimensions(width: Float, height: Float) {
        screenWidth = width
        screenHeight = height
    }

    fun startGame() {
        spawnTimer = 0f
        _gameState.update { it.copy(status = GameStatus.RUNNING, score = 0, lives = 5, balloons = emptyList(), elapsedTimeSeconds = 0f, difficultyMultiplier = 1.0f) }
    }

    fun resetGame() {
        spawnTimer = 0f
        _gameState.value = GameState()
    }

    fun restoreGame(score: Int, lives: Int, elapsedTime: Float) {
        spawnTimer = 0f
        _gameState.update { it.copy(status = GameStatus.PAUSED, score = score, lives = lives, balloons = emptyList(), elapsedTimeSeconds = elapsedTime, difficultyMultiplier = 1.0f + (elapsedTime / 30f)) }
    }

    fun pauseGame() {
        if (_gameState.value.status == GameStatus.RUNNING) {
            _gameState.update { it.copy(status = GameStatus.PAUSED) }
        }
    }

    fun resumeGame() {
        if (_gameState.value.status == GameStatus.PAUSED) {
            _gameState.update { it.copy(status = GameStatus.RUNNING) }
        }
    }

    fun update(deltaSeconds: Float) {
        if (_gameState.value.status != GameStatus.RUNNING) return

        val safeDelta = deltaSeconds.coerceIn(0f, 0.05f)

        _gameState.update { state ->
            val newElapsedTime = state.elapsedTimeSeconds + safeDelta
            val newDifficulty = 1.0f + (newElapsedTime / 30f)

            var missedCount = 0
            // Update balloons
            val updatedBalloons = state.balloons.mapNotNull { balloon ->
                if (balloon.isPopped) {
                    val nextProgress = balloon.popProgress + safeDelta / 0.2f
                    return@mapNotNull if (nextProgress >= 1.0f) null
                    else balloon.copy(popProgress = nextProgress)
                }

                var newX = balloon.x + balloon.vx * safeDelta * newDifficulty
                var newY = balloon.y + balloon.vy * safeDelta * newDifficulty
                var newVx = balloon.vx

                // Bounce off horizontal edges
                if (newX - balloon.radius < 0) {
                    newX = balloon.radius
                    newVx = -balloon.vx
                } else if (newX + balloon.radius > screenWidth) {
                    newX = screenWidth - balloon.radius
                    newVx = -balloon.vx
                }

                // Remove if leaves top (Y < 0 in local coordinates)
                if (newY + balloon.radius < 0) {
                    missedCount++
                    null
                } else {
                    balloon.copy(x = newX, y = newY, vx = newVx)
                }
            }

            val newLives = (state.lives - missedCount).coerceAtLeast(0)
            val newStatus = if (newLives <= 0) GameStatus.GAME_OVER else state.status

            // Spawning
            spawnTimer += safeDelta
            val spawnInterval = baseSpawnInterval / newDifficulty
            val finalBalloons = if (spawnTimer >= spawnInterval && newStatus == GameStatus.RUNNING) {
                spawnTimer = 0f
                updatedBalloons + spawnBalloon()
            } else {
                updatedBalloons
            }

            state.copy(
                balloons = finalBalloons,
                score = state.score,
                lives = newLives,
                status = newStatus,
                elapsedTimeSeconds = newElapsedTime,
                difficultyMultiplier = newDifficulty
            )
        }
    }

    private fun spawnBalloon(): Balloon {
        val radius = Random.nextFloat() * 60f + 40f 
        val x = Random.nextFloat() * (screenWidth - 2 * radius) + radius
        // Spawn just below the visible area
        val y = screenHeight + radius
        val vx = (Random.nextFloat() - 0.5f) * 200f
        val vy = - (Random.nextFloat() * 100f + 200f)
        
        val hue = Random.nextFloat() * 360f
        val color = Color.hsv(hue, 0.8f, 0.9f)
        
        return Balloon(x = x, y = y, vx = vx, vy = vy, radius = radius, color = color)
    }

    fun onTap(x: Float, y: Float): Boolean {
        if (_gameState.value.status != GameStatus.RUNNING) return false
        var popped = false
        _gameState.update { state ->
            val updatedBalloons = state.balloons.map { balloon ->
                if (!popped && !balloon.isPopped && isPointInBalloon(x, y, balloon)) {
                    popped = true
                    balloon.copy(isPopped = true)
                } else {
                    balloon
                }
            }
            if (popped) {
                onPop?.invoke()
                state.copy(balloons = updatedBalloons, score = state.score + 1)
            } else {
                state
            }
        }
        return popped
    }

    private fun isPointInBalloon(px: Float, py: Float, balloon: Balloon): Boolean {
        val dx = px - balloon.x
        val dy = py - balloon.y
        // Significantly increased hit area and adjusted ellipse for easier popping
        val rx = balloon.radius + 15f 
        val ry = balloon.radius * 1.5f + 15f
        return (dx * dx) / (rx * rx) + (dy * dy) / (ry * ry) <= 1.0f
    }
}
