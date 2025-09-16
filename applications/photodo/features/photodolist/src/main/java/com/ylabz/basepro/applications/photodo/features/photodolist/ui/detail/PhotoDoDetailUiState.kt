package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail

import com.ylabz.basepro.applications.photodo.db.TaskEntity

sealed class PhotoDoDetailUiState {
    object Loading : PhotoDoDetailUiState()
    data class Success(val task: TaskEntity, val photos: List<Any> = emptyList()) : PhotoDoDetailUiState() // `photos` is a placeholder for your photo model
    data class Error(val message: String) : PhotoDoDetailUiState()
}