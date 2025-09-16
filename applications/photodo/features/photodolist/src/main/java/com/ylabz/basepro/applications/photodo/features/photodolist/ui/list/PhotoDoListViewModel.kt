package com.ylabz.basepro.applications.photodo.features.photodolist.ui.list


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepository
import com.ylabz.basepro.applications.photodo.db.TaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDoListViewModel @Inject constructor(
    private val repository: PhotoDoRepository
) : ViewModel() {

    private var taskCounter = 0

    val uiState: StateFlow<PhotoDoListUiState> =
        repository.getAllTasks()
            .map { tasks ->
                taskCounter = tasks.size
                PhotoDoListUiState.Success(tasks)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PhotoDoListUiState.Loading
            )

    init {
        viewModelScope.launch {
            if (repository.getAllTasks().first().isEmpty()) {
                for (i in 1..7) {
                    repository.insertTask(TaskEntity(name = "Dummy Task $i"))
                }
            }
        }
    }

    fun onEvent(event: PhotoDoListEvent) {
        when (event) {
            is PhotoDoListEvent.OnItemClick -> {
                // Not implemented yet
            }
            is PhotoDoListEvent.OnAddTaskClicked -> {
                viewModelScope.launch {
                    val newTaskName = "New Task ${taskCounter + 1}"
                    repository.insertTask(TaskEntity(name = newTaskName))
                }
            }
            // Handle the new delete events
            is PhotoDoListEvent.OnDeleteTaskClicked -> {
                viewModelScope.launch {
                    repository.deleteTask(event.task)
                }
            }
            is PhotoDoListEvent.OnDeleteAllTasksClicked -> {
                viewModelScope.launch {
                    repository.deleteAllTasks()
                }
            }
        }
    }
}