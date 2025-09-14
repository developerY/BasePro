package com.ylabz.basepro.applications.photodo.features.photodolist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDoListViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<PhotoDoListUiState>(PhotoDoListUiState.Loading)
    val uiState: StateFlow<PhotoDoListUiState> = _uiState

    init {
        // Simulate loading data
        viewModelScope.launch {
            // In a real app, you would fetch this from a repository
            val photoItems = List(20) { "Photo Item ${it + 1}" }
            _uiState.value = PhotoDoListUiState.Success(photoItems)
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