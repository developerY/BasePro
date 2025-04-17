package com.ylabz.basepro.applications.bike.ui

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.core.data.repository.bikeConnectivity.BikeConnectivityRepository
import com.ylabz.basepro.core.data.repository.travel.compass.CompassRepository
import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationRepository
import com.ylabz.basepro.core.data.repository.weather.WeatherRepo
// import com.ylabz.basepro.core.database.BaseProRepo  // Import your repository
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.CombinedSensorData
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import usecase.WeatherInfo
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.flow.sample


@HiltViewModel
class BikeViewModel @Inject constructor(
    @Named("real")   private val realConnectivityRepo: BikeConnectivityRepository,
    @Named("real")   private val realLocationRepo: UnifiedLocationRepository,
    @Named("real")   private val realCompassRepo: CompassRepository,
    @Named("real")   private val realWeatherRepo: WeatherRepo,

    @Named("demo")   private val demoConnectivityRepo: BikeConnectivityRepository,
    @Named("demo")   private val demoLocationRepo: UnifiedLocationRepository,
    @Named("demo")   private val demoCompassRepo: CompassRepository,
    @Named("demo")   private val demoWeatherRepo: WeatherRepo,
) : ViewModel() {
    companion object {
        private const val TAG = "BikeViewModel"
    }

    private val realMode = true

    // Choose repos
    private val locationRepo      = if (realMode) realLocationRepo     else demoLocationRepo
    private val compassRepo       = if (realMode) realCompassRepo      else demoCompassRepo
    private val connectivityRepository  = if (realMode) realConnectivityRepo else demoConnectivityRepo
    private val weatherRepo       = if (realMode) realWeatherRepo      else demoWeatherRepo

    // UI state
    private val _uiState = MutableStateFlow<BikeUiState>(BikeUiState.Loading)
    val uiState: StateFlow<BikeUiState> = _uiState

    // Timing, flags
    private var rideStartTime = 0L
    private var hasFetchedWeather = false

    // Connectivity
    private var bikeBatteryLevel: Int?   = null
    private var bikeMotorPower: Float?   = null

    // Distance
    private val _totalRouteDistance = MutableStateFlow<Float?>(null)
    val totalRouteDistance: StateFlow<Float?> = _totalRouteDistance

    val traveledDistanceFlow: Flow<Float> = locationRepo.traveledDistanceFlow
    val remainingDistanceFlow: Flow<Float?> = combine(
        traveledDistanceFlow,
        totalRouteDistance
    ) { traveled, total -> total?.let { (it - traveled).coerceAtLeast(0f) } }

    // Combine sensors
    private val sensorDataFlow = combine(
        locationRepo.locationFlow,
        locationRepo.speedFlow,
        traveledDistanceFlow,
        totalRouteDistance,
        remainingDistanceFlow,
        locationRepo.elevationFlow,
        compassRepo.headingFlow
    ) { arr ->
        CombinedSensorData(
            location          = arr[0] as Location,
            speedKmh          = arr[1] as Float,
            traveledDistance  = arr[2] as Float,
            totalDistance     = arr[3] as Float?,
            remainingDistance = arr[4] as Float?,
            elevation         = arr[5] as Float,
            heading           = arr[6] as Float
        )
    }

    init {
        Log.d(TAG, "init: realMode=$realMode")
        onEvent(BikeEvent.LoadBike)
        startRideDurationUpdates()
        startSensorCollection()
    }

    /** 1) Runs on Default dispatcher, throttles updates, then updates UI on Main. */
    @OptIn(FlowPreview::class)
    private fun startSensorCollection() {
        viewModelScope.launch(Dispatchers.Default) {
            Log.d(TAG, "startSensorCollection: subscribing to sensorDataFlow")
            sensorDataFlow
                .sample(200L)
                .collect { data ->
                    Log.d(TAG, "sensorDataFlow emitted: $data")

                    // On first sensor data, trigger weather if not yet done:
                    if (!hasFetchedWeather) {
                        hasFetchedWeather = true
                        Log.d(TAG, "First location received, fetching weather now")
                        refreshWeather(data.location)
                    }

                    // Compute average speed
                    val elapsedHrs = (System.currentTimeMillis() - rideStartTime) / 3_600_000.0
                    val avgSpeed  = if (elapsedHrs > 0) data.traveledDistance / elapsedHrs else 0.0

                    // Build new BikeRideInfo
                    val newBikeData = (_uiState.value as? BikeUiState.Success)?.bikeData?.copy(
                        location            = LatLng(data.location.latitude, data.location.longitude),
                        currentSpeed        = data.speedKmh.toDouble(),
                        averageSpeed        = avgSpeed,
                        currentTripDistance = data.traveledDistance.coerceAtLeast(0f),
                        totalTripDistance   = data.totalDistance,
                        remainingDistance   = data.remainingDistance,
                        elevation           = data.elevation.toDouble(),
                        heading             = data.heading,
                        batteryLevel        = bikeBatteryLevel,
                        motorPower          = bikeMotorPower
                    )

                    // Push to UI on Main
                    withContext(Dispatchers.Main) {
                        newBikeData?.let { bd ->
                            _uiState.update { state ->
                                if (state is BikeUiState.Success) state.copy(bikeData = bd)
                                else state
                            }
                            Log.d(TAG, "UI state updated with sensor data")
                        }
                    }
                }
        }
    }

    /**
     *  Fetch weather once given a location.
     *  Includes logging around the API call and UI merge.
     */
    private fun refreshWeather(location: Location) {
        viewModelScope.launch {
            Log.d(TAG, "refreshWeather: starting for $location")
            val resp = runCatching {
                weatherRepo.openCurrentWeatherByCoords(
                    location.latitude,
                    location.longitude
                )
            }.onSuccess {
                Log.d(TAG, "Weather API success: $it")
            }.onFailure {
                Log.e(TAG, "Weather API failed", it)
            }.getOrNull()

            val weatherInfo = resp?.let {
                BikeWeatherInfo(
                    windDegree    = it.wind.deg,
                    windSpeed     = (it.wind.speed * 3.6f),
                    conditionText = "rain" // it.weatherOne.
                ).also { wi ->
                    Log.d(TAG, "Mapped BikeWeatherInfo: $wi")
                }
            }

            (_uiState.value as? BikeUiState.Success)?.let { current ->
                _uiState.update {
                    current.copy(
                        bikeData = current.bikeData.copy(bikeWeatherInfo = weatherInfo)
                    )
                }
                Log.d(TAG, "UI state merged with weatherInfo=$weatherInfo")
            }
        }
    }

    // … keep your connectBike(), onEvent(), startRideDurationUpdates(), loadSettings(), etc. …


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
                val current = _uiState.value as? BikeUiState.Success ?: return
                when (current.bikeData.rideState) {
                    RideState.NotStarted, RideState.Paused -> {
                        isRideActive = true
                        rideStartTime = System.currentTimeMillis()
                        updateRideState(RideState.Riding)
                        Log.d("BikeViewModel", "Ride started.")
                    }
                    RideState.Riding -> {
                        isRideActive = false
                        updateRideState(RideState.Paused)
                        Log.d("BikeViewModel", "Ride paused.")
                    }
                    RideState.Ended -> {
                        // If they press Start after ending, start a fresh ride:
                        resetRideData()
                        isRideActive = true
                        rideStartTime = System.currentTimeMillis()
                        updateRideState(RideState.Riding)
                        Log.d("BikeViewModel", "Ride restarted after end.")
                    }
                }
            }
            BikeEvent.StopSaveRide -> {
                // 1) Stop & mark ended
                isRideActive = false
                updateRideState(RideState.Ended)
                Log.d("BikeViewModel", "Ride stopped & will be saved.")

                // 2) Save snapshot
                val rideToSave = (_uiState.value as? BikeUiState.Success)?.bikeData
                rideToSave?.let {
                    viewModelScope.launch {
                        //rideHistoryRepo.saveRide(it)
                        Log.d("BikeViewModel", "Ride saved to history.")
                    }
                }

                // 3) Reset live UI data
                resetRideData()
            }
        }
    }

    // Toggles the rideState in the UI model
    private fun updateRideState(state: RideState) {
        val current = _uiState.value as? BikeUiState.Success ?: return
        _uiState.value = current.copy(
            bikeData = current.bikeData.copy(rideState = state)
        )
    }

    // Resets only the live-tracking fields (distance, duration, speed, state)
    private fun resetRideData() {
        val current = _uiState.value as? BikeUiState.Success ?: return
        _uiState.value = current.copy(
            bikeData = current.bikeData.copy(
                location            = null,
                currentSpeed        = 0.0,
                averageSpeed        = 0.0,
                currentTripDistance = 0f,
                totalTripDistance   = null,
                remainingDistance   = null,
                rideDuration        = "00:00",
                rideState           = RideState.NotStarted
            )
        )
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
                    motorPower = null,
                    bikeWeatherInfo    = null   // ← default
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
