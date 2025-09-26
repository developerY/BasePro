package com.ylabz.basepro.applications.photodo.features.photodolist.ui.list

import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity

sealed interface PhotoDoListUiState {
    object Loading : PhotoDoListUiState
    data class Success(val taskLists: List<TaskListEntity>) : PhotoDoListUiState
    data class Error(val message: String) : PhotoDoListUiState
}
