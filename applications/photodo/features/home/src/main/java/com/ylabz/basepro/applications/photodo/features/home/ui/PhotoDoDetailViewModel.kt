package com.ylabz.basepro.applications.photodo.features.home.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect // Added import for .collect
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
        val photoIdString: String? = savedStateHandle["photoDoId"]
        if (photoIdString != null) {
            try {
                val photoIdLong = photoIdString.toLong()
                loadTaskDetails(photoIdLong)
            } catch (e: NumberFormatException) {
                _uiState.value = PhotoDoDetailUiState.Error("Invalid Task ID format.")
            }
        } else {
            _uiState.value = PhotoDoDetailUiState.Error("Task ID not provided.") // Changed error message
        }
    }

    private fun loadTaskDetails(taskId: Long) {
        viewModelScope.launch {
            repository.getTaskById(taskId).collect { task -> // Use the new repository method
                if (task != null) {
                    _uiState.value = PhotoDoDetailUiState.Success(task)
                } else {
                    // Provide a more specific error if task is null after successful query by ID
                    _uiState.value = PhotoDoDetailUiState.Error("Task with ID: $taskId not found.")
                }
            }
        }
    }

    fun onEvent(event: PhotoDoDetailEvent) {
        // Handle events like adding or deleting photos here
    }
}
