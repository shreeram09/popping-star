package com.shreeram.balloonpop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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

    // Auto-start game on first entry
    var hasAutoStarted by remember { mutableStateOf(false) }
    LaunchedEffect(gameState.status) {
        if (!hasAutoStarted) {
            if (gameState.status == GameStatus.IDLE) {
                engine.startGame()
                hasAutoStarted = true
            } else if (gameState.status == GameStatus.PAUSED) {
                engine.resumeGame()
                hasAutoStarted = true
            }
        }
    }

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
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                shape = MaterialTheme.shapes.large
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    // Best Score (Center)
                    Column(
                        modifier = Modifier.align(Alignment.TopCenter),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "BEST",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${maxOf(currentProfile?.bestScore ?: 0, gameState.score)}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Score & Hearts (Start)
                    Column(modifier = Modifier.align(Alignment.TopStart)) {
                        Text(
                            text = "SCORE",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${gameState.score}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row {
                            repeat(gameState.lives) { index ->
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = if (index < gameState.lives) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                                if (index < 2) Spacer(modifier = Modifier.width(2.dp))
                            }
                        }
                    }

                    // Controls (End)
                    Row(
                        modifier = Modifier.align(Alignment.TopEnd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SpriteButton(
                            type = SpriteButtonType.CIRCLE_LEADERBOARD,
                            iconVector = Icons.Default.Leaderboard,
                            onClick = onNavigateToLeaderboard,
                            width = 40,
                            height = 40
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        SpriteButton(
                            type = SpriteButtonType.CIRCLE_GEAR,
                            iconVector = Icons.Default.Settings,
                            onClick = onNavigateToSettings,
                            width = 40,
                            height = 40
                        )
                    }
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
