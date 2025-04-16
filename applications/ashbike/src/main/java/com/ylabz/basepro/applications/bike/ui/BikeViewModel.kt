package com.ylabz.basepro.applications.bike.ui

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.core.data.repository.bikeConnectivity.BikeConnectivityRepository
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class BikeViewModel @Inject constructor(
    // Real implementations
    @Named("real") private val realConnectivityRepository: BikeConnectivityRepository,
    @Named("real") private val realLocationRepository: UnifiedLocationRepository,
    @Named("real") private val realCompassRepository: CompassRepository,

    // Demo implementations
    @Named("demo") private val demoConnectivityRepository: BikeConnectivityRepository,
    @Named("demo") private val demoLocationRepository: UnifiedLocationRepository,
    @Named("demo") private val demoCompassRepository: CompassRepository
) : ViewModel() {

    // Toggle between real and demo mode.
    private val realMode = true

    // Choose the proper repository based on the mode.
    private val unifiedLocationRepository = if (realMode) realLocationRepository else demoLocationRepository
    private val compassRepository = if (realMode) realCompassRepository else demoCompassRepository
    private val connectivityRepository = if (realMode) realConnectivityRepository else demoConnectivityRepository


    // UI State for the bike ride.
    private val _uiState = MutableStateFlow<BikeUiState>(BikeUiState.Loading)
    val uiState: StateFlow<BikeUiState> = _uiState

    // Ride start time to compute ride duration and average speed.
    private var rideStartTime: Long = 0L

    // Bike-specific connectivity data.
    // Both are now initialized as null so that if there is no eBike, these remain null.
    private var bikeBatteryLevel: Int? = null
    private var bikeMotorPower: Float? = null

    // User-provided total route distance (optional).
    private val _totalRouteDistance = MutableStateFlow<Float?>(null)
    val totalRouteDistance: StateFlow<Float?> = _totalRouteDistance

    // Expose traveled distance from the repository.
    val traveledDistanceFlow: Flow<Float> = unifiedLocationRepository.traveledDistanceFlow

    // Compute the remaining distance, if total route distance is provided.
    val remainingDistanceFlow: Flow<Float?> = combine(
        traveledDistanceFlow,
        totalRouteDistance
    ) { traveled, total -> total?.let { (it - traveled).coerceAtLeast(0f) } }

    // Combined sensor data flow.
    private val sensorDataFlow: Flow<CombinedSensorData> = combine(
        unifiedLocationRepository.locationFlow,
        unifiedLocationRepository.speedFlow,
        traveledDistanceFlow,
        totalRouteDistance,
        remainingDistanceFlow,
        unifiedLocationRepository.elevationFlow,
        compassRepository.headingFlow
    ) { values ->
        // The vararg combine returns an Array<Any?> so we cast each value.
        val location = values[0] as Location
        val speedKmh = values[1] as Float
        val traveledDistance = values[2] as Float
        val totalDistance = values[3] as Float?
        val remainingDistance = values[4] as Float?
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
    }

    init {
        // Initialize settings.
        onEvent(BikeEvent.LoadBike)

        // Start ride duration updates and record ride start time.
        startRideDurationUpdates()

        // Process sensor data updates.
        viewModelScope.launch {
            sensorDataFlow.collect { data ->
                // Calculate elapsed time in hours.
                val elapsedMillis = System.currentTimeMillis() - rideStartTime
                val elapsedHours = elapsedMillis / 3600000.0
                // Compute average speed (km/h): traveled distance divided by time.
                val averageSpeed = if (elapsedHours > 0) data.traveledDistance / elapsedHours else 0.0

                // If the current UI state is already Success, update the bikeData.
                val currentState = _uiState.value
                if (currentState is BikeUiState.Success) {
                    _uiState.value = currentState.copy(
                        bikeData = currentState.bikeData.copy(
                            location = LatLng(data.location.latitude, data.location.longitude),
                            currentSpeed = data.speedKmh.toDouble(),
                            averageSpeed = averageSpeed,
                            currentTripDistance = data.traveledDistance.coerceAtLeast(0f),
                            totalTripDistance = data.totalDistance,
                            remainingDistance = data.remainingDistance,
                            elevation = data.elevation.toDouble(),
                            heading = data.heading,
                            batteryLevel = null
                        )
                    )
                    /*Log.d(
                        "BikeViewModel",
                        "Combined update: location=${data.location}, speed=${data.speedKmh}, " +
                                "remaining=${data.remainingDistance}, heading=${data.heading}, elevation=${data.elevation}"
                    )*/
                }
            }
        }
    }

    // Formats milliseconds into a string "1h 30m".
    private fun formatDurationToHM(millis: Long): String {
        val totalMinutes = millis / 1000 / 60
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return if (hours > 0) "$hours h $minutes m" else "$minutes m"
    }

    // Start ride duration updates and record ride start time.
    // We update the ride duration every second.
    private fun startRideDurationUpdatesOrig() {
        rideStartTime = System.currentTimeMillis()
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
                delay(1000L)
            }
        }
    }

    private fun startRideDurationUpdates() {
        // Initialize ride start time for duration calculation.
        rideStartTime = System.currentTimeMillis()
        viewModelScope.launch {
            while (true) {
                if (isRideActive) { // Only update duration if ride is active.
                    val elapsedMillis = System.currentTimeMillis() - rideStartTime
                    val formattedDuration = formatDurationToHM(elapsedMillis)
                    val currentState = _uiState.value as? BikeUiState.Success
                    currentState?.let {
                        _uiState.value = it.copy(
                            bikeData = it.bikeData.copy(rideDuration = formattedDuration)
                        )
                    }
                }
                delay(1000L)
            }
        }
    }

    // ---------- BIKE CONNECTIVITY ----------
    // Connect to the bike over BLE/NFC to retrieve battery and motor info.
    fun connectBike() {
        viewModelScope.launch {
            try {
                val bleAddress = connectivityRepository.getBleAddressFromNfc()
                connectivityRepository.connectBike(bleAddress)
                    // Only consider the emissions where batteryLevel (or motorPower) is not null.
                    .filter { motorData -> motorData.batteryLevel != null }
                    // Prevent duplicate emissions if the battery level stays the same.
                    .distinctUntilChanged()
                    .collect { motorData ->
                        // Update connection data:
                        bikeBatteryLevel = motorData.batteryLevel
                        bikeMotorPower = motorData.motorPower

                        // Update UI state to indicate the bike is connected.
                        val currentState = _uiState.value
                        if (currentState is BikeUiState.Success) {
                            _uiState.value = currentState.copy(
                                bikeData = currentState.bikeData.copy(
                                    batteryLevel = bikeBatteryLevel,
                                    motorPower = bikeMotorPower,
                                    isBikeConnected = true
                                )
                            )
                        }
                        Log.d("BikeViewModel", "Bike connected: battery=${motorData.batteryLevel}, motorPower=${motorData.motorPower}")
                    }
            } catch (e: Exception) {
                Log.e("BikeViewModel", "Failed to connect bike: ${e.message}")
            }
        }
    }

    // New private field to track if the ride is active (running) or paused.
    private var isRideActive: Boolean = false

    fun onEvent(event: BikeEvent) {
        when (event) {
            is BikeEvent.LoadBike -> loadSettings()
            is BikeEvent.UpdateSetting -> updateSetting(event.settingKey, event.settingValue)
            is BikeEvent.DeleteAllEntries -> deleteAllEntries()
            is BikeEvent.Connect -> {
                connectBike()
                Log.d("BikeViewModel", "Connect button clicked.")
            }
            BikeEvent.StartPauseRide -> {
                if (!isRideActive) {
                    // Start the ride.
                    isRideActive = true
                    // (Optional) Reset the ride start time when starting.
                    rideStartTime = System.currentTimeMillis()
                    Log.d("BikeViewModel", "Ride started.")
                } else {
                    // Pause the ride.
                    isRideActive = false
                    Log.d("BikeViewModel", "Ride paused.")
                }
                // Optionally update your UI state to reflect ride state changes.
                // You can add an "isRideActive" field in BikeRideInfo if needed.
            }
            BikeEvent.StopRide -> {
                isRideActive = false
                // Reset the ride start time, ride duration, and other ride metrics.
                rideStartTime = 0L
                Log.d("BikeViewModel", "Ride stopped.")
                val currentState = _uiState.value as? BikeUiState.Success
                currentState?.let {
                    _uiState.value = it.copy(
                        bikeData = it.bikeData.copy(
                            rideDuration = "00:00",
                            currentTripDistance = 0f,
                            averageSpeed = 0.0
                        )
                    )
                }
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
                    batteryLevel = null,
                    motorPower = null
                )
            )
            Log.d("BikeViewModel", "Settings loaded.")
        }
    }

    private fun updateSetting(key: String, value: String) {
        viewModelScope.launch {
            val currentState = _uiState.value as? BikeUiState.Success ?: return@launch
            val updatedSettings = currentState.settings.toMutableMap().apply { this[key] = listOf(value) }
            _uiState.value = currentState.copy(settings = updatedSettings)
        }
    }

    private fun deleteAllEntries() {
        // Add deletion logic here if needed.
    }
}
