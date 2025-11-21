package com.ylabz.basepro.applications.photodo.features.photodolist.ui.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PhotoDoListViewModel"

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
open class PhotoDoListViewModel @Inject constructor(
    private val photoDoRepo: PhotoDoRepo
) : ViewModel() {

    private val _categoryId = MutableStateFlow<Long?>(null)

    open val uiState: StateFlow<PhotoDoListUiState> = _categoryId.flatMapLatest { categoryId ->
        Log.d(TAG, "flatMapLatest triggered with categoryId: $categoryId")
        if (categoryId == null) {
            Log.d(TAG, "categoryId is null, setting state to Loading")
            MutableStateFlow(PhotoDoListUiState.Loading)
        } else {
            photoDoRepo.getTaskListsForCategory(categoryId)
                .map { taskLists ->
                    Log.d(TAG, "For categoryId $categoryId, received ${taskLists.size} task lists from repo.")
                    if (taskLists.isEmpty()) {
                        Log.w(TAG, "Repo returned an empty list for categoryId $categoryId.")
                    }
                    PhotoDoListUiState.Success(taskLists)
                }
                .catch { e ->
                    Log.e(TAG, "Error fetching task lists for categoryId $categoryId: ${e.message}", e)
                    //emit(PhotoDoListUiState.Error("Error fetching lists: ${e.localizedMessage}"))
                }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PhotoDoListUiState.Loading
    )

    fun loadCategory(id: Long) {
        Log.d(TAG, "loadCategory called with id: $id")
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

            // --- NEW HANDLER ---
            is PhotoDoListEvent.OnDeleteTaskListClicked -> {
                viewModelScope.launch {
                    Log.d("ViewModel", "Deleting TaskList with ID: ${event.listId}")
                    photoDoRepo.deleteTaskListById(event.listId)
                    // Note: The UI updates automatically when the DB flow emits a new list.
                }
            }

            PhotoDoListEvent.OnAddTaskListClicked -> {}//TODO()
            PhotoDoListEvent.OnDeleteAllTaskListsClicked -> {}//TODO()
            is PhotoDoListEvent.OnTaskListClick -> {}//TODO()
        }
    }
}