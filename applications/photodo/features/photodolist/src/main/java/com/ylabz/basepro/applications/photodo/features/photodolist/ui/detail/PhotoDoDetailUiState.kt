package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail

import com.ylabz.basepro.applications.photodo.db.entity.TaskWithPhotos

sealed interface PhotoDoDetailUiState {
    object Loading : PhotoDoDetailUiState
    data class Success(val taskWithPhotos: TaskWithPhotos) : PhotoDoDetailUiState
    data class Error(val message: String) : PhotoDoDetailUiState
}
