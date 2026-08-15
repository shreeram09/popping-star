package com.shreeram.balloonpop.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shreeram.balloonpop.leaderboard.LeaderboardRepository
import com.shreeram.balloonpop.profile.ProfileViewModel
import com.shreeram.balloonpop.settings.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataManagementScreen(
    profileViewModel: ProfileViewModel,
    settingsViewModel: SettingsViewModel,
    leaderboardRepository: LeaderboardRepository,
    onNavigateBack: () -> Unit,
    onNavigateToRoot: () -> Unit
) {
    var showConfirmDelete by remember { mutableStateOf(false) }
    var showConfirmClearLeaderboard by remember { mutableStateOf(false) }
    val currentProfile by profileViewModel.currentProfile.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Data & Privacy") },
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
            Text("Dangerous Actions", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(32.dp))

            SpriteButton(
                type = SpriteButtonType.BLANK_LONG,
                text = "Clear Scores",
                onClick = { showConfirmClearLeaderboard = true },
                width = 240,
                height = 64
            )

            Spacer(modifier = Modifier.height(16.dp))

            SpriteButton(
                type = SpriteButtonType.BLANK_LONG,
                text = "Delete Profile",
                onClick = { showConfirmDelete = true },
                width = 240,
                height = 64
            )

            if (showConfirmClearLeaderboard) {
                AlertDialog(
                    onDismissRequest = { showConfirmClearLeaderboard = false },
                    title = { Text("Clear Leaderboard?") },
                    text = { Text("This will permanently remove all high scores from the Hall of Fame.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    leaderboardRepository.clearAll()
                                }
                                showConfirmClearLeaderboard = false
                            }
                        ) {
                            Text("Clear")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmClearLeaderboard = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showConfirmDelete) {
                AlertDialog(
                    onDismissRequest = { showConfirmDelete = false },
                    title = { Text("Delete Profile?") },
                    text = { Text("This will permanently remove all your scores and settings for ${currentProfile?.displayName}.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                currentProfile?.id?.let { id ->
                                    coroutineScope.launch {
                                        leaderboardRepository.deleteScoresForProfile(id)
                                        profileViewModel.deleteProfile(id)
                                    }
                                }
                                showConfirmDelete = false
                                onNavigateToRoot()
                            }
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDelete = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
