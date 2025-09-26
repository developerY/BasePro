package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail

import com.ylabz.basepro.applications.photodo.db.entity.TaskListWithPhotos

sealed interface PhotoDoDetailUiState {
    object Loading : PhotoDoDetailUiState
    data class Success(val taskListWithPhotos: TaskListWithPhotos) : PhotoDoDetailUiState
    data class Error(val message: String) : PhotoDoDetailUiState
}
