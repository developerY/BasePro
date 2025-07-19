package com.zoewave.basepro.applications.rxdigita.features.main.ui

sealed interface MedUiState {
    object Loading : MedUiState
    data class Error(val message: String) : MedUiState
    data class Success(val data: String) : MedUiState
}