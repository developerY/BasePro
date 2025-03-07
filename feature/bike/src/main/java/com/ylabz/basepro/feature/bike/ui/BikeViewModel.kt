package com.ylabz.basepro.feature.bike.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.repository.location.LocationRepository
import com.ylabz.basepro.core.database.BaseProRepo  // Import your repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BikeViewModel @Inject constructor(
    private val repository: BaseProRepo,           // Your existing repo
    private val locationRepository: LocationRepository // The location repo
) : ViewModel() {

    private val _uiState = MutableStateFlow<BikeUiState>(BikeUiState.Loading)
    val uiState: StateFlow<BikeUiState> = _uiState

    init {
        // Trigger initial load of settings
        onEvent(BikeEvent.LoadBike)

        // Start collecting location changes
        viewModelScope.launch {
            locationRepository.currentLocation.collect { latLng ->
                // Only update if we're in the Success state
                val currentState = _uiState.value
                if (currentState is BikeUiState.Success) {
                    // Copy the existing settings, update the location
                    _uiState.value = currentState.copy(location = latLng)
                }
            }
        }

        // Optionally, trigger an initial location fetch
        viewModelScope.launch {
            locationRepository.updateLocation()

        }
        // Fake data: increment speed & distance in a loop
        viewModelScope.launch {
            var speed = 0.0
            var distance = 0.0
            val totalDist = 50.0  // Suppose total planned distance is 50 km

            while (true) {
                // Increment speed up to 60 km/h, then cycle back to 0
                speed = (speed + 2.0).coerceAtMost(60.0)
                if (speed >= 60.0) {
                    speed = 0.0
                }

                // Increment distance up to totalDist
                distance = (distance + 0.2).coerceAtMost(totalDist)
                // If we reach the total distance, reset to see the progress line start over
                if (distance >= totalDist) {
                    distance = 0.0
                }

                // Update UI state
                val currentState = _uiState.value
                if (currentState is BikeUiState.Success) {
                    // Copy the existing settings, update the location
                    _uiState.value = currentState.copy(
                        currentSpeed = speed,
                        currentDistance = distance,
                        totalDistance = totalDist
                    )
                }
                delay(1000) // update every second
            }
        }

    }

    fun onEvent(event: BikeEvent) {
        when (event) {
            is BikeEvent.LoadBike -> {
                loadSettings()
            }
            is BikeEvent.UpdateSetting -> {
                updateSetting(event.settingKey, event.settingValue)
            }
            is BikeEvent.DeleteAllEntries -> {
                deleteAllEntries()
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // Simulate loading settings data
            // In a real app, you'd retrieve these from your repository
            _uiState.value = BikeUiState.Success(
                settings = mapOf(
                    "Theme" to listOf("Light", "Dark", "System Default"),
                    "Language" to listOf("English", "Spanish", "French"),
                    "Notifications" to listOf("Enabled", "Disabled")
                ),
                location = null // We'll update this once location flow emits
            )
        }
    }

    private fun updateSetting(key: String, value: String) {
        viewModelScope.launch {
            val currentSettings = (_uiState.value as? BikeUiState.Success)?.settings ?: emptyMap()
            val updatedSettings = currentSettings.toMutableMap().apply {
                // e.g., handle your setting changes here
            }
            _uiState.value = BikeUiState.Success(
                settings = updatedSettings,
                location = (_uiState.value as? BikeUiState.Success)?.location
            )
        }
    }

    private fun deleteAllEntries() {
        viewModelScope.launch {
            repository.deleteAll()
            // Optionally, update UI state or reload settings
        }
    }
}
