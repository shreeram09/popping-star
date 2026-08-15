package com.shreeram.balloonpop.game

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shreeram.balloonpop.leaderboard.LeaderboardEntry
import com.shreeram.balloonpop.leaderboard.LeaderboardRepository
import com.shreeram.balloonpop.profile.ProfileRepository
import com.shreeram.balloonpop.profile.UserProfile
import com.shreeram.balloonpop.storage.DataStoreManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel(
    private val profileRepository: ProfileRepository,
    private val leaderboardRepository: LeaderboardRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel(), DefaultLifecycleObserver {

    val engine = GameEngine()
    
    private val _currentProfile = MutableStateFlow<UserProfile?>(null)
    val currentProfile: StateFlow<UserProfile?> = _currentProfile.asStateFlow()

    init {
        viewModelScope.launch {
            combine(dataStoreManager.currentProfileId, profileRepository.profiles) { id, profiles ->
                profiles.find { it.id == id }
            }.collect {
                _currentProfile.value = it
            }
        }

        // Restore Session
        viewModelScope.launch {
            dataStoreManager.currentProfileId
                .distinctUntilChanged()
                .collectLatest { profileId ->
                    engine.resetGame()
                    if (profileId != null) {
                        val session = dataStoreManager.sessionState(profileId).first()
                        if (session != null) {
                            engine.restoreGame(session.first, session.second, session.third)
                        }
                    }
                }
            }

        // Game Loop
        viewModelScope.launch {
            var lastTime = System.nanoTime()
            var lastSaveTime = 0L
            while (true) {
                if (engine.gameState.value.status == GameStatus.RUNNING) {
                    val currentTime = System.nanoTime()
                    val delta = (currentTime - lastTime) / 1_000_000_000f
                    lastTime = currentTime
                    engine.update(delta)
                    
                    // Periodically save session (every 5 seconds)
                    if (currentTime - lastSaveTime > 5_000_000_000L) {
                        lastSaveTime = currentTime
                        val state = engine.gameState.value
                        _currentProfile.value?.id?.let { profileId ->
                            dataStoreManager.saveSession(profileId, state.score, state.lives, state.elapsedTimeSeconds)
                        }
                    }
                } else {
                    lastTime = System.nanoTime()
                }
                delay(16)
            }
        }

        // Auto-save score on Game Over
        viewModelScope.launch {
            engine.gameState.map { it.status }.distinctUntilChanged().collect { status ->
                if (status == GameStatus.GAME_OVER) {
                    val state = engine.gameState.value
                    val profile = _currentProfile.value
                    if (profile != null) {
                        profileRepository.updateProfileScore(profile.id, state.score, state.elapsedTimeSeconds)
                        leaderboardRepository.addScore(
                            LeaderboardEntry(
                                profileId = profile.id,
                                playerName = profile.displayName,
                                score = state.score
                            )
                        )
                        dataStoreManager.clearSession(profile.id)
                    }
                }
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        engine.pauseGame()
        val state = engine.gameState.value
        if (state.status == GameStatus.PAUSED) {
            _currentProfile.value?.id?.let { profileId ->
                viewModelScope.launch {
                    dataStoreManager.saveSession(profileId, state.score, state.lives, state.elapsedTimeSeconds)
                }
            }
        }
    }

    fun startGame() = engine.startGame()
    fun pauseGame() = engine.pauseGame()
    fun resumeGame() = engine.resumeGame()
    fun onTap(x: Float, y: Float) = engine.onTap(x, y)
}
