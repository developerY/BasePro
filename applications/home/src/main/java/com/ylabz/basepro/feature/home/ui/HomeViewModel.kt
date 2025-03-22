package com.ylabz.basepro.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.feature.home.data.AndFrameworks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        onEvent(HomeEvent.LoadFrameworks)
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.LoadFrameworks -> loadAndFrameworks()
            HomeEvent.Retry -> loadAndFrameworks()
            is HomeEvent.FrameworkClicked -> {
                // Handle the AndFrameworks click event here if needed
            }
        }
    }

    private fun loadAndFrameworks() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            // delay(2000) // Simulate network delay
            try {
                val AndFrameworks = listOf(
                    AndFrameworks("BLE", "Bluetooth Low Energy"),
                    AndFrameworks("Camera", "Camera functionality"),
                    AndFrameworks("Location", "Location services"),
                    AndFrameworks("Sensors", "Device Sensors"),
                    AndFrameworks("Contacts", "Contacts access")
                )
                _uiState.value = HomeUiState.Success(AndFrameworks)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Failed to load AndFrameworks")
            }
        }
    }
}
