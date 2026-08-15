package com.shreeram.balloonpop.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shreeram.balloonpop.settings.SettingsViewModel
import com.shreeram.balloonpop.settings.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBackground: () -> Unit,
    onNavigateToDataManagement: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Sound", style = MaterialTheme.typography.titleLarge)
                SpriteButton(
                    type = if (settings.soundEnabled) SpriteButtonType.VOLUME_ON else SpriteButtonType.VOLUME_OFF,
                    onClick = { viewModel.updateSoundEnabled(!settings.soundEnabled) },
                    width = 64,
                    height = 64
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Theme", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
            ThemeMode.entries.forEach { mode ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = settings.themeMode == mode,
                        onClick = { viewModel.updateThemeMode(mode) }
                    )
                    Text(mode.name)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            SpriteButton(
                type = SpriteButtonType.BLANK_LONG,
                text = "Backgrounds",
                onClick = onNavigateToBackground,
                width = 240,
                height = 64
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SpriteButton(
                type = SpriteButtonType.BLANK_LONG,
                text = "Data Management",
                onClick = onNavigateToDataManagement,
                width = 240,
                height = 64
            )
        }
    }
}
