package com.ylabz.basepro.feature.bike.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.repository.travel.LocationRepository
import com.ylabz.basepro.core.data.repository.travel.SpeedRepository
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
    private val locationRepository: LocationRepository, // The location repo
    private val speedRepository: SpeedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BikeUiState>(BikeUiState.Loading)
    val uiState: StateFlow<BikeUiState> = _uiState

    init {
        // Trigger initial load of settings
        onEvent(BikeEvent.LoadBike)
        // Start collecting location changes
        viewModelScope.launch {
            locationRepository.currentLocation.collect { latLng ->
                val currentState = _uiState.value
                if (currentState is BikeUiState.Success) {
                    _uiState.value = currentState.copy(location = latLng)
                    Log.d("BikeViewModel", "Location updated: $latLng")
                }
            }
        }
        viewModelScope.launch {
            viewModelScope.launch {
                speedRepository.speedFlow.collect { speedKmh ->
                    val currentState = _uiState.value
                    if (currentState is BikeUiState.Success) {
                        _uiState.value = currentState.copy(
                            speedKmh = speedKmh
                        )
                        Log.d("BikeViewModel", "Speed: ], $speedKmh")
                    }
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
            val totalDist = 50.0  // Total planned distance (km)
            while (true) {
                // Increment speed up to 60 km/h, then cycle back to 0
                speed = (speed + 4.0).coerceAtMost(60.0)  // Increased increment
                if (speed >= 60.0) speed = 0.0

                // Increment distance up to totalDist
                distance = (distance + 0.4).coerceAtMost(totalDist)  // Increased increment
                if (distance >= totalDist) distance = 0.0

                val currentState = _uiState.value
                if (currentState is BikeUiState.Success) {
                    _uiState.value = currentState.copy(
                        currentSpeed = speed,
                        currentDistance = distance,
                        totalDistance = totalDist
                    )
                    Log.d("BikeViewModel", "Speed: $speed, Distance: $distance")
                }
                delay(500L) // update every 500ms for smoother motion
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

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = BikeUiState.Success(
                settings = mapOf(
                    "Theme" to listOf("Light", "Dark", "System Default"),
                    "Language" to listOf("English", "Spanish", "French"),
                    "Notifications" to listOf("Enabled", "Disabled")
                ),
                location = null, // will update later
                currentSpeed = 0.0,
                currentDistance = 0.0,
                totalDistance = 50.0,
                locationString = "Santa Barbara, US"
            )
            Log.d("BikeViewModel", "Settings loaded.")
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
