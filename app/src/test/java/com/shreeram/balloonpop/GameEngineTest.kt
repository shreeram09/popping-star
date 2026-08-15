package com.shreeram.balloonpop

import com.shreeram.balloonpop.game.GameEngine
import com.shreeram.balloonpop.game.GameStatus
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GameEngineTest {

    private lateinit var engine: GameEngine

    @Before
    fun setup() {
        engine = GameEngine()
        engine.setDimensions(1080f, 1920f)
    }

    @Test
    fun `test initial state`() {
        val state = engine.gameState.value
        assertEquals(0, state.score)
        assertEquals(GameStatus.IDLE, state.status)
        assertTrue(state.balloons.isEmpty())
    }

    @Test
    fun `test start game`() {
        engine.startGame()
        val state = engine.gameState.value
        assertEquals(GameStatus.RUNNING, state.status)
        assertEquals(0, state.score)
    }

    @Test
    fun `test reset game returns to idle state`() {
        engine.startGame()
        engine.resetGame()

        assertEquals(GameStatus.IDLE, engine.gameState.value.status)
        assertEquals(0, engine.gameState.value.score)
        assertEquals(5, engine.gameState.value.lives)
        assertTrue(engine.gameState.value.balloons.isEmpty())
    }

    @Test
    fun `test pause and resume`() {
        engine.startGame()
        engine.pauseGame()
        assertEquals(GameStatus.PAUSED, engine.gameState.value.status)
        
        engine.resumeGame()
        assertEquals(GameStatus.RUNNING, engine.gameState.value.status)
    }

    private fun advanceTime(seconds: Float) {
        val step = 0.016f // 60 FPS
        var remaining = seconds
        while (remaining > 0) {
            val dt = minOf(remaining, step)
            engine.update(dt)
            remaining -= dt
        }
    }

    @Test
    fun `test balloon spawning and movement`() {
        engine.startGame()
        
        // Advance time to trigger spawn
        advanceTime(2.0f)
        
        val state = engine.gameState.value
        assertFalse("Balloons should have spawned", state.balloons.isEmpty())
        
        val initialY = state.balloons.first().y
        advanceTime(0.1f)
        val newY = engine.gameState.value.balloons.first().y
        
        assertTrue("Balloon should have moved up (decreasing Y)", newY < initialY)
    }

    @Test
    fun `test tap collision`() {
        engine.startGame()
        advanceTime(2.0f) // Spawn a balloon
        
        val balloon = engine.gameState.value.balloons.first()
        val tapped = engine.onTap(balloon.x, balloon.y)
        
        assertTrue("Tap should have registered", tapped)
        assertEquals("Score should be 1", 1, engine.gameState.value.score)
        assertTrue("Balloon should be marked as popped", engine.gameState.value.balloons.first().isPopped)
    }

    @Test
    fun `test tap collision with margin`() {
        engine.startGame()
        advanceTime(2.0f)
        
        val balloon = engine.gameState.value.balloons.first()
        // Tap just outside the radius (e.g., radius + 1px)
        // isPointInBalloon adds 2px margin
        val tapped = engine.onTap(balloon.x + balloon.radius + 1f, balloon.y)
        
        assertTrue("Tap should have registered within 2px margin", tapped)
    }

    @Test
    fun `test edge bouncing`() {
        engine.startGame()
        advanceTime(2.0f)
        val balloon = engine.gameState.value.balloons.first()
        
        advanceTime(10.0f)
        engine.gameState.value.balloons.forEach {
            assertTrue("Balloon X should be >= radius", it.x >= it.radius - 0.01f)
            assertTrue("Balloon X should be <= screenWidth - radius", it.x <= 1080f - it.radius + 0.01f)
        }
    }
}
