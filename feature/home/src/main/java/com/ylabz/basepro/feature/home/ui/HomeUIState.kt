package com.ylabz.basepro.feature.home.ui

import com.ylabz.basepro.feature.home.data.AndFrameworks

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val frameworks: List<AndFrameworks>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
