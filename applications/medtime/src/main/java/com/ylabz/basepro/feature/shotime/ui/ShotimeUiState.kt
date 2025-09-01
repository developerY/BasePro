package com.ylabz.basepro.feature.shotime.ui

import com.ylabz.basepro.core.model.shotime.ShotimeSessionData


sealed interface ShotimeUiState {
    object Loading : ShotimeUiState
    data class Success(val data: List<ShotimeSessionData> = emptyList()) : ShotimeUiState
    data class Error(val message: String) : ShotimeUiState
}


