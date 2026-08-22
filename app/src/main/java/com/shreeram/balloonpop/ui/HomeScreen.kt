package com.shreeram.balloonpop.ui

import android.app.Activity
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shreeram.balloonpop.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNewGame: () -> Unit,
    onResume: () -> Unit,
    onSettings: () -> Unit,
    onLeaderboard: () -> Unit,
    currentProfileName: String?
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    var backPressedOnce by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    BackHandler {
        if (backPressedOnce) {
            (context as? Activity)?.finish()
        } else {
            backPressedOnce = true
            scope.launch {
                delay(2000)
                backPressedOnce = false
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White) // Blend with the white background of the image
    ) {
        Image(
            painter = painterResource(id = R.drawable.home_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit, // Prevent cutting
            alignment = Alignment.TopCenter
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Push content to the bottom white area
            Spacer(modifier = Modifier.weight(if (isLandscape) 1.5f else 1f))

            if (isLandscape) {
                // Horizontal layout for buttons in landscape to stay in the white area
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SpriteButton(
                        type = SpriteButtonType.PLAY,
                        onClick = onNewGame,
                        width = 72,
                        height = 72
                    )

                    if (currentProfileName != null) {
                        Spacer(modifier = Modifier.width(16.dp))
                        SpriteButton(
                            type = SpriteButtonType.RESUME,
                            onClick = onResume,
                            width = 72,
                            height = 72
                        )
                    }

                    Spacer(modifier = Modifier.width(32.dp))

                    SpriteButton(
                        type = SpriteButtonType.CIRCLE_GEAR,
                        iconVector = Icons.Default.Settings,
                        onClick = onSettings,
                        width = 64,
                        height = 64
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    SpriteButton(
                        type = SpriteButtonType.CIRCLE_LEADERBOARD,
                        iconVector = Icons.Default.Leaderboard,
                        onClick = onLeaderboard,
                        width = 64,
                        height = 64
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    SpriteButton(
                        type = SpriteButtonType.CLOSE,
                        onClick = { (context as? Activity)?.finish() },
                        width = 64,
                        height = 64
                    )
                }
            } else {
                // Vertical stack for portrait
                SpriteButton(
                    type = SpriteButtonType.PLAY,
                    onClick = onNewGame,
                    width = 84,
                    height = 84
                )

                if (currentProfileName != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    SpriteButton(
                        type = SpriteButtonType.RESUME,
                        onClick = onResume,
                        width = 84,
                        height = 84
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SpriteButton(
                        type = SpriteButtonType.CIRCLE_GEAR,
                        iconVector = Icons.Default.Settings,
                        onClick = onSettings,
                        width = 72,
                        height = 72
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    SpriteButton(
                        type = SpriteButtonType.CIRCLE_LEADERBOARD,
                        iconVector = Icons.Default.Leaderboard,
                        onClick = onLeaderboard,
                        width = 72,
                        height = 72
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    SpriteButton(
                        type = SpriteButtonType.CLOSE,
                        onClick = { (context as? Activity)?.finish() },
                        width = 72,
                        height = 72
                    )
                }
            }
            
            // Bottom margin to ensure distance from navigation bar
            Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 48.dp))
        }
    }
}
