package com.shreeram.balloonpop.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shreeram.balloonpop.storage.DataStoreManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val profiles: StateFlow<List<UserProfile>> = repository.profiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentProfileId: StateFlow<String?> = dataStoreManager.currentProfileId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentProfile: StateFlow<UserProfile?> = combine(profiles, currentProfileId) { profiles, id ->
        profiles.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun createProfile(name: String, onResult: (UserProfile?) -> Unit) {
        viewModelScope.launch {
            val profile = repository.addProfile(name)
            if (profile != null) {
                dataStoreManager.setCurrentProfileId(profile.id)
            }
            onResult(profile)
        }
    }

    fun selectProfile(id: String) {
        viewModelScope.launch {
            dataStoreManager.setCurrentProfileId(id)
        }
    }

    fun deleteProfile(id: String) {
        viewModelScope.launch {
            if (currentProfileId.value == id) {
                dataStoreManager.setCurrentProfileId(null)
            }
            dataStoreManager.clearSession(id)
            repository.deleteProfile(id)
        }
    }

    fun clearCurrentSession() {
        currentProfileId.value?.let { profileId ->
            viewModelScope.launch {
                dataStoreManager.clearSession(profileId)
            }
        }
    }

    fun clearAllProfiles() {
        viewModelScope.launch {
            dataStoreManager.setCurrentProfileId(null)
            repository.clearAllData()
        }
    }

    fun updateScore(id: String, score: Int, duration: Float) {
        viewModelScope.launch {
            repository.updateProfileScore(id, score, duration)
        }
    }
}
