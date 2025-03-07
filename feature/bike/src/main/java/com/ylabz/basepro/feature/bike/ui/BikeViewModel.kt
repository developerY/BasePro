package com.ylabz.basepro.feature.bike.ui

import android.util.Log
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
        // Trigger initial load of settings.
        loadSettings()
        // Start collecting location changes and updating the UI state.
        collectLocationUpdates()
        // Start faking speed & distance updates.
        startFakeDataUpdates()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // Simulate loading settings data. In a real app, you'd retrieve these from your repository.
            _uiState.value = BikeUiState.Success(
                settings = mapOf(
                    "Theme" to listOf("Light", "Dark", "System Default"),
                    "Language" to listOf("English", "Spanish", "French"),
                    "Notifications" to listOf("Enabled", "Disabled")
                ),
                location = null,
                // Provide default values for speed/distance if needed.
                currentSpeed = 0.0,
                currentDistance = 0.0,
                totalDistance = 50.0,
                locationString = "Santa Barbara, US"
            )
        }
    }

    private fun collectLocationUpdates() {
        viewModelScope.launch {
            // Optionally trigger an initial fetch
            locationRepository.updateLocation()
            // Collect location updates.
            locationRepository.currentLocation.collect { latLng ->
                val currentState = _uiState.value
                if (currentState is BikeUiState.Success) {
                    // Update the UI state with the new location.
                    _uiState.value = currentState.copy(location = latLng)
                }
            }
        }
    }

    private fun startFakeDataUpdates() {
        viewModelScope.launch {
            var speed = 0.0
            var distance = 0.0
            val totalDist = 50.0  // Total planned distance (km)
            while (true) {
                // Increment speed up to 60 km/h, then reset to 0.
                speed = (speed + 2.0).coerceAtMost(60.0)
                if (speed >= 60.0) speed = 0.0

                // Increment distance up to totalDist, then reset.
                distance = (distance + 0.2).coerceAtMost(totalDist)
                if (distance >= totalDist) distance = 0.0

                val currentState = _uiState.value
                if (currentState is BikeUiState.Success) {
                    _uiState.value = currentState.copy(
                        currentSpeed = speed,
                        currentDistance = distance,
                        totalDistance = totalDist
                    )
                }
                delay(1000L) // Update every second.
            }
        }
    }

    fun onEvent(event: BikeEvent) {
        when (event) {
            is BikeEvent.LoadBike -> loadSettings()
            is BikeEvent.UpdateSetting -> updateSetting(event.settingKey, event.settingValue)
            is BikeEvent.DeleteAllEntries -> deleteAllEntries()
        }
    }

    private fun updateSetting(key: String, value: String) {
        viewModelScope.launch {
            val currentState = _uiState.value as? BikeUiState.Success ?: return@launch
            val updatedSettings = currentState.settings.toMutableMap().apply {
                this[key] = listOf(value)
            }
            _uiState.value = currentState.copy(settings = updatedSettings)
        }
    }

    private fun deleteAllEntries() {
        viewModelScope.launch {
            repository.deleteAll()
            Log.d("BikeViewModel", "All entries deleted from repository.")
        }
    }
}
