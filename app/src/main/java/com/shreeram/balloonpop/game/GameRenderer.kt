package com.shreeram.balloonpop.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.shreeram.balloonpop.R

@Composable
fun GameRenderer(
    engine: GameEngine,
    modifier: Modifier = Modifier
) {
    val gameState by engine.gameState.collectAsState()
    val balloonImage = ImageBitmap.imageResource(id = R.drawable.balloon)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                engine.setDimensions(size.width.toFloat(), size.height.toFloat())
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Press || event.type == PointerEventType.Move) {
                            event.changes.forEach { change ->
                                if (change.pressed) {
                                    engine.onTap(change.position.x, change.position.y)
                                }
                            }
                        }
                    }
                }
            }
    ) {
        drawGame(gameState, balloonImage)
    }
}

private fun DrawScope.drawGame(state: GameState, balloonImage: ImageBitmap) {
    val imageAspectRatio = balloonImage.height.toFloat() / balloonImage.width.toFloat()
    
    state.balloons.forEach { balloon ->
        val scale = if (balloon.isPopped) 1f + balloon.popProgress * 0.5f else 1f
        val alpha = if (balloon.isPopped) (1f - balloon.popProgress) * 0.6f else 0.6f
        
        val width = (balloon.radius * 2 * scale).toInt()
        val height = (width * imageAspectRatio).toInt()
        
        drawImage(
            image = balloonImage,
            dstOffset = IntOffset(
                (balloon.x - balloon.radius * scale).toInt(),
                (balloon.y - height / 2).toInt()
            ),
            dstSize = IntSize(width, height),
            colorFilter = ColorFilter.tint(
                balloon.color.copy(alpha = alpha),
                blendMode = BlendMode.Modulate
            )
        )
    }
}
