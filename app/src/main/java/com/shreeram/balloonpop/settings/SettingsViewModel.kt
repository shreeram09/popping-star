package com.shreeram.balloonpop.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shreeram.balloonpop.storage.DataStoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val settings: StateFlow<AppSettings> = dataStoreManager.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun updateSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.updateSettings { it.copy(soundEnabled = enabled) }
        }
    }

    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            dataStoreManager.updateSettings { it.copy(themeMode = mode) }
        }
    }

    fun updateOrientationMode(mode: OrientationMode) {
        viewModelScope.launch {
            dataStoreManager.updateSettings { it.copy(orientationMode = mode) }
        }
    }

    fun updateBackgroundMode(mode: BackgroundMode) {
        viewModelScope.launch {
            dataStoreManager.updateSettings { it.copy(backgroundMode = mode) }
        }
    }

    fun updateBackgroundColor(color: Int) {
        viewModelScope.launch {
            dataStoreManager.updateSettings { it.copy(backgroundColor = color) }
        }
    }

    fun updateBackgroundImage(uri: String?) {
        viewModelScope.launch {
            dataStoreManager.updateSettings { it.copy(backgroundImageUri = uri) }
        }
    }

    fun updateBackground(mode: BackgroundMode, color: Int? = null, uri: String? = null) {
        viewModelScope.launch {
            dataStoreManager.updateSettings { current ->
                current.copy(
                    backgroundMode = mode,
                    backgroundColor = color ?: current.backgroundColor,
                    backgroundImageUri = uri ?: current.backgroundImageUri
                )
            }
        }
    }
}
