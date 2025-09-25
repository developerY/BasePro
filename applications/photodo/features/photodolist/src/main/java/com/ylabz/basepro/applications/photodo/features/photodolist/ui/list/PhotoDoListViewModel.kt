package com.ylabz.basepro.applications.photodo.features.photodolist.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class PhotoDoListViewModel @Inject constructor(
    private val photoDoRepo: PhotoDoRepo
    // SavedStateHandle is no longer needed here for projectId
) : ViewModel() {

    private val _projectId = MutableStateFlow<Long?>(null)

    // This flow reactively loads tasks whenever _projectId changes.
    val uiState: StateFlow<PhotoDoListUiState> = _projectId.flatMapLatest { projectId ->
        if (projectId == null) {
            MutableStateFlow(PhotoDoListUiState.Loading) // Or an Empty state
        } else {
            photoDoRepo.getTasksForProject(projectId)
                .map { tasks -> PhotoDoListUiState.Success(tasks) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PhotoDoListUiState.Loading
    )

    /**
     * Sets the project ID to load tasks for. The uiState will automatically update.
     */
    fun loadProject(id: Long) {
        _projectId.value = id
    }

    fun onEvent(event: PhotoDoListEvent) {
        val currentProjectId = _projectId.value ?: return // Don't handle events without a project ID

        when (event) {
            is PhotoDoListEvent.OnDeleteAllTasksClicked -> {
                viewModelScope.launch {
                    // TODO: Implement photoDoRepo.deleteAllTasksForProject(currentProjectId)
                }
            }
            is PhotoDoListEvent.OnAddTaskClicked -> {
                // TODO: Handle Add Task Click
            }
            is PhotoDoListEvent.OnDeleteTaskClicked -> {
                viewModelScope.launch {
                    // TODO: Implement photoDoRepo.deleteTask(event.taskId)
                }
            }
            is PhotoDoListEvent.OnItemClick -> {
                // TODO: Handle Item Click for navigation
            }
        }
    }
}