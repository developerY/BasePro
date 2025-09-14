package com.ylabz.basepro.applications.photodo.features.photodolist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.TaskEntity
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepository
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

    val uiState: StateFlow<PhotoDoListUiState> =
        repository.getAllTasks()
            .map { tasks -> PhotoDoListUiState.Success(tasks) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PhotoDoListUiState.Loading
            )

    init {
        // Populate with dummy data if the database is empty
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
                // Handle item click logic here
            }
        }
    }
}