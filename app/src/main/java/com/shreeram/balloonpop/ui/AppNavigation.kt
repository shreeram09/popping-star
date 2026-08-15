package com.shreeram.balloonpop.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shreeram.balloonpop.game.GameViewModel
import com.shreeram.balloonpop.leaderboard.LeaderboardRepository
import com.shreeram.balloonpop.leaderboard.LeaderboardViewModel
import com.shreeram.balloonpop.profile.ProfileViewModel
import com.shreeram.balloonpop.settings.SettingsViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ProfileSetup : Screen("profile_setup")
    object Game : Screen("game")
    object Settings : Screen("settings")
    object Background : Screen("background")
    object DataManagement : Screen("data_management")
    object Leaderboard : Screen("leaderboard")
}

@Composable
fun AppNavigation(
    profileViewModel: ProfileViewModel,
    settingsViewModel: SettingsViewModel,
    leaderboardViewModel: LeaderboardViewModel,
    gameViewModel: GameViewModel,
    leaderboardRepository: LeaderboardRepository,
    navController: NavHostController = rememberNavController()
) {
    val currentProfile by profileViewModel.currentProfile.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNewGame = { navController.navigate(Screen.ProfileSetup.route) },
                onResume = { 
                    if (currentProfile != null) {
                        navController.navigate(Screen.Game.route)
                    }
                },
                onSettings = { navController.navigate(Screen.Settings.route) },
                currentProfileName = currentProfile?.displayName,
                onLeaderboard = { navController.navigate(Screen.Leaderboard.route) }
            )
        }
        composable(Screen.ProfileSetup.route) {
            ProfileScreen(profileViewModel) {
                navController.navigate(Screen.Game.route) {
                    popUpTo(Screen.Home.route)
                }
            }
        }
        composable(Screen.Game.route) {
            if (currentProfile == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                GameScreen(
                    settingsViewModel = settingsViewModel,
                    gameViewModel = gameViewModel,
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToLeaderboard = { navController.navigate(Screen.Leaderboard.route) }
                )
            }
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBackground = { navController.navigate(Screen.Background.route) },
                onNavigateToDataManagement = { navController.navigate(Screen.DataManagement.route) }
            )
        }
        composable(Screen.Background.route) {
            BackgroundScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.DataManagement.route) {
            DataManagementScreen(
                profileViewModel = profileViewModel,
                settingsViewModel = settingsViewModel,
                leaderboardRepository = leaderboardRepository,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRoot = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(
                viewModel = leaderboardViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
