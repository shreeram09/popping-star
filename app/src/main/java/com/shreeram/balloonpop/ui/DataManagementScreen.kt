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
    var pendingAction by remember { mutableStateOf<DataAction?>(null) }
    var confirmationText by remember { mutableStateOf("") }
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
            Text("Data Management", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            SpriteButton(
                type = SpriteButtonType.BLANK_LONG,
                text = "Clear Scores",
                onClick = { pendingAction = DataAction.CLEAR_SCORES },
                width = 240,
                height = 64
            )

            Spacer(modifier = Modifier.height(16.dp))

            SpriteButton(
                type = SpriteButtonType.BLANK_LONG,
                text = "Clear Saved Game",
                onClick = { pendingAction = DataAction.CLEAR_SESSION },
                width = 240,
                height = 64
            )

            Spacer(modifier = Modifier.height(16.dp))

            SpriteButton(
                type = SpriteButtonType.BLANK_LONG,
                text = "Reset Preferences",
                onClick = { pendingAction = DataAction.RESET_PREFERENCES },
                width = 240,
                height = 64
            )

            Spacer(modifier = Modifier.height(16.dp))

            SpriteButton(
                type = SpriteButtonType.BLANK_LONG,
                text = "Remove Background",
                onClick = { pendingAction = DataAction.REMOVE_BACKGROUND },
                width = 240,
                height = 64
            )

            Spacer(modifier = Modifier.height(16.dp))

            SpriteButton(
                type = SpriteButtonType.BLANK_LONG,
                text = "Delete Profile",
                onClick = { pendingAction = DataAction.DELETE_PROFILE },
                width = 240,
                height = 64
            )

            Spacer(modifier = Modifier.height(16.dp))

            SpriteButton(
                type = SpriteButtonType.BLANK_LONG,
                text = "Clear All App Data",
                onClick = { pendingAction = DataAction.CLEAR_ALL },
                width = 240,
                height = 64
            )

            pendingAction?.let { action ->
                val requiresTypedConfirmation = action == DataAction.DELETE_PROFILE || action == DataAction.CLEAR_ALL
                val requiredConfirmation = currentProfile?.displayName ?: "DELETE"
                AlertDialog(
                    onDismissRequest = {
                        pendingAction = null
                        confirmationText = ""
                    },
                    title = { Text(action.title) },
                    text = {
                        Column {
                            Text(action.description)
                            if (requiresTypedConfirmation) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Type $requiredConfirmation to continue.")
                                OutlinedTextField(
                                    value = confirmationText,
                                    onValueChange = { confirmationText = it },
                                    singleLine = true
                                )
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    when (action) {
                                        DataAction.CLEAR_SCORES -> leaderboardRepository.clearAll()
                                        DataAction.CLEAR_SESSION -> profileViewModel.clearCurrentSession()
                                        DataAction.RESET_PREFERENCES -> settingsViewModel.resetPreferences()
                                        DataAction.REMOVE_BACKGROUND -> settingsViewModel.removeBackgroundImage()
                                        DataAction.DELETE_PROFILE -> {
                                            currentProfile?.id?.let { id ->
                                                leaderboardRepository.deleteScoresForProfile(id)
                                                profileViewModel.deleteProfile(id)
                                            }
                                            onNavigateToRoot()
                                        }
                                        DataAction.CLEAR_ALL -> {
                                            leaderboardRepository.clearAll()
                                            profileViewModel.clearAllProfiles()
                                            settingsViewModel.clearAll()
                                            onNavigateToRoot()
                                        }
                                    }
                                }
                                pendingAction = null
                                confirmationText = ""
                            }
                            ,
                            enabled = !requiresTypedConfirmation || confirmationText == requiredConfirmation
                        ) {
                            Text(action.confirmLabel)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            pendingAction = null
                            confirmationText = ""
                        }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

private enum class DataAction(
    val title: String,
    val description: String,
    val confirmLabel: String
) {
    CLEAR_SCORES(
        "Clear leaderboard?",
        "This permanently removes all high scores from the local Hall of Fame.",
        "Clear"
    ),
    CLEAR_SESSION(
        "Clear saved game?",
        "This removes the current profile's saved game progress.",
        "Clear"
    ),
    RESET_PREFERENCES(
        "Reset preferences?",
        "This restores sound, theme, orientation, and background preferences to their defaults.",
        "Reset"
    ),
    REMOVE_BACKGROUND(
        "Remove background?",
        "This removes the selected background image and restores the default background.",
        "Remove"
    ),
    DELETE_PROFILE(
        "Delete profile?",
        "This permanently removes the current profile, its saved game, and its leaderboard score.",
        "Delete"
    ),
    CLEAR_ALL(
        "Clear all app data?",
        "This permanently removes all profiles, saved games, leaderboard scores, and preferences on this device.",
        "Clear all"
    )
}
