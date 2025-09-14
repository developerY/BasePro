package com.ylabz.basepro.applications.photodo.features.photodolist.ui

sealed class PhotoDoListUiState {
    object Loading : PhotoDoListUiState()
    data class Success(val photoItems: List<String>) : PhotoDoListUiState()
    data class Error(val message: String) : PhotoDoListUiState()
}