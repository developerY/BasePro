package com.ylabz.basepro.feature.alarm.ui

import com.ylabz.basepro.core.model.shotime.ShotimeSessionData


sealed interface AlarmUiState {
    object Loading : AlarmUiState
    object Empty : AlarmUiState // Represents an empty state when no alarms are available
    data class Success(val data: List<ShotimeSessionData>) : AlarmUiState
    data class Error(val message: String) : AlarmUiState
}

