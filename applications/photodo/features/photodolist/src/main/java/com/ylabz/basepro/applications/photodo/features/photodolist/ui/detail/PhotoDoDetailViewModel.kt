package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle // Stays for now
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Assuming PhotoDoDetailUiState is defined elsewhere and has Loading, Success, Error states.
// And PhotoDoDetailEvent is also defined.

@HiltViewModel
class PhotoDoDetailViewModel @Inject constructor(
    private val repository: PhotoDoRepository,
    savedStateHandle: SavedStateHandle // Kept in constructor
) : ViewModel() {

    private val _uiState = MutableStateFlow<PhotoDoDetailUiState>(PhotoDoDetailUiState.Loading) // Initial state
    val uiState: StateFlow<PhotoDoDetailUiState> = _uiState

    // Removed the init block that used savedStateHandle["photoId"]

    fun loadPhoto(photoIdString: String?) {
        // This method now takes the photoIdString directly
        _uiState.value = PhotoDoDetailUiState.Loading // Set to loading when this method is called
        Log.d("PhotoDetailVM", "loadPhoto called with photoIdString: '$photoIdString'")
        if (photoIdString != null) {
            try {
                val photoIdLong = photoIdString.toLong()
                Log.d("PhotoDetailVM", "Converted photoIdString '$photoIdString' to Long: $photoIdLong")
                loadTaskDetails(photoIdLong)
            } catch (e: NumberFormatException) {
                Log.e("PhotoDetailVM", "Error converting ID '$photoIdString' to Long in loadPhoto", e)
                _uiState.value = PhotoDoDetailUiState.Error("Invalid Task ID format: $photoIdString")
            }
        } else {
            Log.w("PhotoDetailVM", "Task ID (photoIdString) is null in loadPhoto.")
            _uiState.value = PhotoDoDetailUiState.Error("Task ID not provided to loadPhoto.")
        }
    }

    private fun loadTaskDetails(taskId: Long) {
        Log.d("PhotoDetailVM", "Attempting to load task with ID (Long): $taskId")
        viewModelScope.launch {
            repository.getTaskById(taskId).collect { task ->
                if (task != null) {
                    Log.d("PhotoDetailVM", "Task found: ${task.name}, ID: ${task.id}")
                    _uiState.value = PhotoDoDetailUiState.Success(task)
                } else {
                    Log.w("PhotoDetailVM", "Task with ID: $taskId NOT FOUND in repository")
                    _uiState.value = PhotoDoDetailUiState.Error("Task with ID: $taskId not found.")
                }
            }
        }
    }

    fun onEvent(event: PhotoDoDetailEvent) {
        // Handle events
    }
}
