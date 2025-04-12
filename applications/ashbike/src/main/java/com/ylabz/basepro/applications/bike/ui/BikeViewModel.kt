package com.ylabz.basepro.applications.bike.ui

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.core.data.repository.travel.CompassRepository
import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationRepository
// import com.ylabz.basepro.core.database.BaseProRepo  // Import your repository
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.CombinedSensorData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BikeViewModel @Inject constructor(
    //private val repository: BaseProRepo,           // Your existing repo
    private val unifiedLocationRepository: UnifiedLocationRepository,
    private val compassRepository : CompassRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BikeUiState>(BikeUiState.Loading)
    val uiState: StateFlow<BikeUiState> = _uiState

    // Store the ride start time.
    private var rideStartTime: Long = 0L
    private val demo_mode = false

    var batteryLevel = 100  // Start at 100%
    val totalDistance = 50.0  // Total planned distance in km


    init {
        // Trigger initial load of settings.
        onEvent(BikeEvent.LoadBike)

        viewModelScope.launch {
            combine(
                unifiedLocationRepository.locationFlow,
                unifiedLocationRepository.speedFlow,
                unifiedLocationRepository.remainingDistanceFlow,
                unifiedLocationRepository.elevationFlow,
                compassRepository.headingFlow
            ) { location, speedKmh, remainingDistance, elevation, heading ->
                CombinedSensorData(location, speedKmh, remainingDistance, heading, elevation)
            }.collect { data ->
                val currentState = _uiState.value
                if (currentState is BikeUiState.Success) {
                    _uiState.value = currentState.copy(
                        bikeData = currentState.bikeData.copy(
                            location = LatLng(data.location.latitude, data.location.longitude),
                            currentSpeed = data.speedKmh.toDouble(),
                            currentTripDistance = (totalDistance - data.remainingDistance).coerceAtLeast(
                                0.0
                            ).toDouble(),
                            elevation = data.elevation.toDouble(),
                            heading = data.heading
                        )
                    )
                    Log.d(
                        "BikeViewModel",
                        "Combined update: location=${data.location}, speed=${data.speedKmh}, remaining=${data.remainingDistance}, heading=${data.heading}, elevation=${data.elevation}"
                    )
                }
            }
        }

        // (Optional) If you want to use fake data instead, comment out the combine block above
        // and use the following fake update loop.
        if(demo_mode) {
            viewModelScope.launch {
                var speed = 0.0
                var traveledDistance = 0.0
                var heading: Float = 0f
                val totalDist = 50.0  // Total planned distance in km

                while (true) {
                    // Update speed: increase by 4 km/h, then reset at 60 km/h
                    speed = (speed + 4.0).coerceAtMost(60.0)
                    if (speed >= 60.0) speed = 0.0

                    // Update traveled distance: increase by 0.4 km, then reset at totalDist
                    traveledDistance = (traveledDistance + 0.4).coerceAtMost(totalDist)
                    if (traveledDistance >= totalDist) traveledDistance = 0.0

                    // Simulate heading changes: increase by 3° each iteration and wrap around at 360
                    heading = ((heading + 1) % 360.0).toFloat()

                    // Simulate battery drain: decrement batteryLevel, then reset to 100 if it goes below 0
                    batteryLevel -= 1
                    if (batteryLevel < 0) {
                        batteryLevel = 100
                    }

                    val currentState = _uiState.value
                    if (currentState is BikeUiState.Success) {
                        // Update the nested bikeData immutably using copy
                        _uiState.value = currentState.copy(
                            bikeData = currentState.bikeData.copy(
                                currentSpeed = speed,
                                currentTripDistance = traveledDistance,
                                totalDistance = totalDist,
                                heading = heading,
                                batteryLevel = batteryLevel
                            )
                        )
                        Log.d(
                            "BikeViewModel",
                            "Fake update: speed=$speed, traveledDistance=$traveledDistance, heading=$heading"
                        )
                    }
                    delay(500L) // update every 500ms
                }
            }
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

    // Format milliseconds to a string like "1h 30m"
    private fun formatDurationToHM(millis: Long): String {
        val totalMinutes = millis / 1000 / 60
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return if (hours > 0) {
            "${hours}h ${minutes}m"
        } else {
            "${minutes}m"
        }
    }

    private fun startRideDurationUpdates() {
        rideStartTime = System.currentTimeMillis() // Record ride start time.
        viewModelScope.launch {
            while (true) {
                val elapsedMillis = System.currentTimeMillis() - rideStartTime
                val formatted = formatDurationToHM(elapsedMillis)
                val currentState = _uiState.value

                if (currentState is BikeUiState.Success) {
                    // Update the nested bikeData immutably using copy
                    _uiState.value = currentState.copy(
                        bikeData = currentState.bikeData.copy(
                            rideDuration = formatted
                        )
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
            is BikeEvent.Connect -> bikeConnect()
        }
    }

    private fun bikeConnect() {
        // Implement your bike connection logic here
        viewModelScope.launch {


                val currentState = _uiState.value

                if (currentState is BikeUiState.Success) {

                        // Update the nested bikeData immutably using copy
                        _uiState.value = currentState.copy(
                            bikeData = currentState.bikeData.copy(
                                isBikeConnected = true,
                            )
                        )

                }

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
                bikeData = BikeRideInfo(
                    isBikeConnected = false,
                    location = LatLng(37.4219999, -122.0862462),
                    currentSpeed = 55.0,
                    currentTripDistance = 5.0,
                    totalDistance = 100.0,
                    rideDuration = "00:15:00",
                    settings = mapOf("Theme" to listOf("Light", "Dark", "System Default"),
                        "Language" to listOf("English", "Spanish", "French"),
                        "Notifications" to listOf("Enabled", "Disabled")),
                    averageSpeed = 12.0,
                    elevation = 12.0,
                    heading = 12.0f,
                    batteryLevel = 12,
                    motorPower = 12.0f
                )
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
        /*viewModelScope.launch {
            repository.deleteAll()
            Log.d("BikeViewModel", "All entries deleted from repository.")
        }*/
    }
}
