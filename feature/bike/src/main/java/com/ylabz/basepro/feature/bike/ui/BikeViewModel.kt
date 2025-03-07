package com.ylabz.basepro.feature.bike.ui

import android.util.Log
import androidx.core.util.TimeUtils.formatDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.repository.travel.DistanceRepository
import com.ylabz.basepro.core.data.repository.travel.LocationRepository
import com.ylabz.basepro.core.data.repository.travel.SpeedRepository
import com.ylabz.basepro.core.database.BaseProRepo  // Import your repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BikeViewModel @Inject constructor(
    private val repository: BaseProRepo,           // Your existing repo
    private val locationRepository: LocationRepository, // The location repo
    private val speedRepository: SpeedRepository,
    private val distanceRepository: DistanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BikeUiState>(BikeUiState.Loading)
    val uiState: StateFlow<BikeUiState> = _uiState

    // Store the ride start time.
    private var rideStartTime: Long = 0L

    init {
        // Trigger initial load of settings.
        onEvent(BikeEvent.LoadBike)

        /* Combine location, speed, and distance flows.
        viewModelScope.launch {
            combine(
                locationRepository.currentLocation,
                speedRepository.speedFlow,
                distanceRepository.remainingDistanceFlow
            ) { location, speedKmh, remainingDistance ->
                Triple(location, speedKmh, remainingDistance)
            }.collect { (location, speedKmh, remainingDistance) ->
                val currentState = _uiState.value
                if (currentState is BikeUiState.Success) {
                    // Assume totalDistance is set in the UI state.
                    // Calculate traveled distance as: totalDistance - remainingDistance.
                    val traveledDistance = currentState.totalDistance - remainingDistance.toDouble()
                    _uiState.value = currentState.copy(
                        location = location,
                        currentSpeed = speedKmh.toDouble(),
                        currentDistance = traveledDistance
                    )
                    Log.d(
                        "BikeViewModel",
                        "Combined update: location=$location, speed=$speedKmh, remaining=$remainingDistance"
                    )
                }
            }
        }*/

        // (Optional) If you want to use fake data instead, comment out the combine block above
        // and use the following fake update loop.
        viewModelScope.launch {
            var speed = 0.0
            var traveledDistance = 0.0
            val totalDist = 50.0  // Total planned distance (km)
            while (true) {
                speed = (speed + 4.0).coerceAtMost(60.0)
                if (speed >= 60.0) speed = 0.0
                traveledDistance = (traveledDistance + 0.4).coerceAtMost(totalDist)
                if (traveledDistance >= totalDist) traveledDistance = 0.0

                val currentState = _uiState.value
                if (currentState is BikeUiState.Success) {
                    _uiState.value = currentState.copy(
                        currentSpeed = speed,
                        currentDistance = traveledDistance,
                        totalDistance = totalDist
                    )
                    Log.d("BikeViewModel", "Fake update: speed=$speed, traveledDistance=$traveledDistance")
                }
                delay(500L)
            }
        }

        // Optionally, trigger an initial location fetch.
        viewModelScope.launch {
            locationRepository.updateLocation()
        }

        // Start ride duration updates.
        startRideDurationUpdates()

    }

    // Helper to format milliseconds as HH:MM:SS
    private fun formatDuration(millis: Long): String {
        val seconds = millis / 1000 % 60
        val minutes = millis / (1000 * 60) % 60
        val hours = millis / (1000 * 60 * 60)
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun startRideDurationUpdates() {
        rideStartTime = System.currentTimeMillis() // Record ride start time.
        viewModelScope.launch {
            while (true) {
                val elapsedMillis = System.currentTimeMillis() - rideStartTime
                val formatted = formatDuration(elapsedMillis)
                val currentState = _uiState.value
                if (currentState is BikeUiState.Success) {
                    _uiState.value = currentState.copy(rideDuration = formatted)
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

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = BikeUiState.Success(
                settings = mapOf(
                    "Theme" to listOf("Light", "Dark", "System Default"),
                    "Language" to listOf("English", "Spanish", "French"),
                    "Notifications" to listOf("Enabled", "Disabled")
                ),
                location = null,
                currentSpeed = 0.0,
                currentDistance = 0.0,
                totalDistance = 50.0,
                locationString = "Santa Barbara, US",
                rideDuration = "00:00:00"
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
