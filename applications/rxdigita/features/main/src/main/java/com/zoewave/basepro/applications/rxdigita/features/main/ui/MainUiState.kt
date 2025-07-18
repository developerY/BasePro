package com.zoewave.basepro.applications.rxdigita.features.main.ui

sealed interface MainUiState {
    object Loading : MainUiState
    data class Error(val message: String) : MainUiState
    data class Success(val data: String) : MainUiState
}