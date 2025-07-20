package com.rxdigita.basepro.applications.rxtrack.features.medlist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Renamed MedlistUiState to MedListUiState and MedlistEvent to MedListEvent for consistency
sealed interface MedListUiState {
    object Loading : MedListUiState
    data class Error(val message: String) : MedListUiState
    data class Success(val data: String) : MedListUiState
}

@HiltViewModel
class MedListViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<MedListUiState>(MedListUiState.Loading)
    val uiState: StateFlow<MedListUiState> = _uiState

    init {
        // Example: Load initial data
        viewModelScope.launch {
            _uiState.value = MedListUiState.Success("Initial MedList Data")
        }
    }

    fun onEvent(event: MedListEvent) {
        // Handle events
    }
}
