package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.entity.PhotoEntity
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

private const val TAG = "PhotoDoDetailViewModel"

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class PhotoDoDetailViewModel @Inject constructor(
    private val photoDoRepo: PhotoDoRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Retrieve the task list ID from navigation arguments
    private val taskListId: Long = 0L
        /*savedStateHandle.get<PhotoDoNavKeys.TaskListDetailKey>("detailKey")?.listId
            ?: throw IllegalArgumentException("listId not found in SavedStateHandle")*/

    private val _listId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PhotoDoDetailUiState> = _listId.flatMapLatest { listId ->
        if (listId == null) {
            MutableStateFlow(PhotoDoDetailUiState.Loading)
        } else {
            val longListId = listId.toLongOrNull() ?: return@flatMapLatest MutableStateFlow(PhotoDoDetailUiState.Loading)
            photoDoRepo.getTaskListWithPhotos(longListId)
                .map { taskListWithPhotos ->
                    if (taskListWithPhotos != null) {
                        PhotoDoDetailUiState.Success(taskListWithPhotos)
                    } else {
                        PhotoDoDetailUiState.Loading
                    }
                }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PhotoDoDetailUiState.Loading
    )

    private val _uiState =
        MutableStateFlow<PhotoDoDetailUiState>(PhotoDoDetailUiState.Loading)
    // val uiState: StateFlow<PhotoDoDetailUiState> = _uiState.asStateFlow()

    init {
        loadTaskListDetails()
    }

    /**
     * Handle events coming from the UI
     */
    fun onEvent(event: PhotoDoDetailEvent) {
        when (event) {
            is PhotoDoDetailEvent.AddPhoto -> {
                viewModelScope.launch {
                    addPhotoToList(event.photoUri)
                }
            }
            // Handle other events
            PhotoDoDetailEvent.OnBackFromCamera -> TODO()
            PhotoDoDetailEvent.OnCameraClick -> TODO()
            is PhotoDoDetailEvent.OnDeletePhoto -> TODO()
            is PhotoDoDetailEvent.OnPhotoSaved -> TODO()
        }
    }

    private fun loadTaskListDetails() {
        viewModelScope.launch {
            Log.d(TAG, "Loading details for listId: $taskListId")
            photoDoRepo.getTaskListWithPhotos(taskListId)
                .catch { exception ->
                    Log.e(TAG, "Error loading task list details", exception)
                    _uiState.value = PhotoDoDetailUiState.Error(
                        exception.message ?: "Unknown error"
                    )
                }
                .collect { taskListWithPhotos ->
                    if (taskListWithPhotos != null) {
                        Log.d(TAG, "Successfully loaded: ${taskListWithPhotos.taskList.name} with ${taskListWithPhotos.photos.size} photos")
                        _uiState.value = PhotoDoDetailUiState.Success(taskListWithPhotos)
                    } else {
                        Log.e(TAG, "Task list with id $taskListId not found")
                        _uiState.value =
                            PhotoDoDetailUiState.Error("Task list not found")
                    }
                }
        }
    }

    /**
     * Creates a new PhotoEntity and inserts it into the database.
     * The StateFlow collecting `getTaskListWithPhotos` will automatically pick up the change.
     */
    private suspend fun addPhotoToList(photoUri: String) {
        try {
            val newPhoto = PhotoEntity(
                listId = taskListId,
                uri = photoUri,
                // You can add more details like timestamp if needed
                // timestamp = System.currentTimeMillis()
            )
            photoDoRepo.insertPhoto(newPhoto)
            Log.d(TAG, "New photo added to list $taskListId: $photoUri")

            // --- Option 1: Rely on the collect to update (current logic) ---
            // The flow in loadTaskListDetails will automatically emit the new list

            // --- Option 2: Manually update the state (if not using a cold flow) ---
            // if (_uiState.value is PhotoDoDetailUiState.Success) {
            //    val currentState = _uiState.value as PhotoDoDetailUiState.Success
            //    val updatedPhotos = currentState.taskListWithPhotos.photos + newPhoto
            //    _uiState.update {
            //        currentState.copy(
            //            taskListWithPhotos = currentState.taskListWithPhotos.copy(
            //                photos = updatedPhotos
            //            )
            //        )
            //    }
            // }

        } catch (e: Exception) {
            Log.e(TAG, "Error saving photo to database", e)
            // Optionally, expose a one-time error to the UI (e.g., via a SharedFlow or Channel)
        }
    }

    fun loadList(id: String) {
        _listId.value = id
    }
}