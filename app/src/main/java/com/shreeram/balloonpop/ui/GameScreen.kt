package com.shreeram.balloonpop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shreeram.balloonpop.audio.SoundManager
import com.shreeram.balloonpop.game.GameRenderer
import com.shreeram.balloonpop.game.GameStatus
import com.shreeram.balloonpop.game.GameViewModel
import com.shreeram.balloonpop.settings.BackgroundMode
import com.shreeram.balloonpop.settings.SettingsViewModel

@Composable
fun GameScreen(
    settingsViewModel: SettingsViewModel,
    gameViewModel: GameViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToLeaderboard: () -> Unit
) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }
    val settings by settingsViewModel.settings.collectAsState()
    val currentProfile by gameViewModel.currentProfile.collectAsState()
    val engine = gameViewModel.engine
    
    // Wire up sound
    engine.onPop = {
        if (settings.soundEnabled) {
            soundManager.playPopSound()
        }
    }

    val gameState by engine.gameState.collectAsState()

    // Pause game when navigating away
    DisposableEffect(Unit) {
        onDispose {
            engine.pauseGame()
            soundManager.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Full screen background layer
        when (settings.backgroundMode) {
            BackgroundMode.DEFAULT -> {
                Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
            }
            BackgroundMode.RANDOM_SOLID, BackgroundMode.CUSTOM_SOLID -> {
                Box(modifier = Modifier.fillMaxSize().background(Color(settings.backgroundColor)))
            }
            BackgroundMode.IMAGE -> {
                if (settings.backgroundImageUri != null) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = settings.backgroundImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f))
                        )
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
                }
            }
        }

        // Layout with HUD at top, Game in middle, Controls at bottom
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top HUD Row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Best Score
                Column(
                    modifier = Modifier.align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "BEST", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "${maxOf(currentProfile?.bestScore ?: 0, gameState.score)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Score & Lives
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    Text(text = "SCORE", style = MaterialTheme.typography.labelSmall)
                    Text(text = "${gameState.score}", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "LIVES: ${gameState.lives}",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (gameState.lives <= 1) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Controls
                Row(modifier = Modifier.align(Alignment.TopEnd), verticalAlignment = Alignment.CenterVertically) {
                    SpriteButton(
                        type = SpriteButtonType.CIRCLE_LEADERBOARD,
                        iconVector = Icons.Default.Leaderboard,
                        onClick = onNavigateToLeaderboard,
                        width = 44,
                        height = 44
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    SpriteButton(
                        type = SpriteButtonType.CIRCLE_GEAR,
                        iconVector = Icons.Default.Settings,
                        onClick = onNavigateToSettings,
                        width = 44,
                        height = 44
                    )
                }
            }

            // Middle: Game Playable Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                GameRenderer(
                    engine = engine,
                    modifier = Modifier.fillMaxSize()
                )
                
                if (gameState.status == GameStatus.GAME_OVER) {
                    Text(
                        text = "GAME OVER",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // Bottom: Game Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                if (gameState.status == GameStatus.IDLE || gameState.status == GameStatus.GAME_OVER) {
                    SpriteButton(
                        type = SpriteButtonType.PLAY,
                        onClick = { engine.startGame() },
                        width = 72,
                        height = 72
                    )
                } else {
                    SpriteButton(
                        type = if (gameState.status == GameStatus.RUNNING) SpriteButtonType.PAUSE else SpriteButtonType.RESUME,
                        onClick = {
                            if (gameState.status == GameStatus.RUNNING) engine.pauseGame()
                            else engine.resumeGame()
                        },
                        width = 72,
                        height = 72
                    )
                }
            }
        }
    }
}
