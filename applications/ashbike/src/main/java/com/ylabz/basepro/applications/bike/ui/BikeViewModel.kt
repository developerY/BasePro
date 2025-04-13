package com.ylabz.basepro.applications.bike.ui

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.core.data.repository.travel.compass.CompassRepository
import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationRepository
// import com.ylabz.basepro.core.database.BaseProRepo  // Import your repository
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.CombinedSensorData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class BikeViewModel @Inject constructor(
    @Named("real") private val realLocationRepository: UnifiedLocationRepository,
    @Named("real") private val realCompassRepository: CompassRepository,  // Assuming similar setup for compass
    @Named("demo") private val demoLocationRepository: UnifiedLocationRepository,
    @Named("demo") private val demoCompassRepository: CompassRepository
) : ViewModel() {

    // Set this flag according to your needs (it could be from remote config, build config, etc.)
    private val realMode = true

    // Choose the proper repository based on the demo flag
    private val unifiedLocationRepository: UnifiedLocationRepository =
        if (realMode) realLocationRepository else demoLocationRepository

    private val compassRepository: CompassRepository =
        if (realMode) realCompassRepository else demoCompassRepository


    private val _uiState = MutableStateFlow<BikeUiState>(BikeUiState.Loading)
    val uiState: StateFlow<BikeUiState> = _uiState

    // Store the ride start time.
    private var rideStartTime: Long = 0L

    // Starting battery level, total planned distance (km)
    var batteryLevel = 100
    // LiveData or StateFlow representing the total distance from the user
    // MutableStateFlow to hold an optional total route distance (in kilometers).
    // Null indicates that the rider did not set a total distance.
    private val _totalRouteDistance = MutableStateFlow<Float?>(null)
    val totalRouteDistance: StateFlow<Float?> = _totalRouteDistance

    // Expose traveled distance directly from the repository.
    val traveledDistanceFlow: Flow<Float> = unifiedLocationRepository.traveledDistanceFlow

    // Calculate remaining distance only if a total route distance has been provided.
    // If not provided (null), the remainingDistanceFlow will also emit null.
    val remainingDistanceFlow: Flow<Float?> = combine(
        traveledDistanceFlow,
        totalRouteDistance
    ) { traveled, total ->
        total?.let { (it - traveled).coerceAtLeast(0f) }
    }

    init {
        // Trigger initial load of settings.
        onEvent(BikeEvent.LoadBike)

        // Subscribe to real sensor data updates using combine()
        viewModelScope.launch {
            combine(
                unifiedLocationRepository.locationFlow,
                unifiedLocationRepository.speedFlow,
                traveledDistanceFlow,
                totalRouteDistance,
                remainingDistanceFlow,
                unifiedLocationRepository.elevationFlow,
                compassRepository.headingFlow
            ) { values -> // Cast each element from the array
                val location = values[0] as Location
                val speedKmh = values[1] as Float
                val traveledDistance = values[2] as Float
                val totalDistance = values[3] as Float?  // Optional value
                val remainingDistance = values[4] as Float?  // Optional value
                val elevation = values[5] as Float
                val heading = values[6] as Float
                CombinedSensorData(
                    location = location,
                    speedKmh = speedKmh,
                    traveledDistance = traveledDistance,
                    totalDistance = totalDistance,
                    remainingDistance = remainingDistance,
                    elevation = elevation,
                    heading = heading
                )
            }.collect { data ->
                val currentState = _uiState.value
                if (currentState is BikeUiState.Success) {

                    val elapsedMillis = System.currentTimeMillis() - rideStartTime
                    val elapsedHours = elapsedMillis / 3600000.0
                    // Compute average speed in km/h.
                    val averageSpeed = if (elapsedHours > 0) data.traveledDistance / elapsedHours else 0.0

                    _uiState.value = currentState.copy(
                        bikeData = currentState.bikeData.copy(
                            location = LatLng(data.location.latitude, data.location.longitude),
                            currentSpeed = data.speedKmh.toDouble(),
                            averageSpeed = averageSpeed,
                            // Calculate traveled distance: planned totalDistance minus remaining distance,
                            // ensuring the value is not negative.
                            currentTripDistance = data.traveledDistance.coerceAtLeast(0f),
                            totalTripDistance = data.totalDistance,
                            remainingDistance = data.remainingDistance,
                            elevation = data.elevation.toDouble(),
                            heading = data.heading,
                            // Battery can be updated elsewhere (e.g., via connectivity events)
                            batteryLevel = batteryLevel
                        )
                    )
                    Log.d(
                        "BikeViewModel",
                        "Combined update: location=${data.location}, speed=${data.speedKmh}, remaining=${data.remainingDistance}, heading=${data.heading}, elevation=${data.elevation}"
                    )
                }
            }
        }

        // Start the ride duration updates.
        startRideDurationUpdates()
    }

    // Helper to format milliseconds as "1h 30m"
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
                    // Update the nested bikeData with the rideDuration.
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

    // Event handling.
    fun onEvent(event: BikeEvent) {
        when (event) {
            is BikeEvent.LoadBike -> loadSettings()
            is BikeEvent.UpdateSetting -> updateSetting(event.settingKey, event.settingValue)
            is BikeEvent.DeleteAllEntries -> deleteAllEntries()
            is BikeEvent.Connect -> bikeConnect()
        }
    }

    private fun bikeConnect() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is BikeUiState.Success) {
                _uiState.value = currentState.copy(
                    bikeData = currentState.bikeData.copy(
                        isBikeConnected = true
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
                    currentSpeed = 0.0,
                    currentTripDistance = 0.0f,
                    totalTripDistance = null,
                    remainingDistance = null,
                    rideDuration = "00:00",
                    settings = mapOf(
                        "Theme" to listOf("Light", "Dark", "System Default"),
                        "Language" to listOf("English", "Spanish", "French"),
                        "Notifications" to listOf("Enabled", "Disabled")
                    ),
                    averageSpeed = 0.0,
                    elevation = 0.0,
                    heading = 0.0f,
                    batteryLevel = batteryLevel,
                    motorPower = null
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
        // Implement data deletion logic if needed.
    }
}
