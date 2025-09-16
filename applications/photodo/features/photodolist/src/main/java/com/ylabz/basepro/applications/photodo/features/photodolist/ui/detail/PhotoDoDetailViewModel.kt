package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail

import android.util.Log // Added this import
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDoDetailViewModel @Inject constructor(
    private val repository: PhotoDoRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<PhotoDoDetailUiState>(PhotoDoDetailUiState.Loading)
    val uiState: StateFlow<PhotoDoDetailUiState> = _uiState

    init {
        val photoIdString: String? = savedStateHandle["photoId"]
        Log.d("PhotoDetailVM", "Retrieved from SavedStateHandle photoDoId: '$photoIdString'")
        if (photoIdString != null) {
            try {
                val photoIdLong = photoIdString.toLong()
                Log.d("PhotoDetailVM", "Converted to Long: $photoIdLong") 
                loadTaskDetails(photoIdLong)
            } catch (e: NumberFormatException) {
                Log.e("PhotoDetailVM", "Error converting ID '$photoIdString' to Long", e) 
                _uiState.value = PhotoDoDetailUiState.Error("Invalid Task ID format: $photoIdString")
            }
        } else {
            Log.w("PhotoDetailVM", "Task ID (photoDoId) not found in SavedStateHandle") 
            _uiState.value = PhotoDoDetailUiState.Error("Task ID not provided.")
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
