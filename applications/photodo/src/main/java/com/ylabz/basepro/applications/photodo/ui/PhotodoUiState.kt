package com.ylabz.basepro.applications.photodo.ui

import com.ylabz.basepro.core.model.shotime.ShotimeSessionData


sealed interface PhotodoUiState {
    object Loading : PhotodoUiState
    data class Success(val data: List<ShotimeSessionData> = emptyList()) : PhotodoUiState
    data class Error(val message: String) : PhotodoUiState
}


