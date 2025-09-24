package com.ylabz.basepro.applications.photodo.features.photodolist.ui.list

import com.ylabz.basepro.applications.photodo.db.entity.TaskEntity

sealed interface PhotoDoListUiState {
    object Loading : PhotoDoListUiState
    data class Success(val tasks: List<TaskEntity>) : PhotoDoListUiState
    data class Error(val message: String) : PhotoDoListUiState
}