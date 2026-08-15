package com.shreeram.balloonpop

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shreeram.balloonpop.game.GameViewModel
import com.shreeram.balloonpop.leaderboard.LeaderboardRepository
import com.shreeram.balloonpop.leaderboard.LeaderboardViewModel
import com.shreeram.balloonpop.profile.ProfileRepository
import com.shreeram.balloonpop.profile.ProfileViewModel
import com.shreeram.balloonpop.settings.OrientationMode
import com.shreeram.balloonpop.settings.SettingsViewModel
import com.shreeram.balloonpop.storage.AppDatabase
import com.shreeram.balloonpop.storage.DataStoreManager
import com.shreeram.balloonpop.ui.AppNavigation
import com.shreeram.balloonpop.ui.theme.BalloonPopTheme

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
private val Context.profileDataStore: DataStore<Preferences> by preferencesDataStore(name = "profiles")

class MainActivity : ComponentActivity() {
    
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var profileRepository: ProfileRepository
    private lateinit var leaderboardRepository: LeaderboardRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(applicationContext)
        dataStoreManager = DataStoreManager(settingsDataStore)
        profileRepository = ProfileRepository(profileDataStore)
        leaderboardRepository = LeaderboardRepository(database.leaderboardDao())

        val profileViewModel by viewModels<ProfileViewModel> {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(profileRepository, dataStoreManager) as T
                }
            }
        }

        val settingsViewModel by viewModels<SettingsViewModel> {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(dataStoreManager) as T
                }
            }
        }

        val leaderboardViewModel by viewModels<LeaderboardViewModel> {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LeaderboardViewModel(leaderboardRepository) as T
                }
            }
        }

        val gameViewModel by viewModels<GameViewModel> {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GameViewModel(
                        profileRepository,
                        leaderboardRepository,
                        dataStoreManager
                    ) as T
                }
            }
        }

        lifecycle.addObserver(gameViewModel)

        enableEdgeToEdge()
        setContent {
            val settings by settingsViewModel.settings.collectAsState()
            
            LaunchedEffect(settings.orientationMode) {
                requestedOrientation = when (settings.orientationMode) {
                    OrientationMode.SYSTEM -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    OrientationMode.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    OrientationMode.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }

            BalloonPopTheme(themeMode = settings.themeMode) {
                AppNavigation(
                    profileViewModel = profileViewModel,
                    settingsViewModel = settingsViewModel,
                    leaderboardViewModel = leaderboardViewModel,
                    gameViewModel = gameViewModel,
                    leaderboardRepository = leaderboardRepository
                )
            }
        }
    }
}
