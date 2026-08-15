package com.shreeram.balloonpop.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class LeaderboardViewModel(private val repository: LeaderboardRepository) : ViewModel() {
    val topScores: StateFlow<List<LeaderboardEntry>> = repository.topScores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
