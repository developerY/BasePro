package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.entity.PhotoEntity
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PhotoDoDetailViewModel"

@HiltViewModel
class PhotoDoDetailViewModel @Inject constructor(
    private val photoDoRepo: PhotoDoRepo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhotoDoDetailUiState())
    val uiState: StateFlow<PhotoDoDetailUiState> = _uiState.asStateFlow()

    private var listId: Long = 0L

    init {
        Log.d(TAG, "ViewModel initialized. Waiting for loadTaskDetails().")
    }

    /**
     * Loads the task details. This MUST be called from the UI (Composable)
     * with the listId from the navigation arguments.
     */
    fun loadTaskDetails(id: Long) {
        if (id == 0L) {
            Log.e(TAG, "Invalid listId (0), cannot load details.")
            _uiState.update { it.copy(loadState = DetailLoadState.Error("Invalid Task ID")) }
            return
        }
        if (id == this.listId) {
            Log.d(TAG, "Details for listId $id already loaded or loading.")
            return
        }

        Log.d(TAG, "Loading details for listId: $id")
        this.listId = id
        _uiState.update { it.copy(loadState = DetailLoadState.Loading) }

        viewModelScope.launch {
            photoDoRepo.getTaskListWithPhotos(id)
                .catch { e ->
                    Log.e(TAG, "Error loading task details", e)
                    _uiState.update { it.copy(loadState = DetailLoadState.Error(e.message ?: "Unknown error")) }
                }
                .collect { taskListWithPhotos ->
                    if (taskListWithPhotos != null) {
                        // This is now much simpler.
                        // We just pass the object from the database directly to the Success state.
                        _uiState.update {
                            it.copy(loadState = DetailLoadState.Success(taskListWithPhotos))
                        }
                    } else {
                        _uiState.update {
                            it.copy(loadState = DetailLoadState.Error("Task not found"))
                        }
                    }
                }
        }
    }

    fun onEvent(event: PhotoDoDetailEvent) {
        when (event) {
            is PhotoDoDetailEvent.OnPhotoSaved -> {
                if (listId == 0L) {
                    Log.e(TAG, "Cannot save photo, listId is 0.")
                    return
                }
                viewModelScope.launch {
                    try {
                        val newPhoto = PhotoEntity(
                            uri = event.uri.toString(),
                            listId = listId
                        )
                        photoDoRepo.insertPhoto(newPhoto)
                        Log.d(TAG, "Photo saved to database: ${event.uri}")
                        _uiState.update { it.copy(showCamera = false) } // Hide camera
                    } catch (e: Exception) {
                        Log.e(TAG, "Error saving photo to database", e)
                    }
                }
            }
            is PhotoDoDetailEvent.OnDeletePhoto -> {
                viewModelScope.launch {
                    val currentLoadState = _uiState.value.loadState
                    if (currentLoadState is DetailLoadState.Success) {
                        try {
                            // Find the photo from the list in the Success state
                            val photoToDelete = currentLoadState.taskListWithPhotos.photos.find { it.photoId == event.photoId }
                            if (photoToDelete != null) {
                                photoDoRepo.deletePhoto(photoToDelete)
                            } else {
                                Log.w(TAG, "Could not find photo with id ${event.photoId} to delete.")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error deleting photo", e)
                        }
                    }
                }
            }
            PhotoDoDetailEvent.OnCameraClick -> {
                _uiState.update { it.copy(showCamera = true) }
            }
            PhotoDoDetailEvent.OnBackFromCamera -> {
                _uiState.update { it.copy(showCamera = false) }
            }
        }
    }
}