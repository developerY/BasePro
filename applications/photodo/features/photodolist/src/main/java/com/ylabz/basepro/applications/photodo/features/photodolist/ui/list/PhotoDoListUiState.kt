package com.ylabz.basepro.applications.photodo.features.photodolist.ui.list

import com.ylabz.basepro.applications.photodo.db.TaskEntity

sealed class PhotoDoListUiState {
    object Loading : PhotoDoListUiState()
    data class Success(val photoItems: List<TaskEntity>) : PhotoDoListUiState()
    data class Error(val message: String) : PhotoDoListUiState()
}