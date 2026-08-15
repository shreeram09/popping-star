package com.shreeram.balloonpop.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "PoppingStar",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(48.dp))

        SpriteButton(
            type = SpriteButtonType.PLAY,
            onClick = onNewGame,
            width = 72,
            height = 72
        )

        if (currentProfileName != null) {
            Spacer(modifier = Modifier.height(16.dp))
            SpriteButton(
                type = SpriteButtonType.RESUME,
                onClick = onResume,
                width = 72,
                height = 72
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
}
