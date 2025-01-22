package com.ylabz.basepro.feature.alarm.ui

import com.ylabz.basepro.core.model.shotime.ShotimeSessionData


sealed interface AlarmUiState {
    object Loading : AlarmUiState
    data class Success(val data: List<ShotimeSessionData> = emptyList()) : AlarmUiState
    data class Error(val message: String) : AlarmUiState
}


