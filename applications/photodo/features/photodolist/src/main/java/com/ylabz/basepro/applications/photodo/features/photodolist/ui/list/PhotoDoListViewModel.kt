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
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class PhotoDoListViewModel @Inject constructor(
    private val photoDoRepo: PhotoDoRepo
) : ViewModel() {

    private val _categoryId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<PhotoDoListUiState> = _categoryId.flatMapLatest { categoryId ->
        if (categoryId == null) {
            MutableStateFlow(PhotoDoListUiState.Loading)
        } else {
            photoDoRepo.getTaskListsForCategory(categoryId)
                .map { taskLists -> PhotoDoListUiState.Success(taskLists) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PhotoDoListUiState.Loading
    )

    fun loadCategory(id: Long) {
        _categoryId.value = id
    }

    fun onEvent(event: PhotoDoListEvent) {
        val currentCategoryId = _categoryId.value ?: return

        when (event) {
            /*is PhotoDoListEvent.OnDeleteAllTasksClicked -> {
                viewModelScope.launch {
                    // TODO: Implement photoDoRepo.deleteAllTaskListsForCategory(currentCategoryId)
                }
            }
            is PhotoDoListEvent.OnAddTaskClicked -> {
                viewModelScope.launch {
                    val newTaskList = TaskListEntity(
                        categoryId = currentCategoryId,
                        name = "New List ${System.currentTimeMillis() % 1000}",
                        notes = "Tap to edit details"
                    )
                    photoDoRepo.insertTaskList(newTaskList)
                }
            }
            is PhotoDoListEvent.OnDeleteTaskClicked -> {
                // TODO: Handle delete single task list
            }*/

            PhotoDoListEvent.OnAddTaskListClicked -> {}//TODO()
            PhotoDoListEvent.OnDeleteAllTaskListsClicked -> {}//TODO()
            is PhotoDoListEvent.OnDeleteTaskListClicked -> {}//TODO()
            is PhotoDoListEvent.OnTaskListClick -> {}//TODO()
        }
    }
}
