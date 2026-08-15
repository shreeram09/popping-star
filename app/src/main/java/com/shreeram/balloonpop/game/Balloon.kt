package com.shreeram.balloonpop.game

import androidx.compose.ui.graphics.Color
import java.util.UUID

data class Balloon(
    val id: String = UUID.randomUUID().toString(),
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val radius: Float,
    val color: Color,
    val isPopped: Boolean = false,
    val popProgress: Float = 0f
)
