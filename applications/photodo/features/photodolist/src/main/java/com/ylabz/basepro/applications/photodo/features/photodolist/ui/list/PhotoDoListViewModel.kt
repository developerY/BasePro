package com.ylabz.basepro.applications.photodo.features.photodolist.ui.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDoListViewModel @Inject constructor(
    private val photoDoRepo: PhotoDoRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: Long = savedStateHandle.get<Long>("projectId")!!

    val uiState: StateFlow<PhotoDoListUiState> =
        photoDoRepo.getTasksForProject(projectId)
            .map { tasks -> PhotoDoListUiState.Success(tasks) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PhotoDoListUiState.Loading
            )

    fun onEvent(event: PhotoDoListEvent) {
        when (event) {
            is PhotoDoListEvent.OnDeleteAllTasksClicked -> {
                viewModelScope.launch {
                    // TODO: Implement actual deletion logic using photoDoRepo
                    // Example: photoDoRepo.deleteAllTasksForProject(projectId)
                    // photoDoRepo.deleteAllTasksForProject(projectId) // Uncomment and implement
                }
            }
            is PhotoDoListEvent.OnAddTaskClicked -> {
                // TODO: Handle Add Task Click - e.g., trigger navigation to an 'add task' screen or show a dialog.
                // This usually involves sending an event/effect to the UI to handle navigation.
            }
            is PhotoDoListEvent.OnDeleteTaskClicked -> {
                viewModelScope.launch {
                    // TODO: Ensure photoDoRepo has a 'deleteTask' method that accepts a taskId.
                    // photoDoRepo.deleteTask(event.taskId) // Uncomment and implement
                }
            }
            is PhotoDoListEvent.OnItemClick -> {
                // TODO: Handle Item Click - e.g., trigger navigation to task detail screen with event.taskId.
                // This usually involves sending an event/effect to the UI to handle navigation.
            }
        }
    }
}