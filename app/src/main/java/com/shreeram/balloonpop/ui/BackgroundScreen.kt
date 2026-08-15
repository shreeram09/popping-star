package com.shreeram.balloonpop.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.shreeram.balloonpop.settings.BackgroundMode
import com.shreeram.balloonpop.settings.SettingsViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    // Fallback
                }
                viewModel.updateBackground(BackgroundMode.IMAGE, uri = uri.toString())
            }
        }
    )

    val colors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Yellow, 
        Color.Cyan, Color.Magenta, Color.Gray, Color.DarkGray,
        Color(0xFFFF5722), Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFFE91E63)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Background") },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 8.dp)) {
                        SpriteButton(
                            type = SpriteButtonType.HOME,
                            onClick = onNavigateBack,
                            width = 44,
                            height = 44
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            BackgroundMode.entries.forEach { mode ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = settings.backgroundMode == mode,
                        onClick = {
                            if (mode == BackgroundMode.IMAGE) {
                                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            } else if (mode == BackgroundMode.RANDOM_SOLID) {
                                val randomColor = Color(
                                    Random.nextInt(256),
                                    Random.nextInt(256),
                                    Random.nextInt(256)
                                ).toArgb()
                                viewModel.updateBackground(mode, color = randomColor)
                            } else {
                                viewModel.updateBackground(mode)
                            }
                        }
                    )
                    Text(mode.name)
                }

                if (mode == BackgroundMode.CUSTOM_SOLID && settings.backgroundMode == BackgroundMode.CUSTOM_SOLID) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(48.dp),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.height(150.dp)
                    ) {
                        items(colors) { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (settings.backgroundColor == color.toArgb()) 2.dp else 0.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        viewModel.updateBackground(BackgroundMode.CUSTOM_SOLID, color = color.toArgb())
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}
