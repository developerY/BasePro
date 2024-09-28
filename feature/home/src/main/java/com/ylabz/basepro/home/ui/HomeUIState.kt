package com.ylabz.basepro.home.ui

import com.ylabz.basepro.home.data.AndFrameworks

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val frameworks: List<AndFrameworks>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
