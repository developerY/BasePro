package com.ylabz.basepro.applications.bike.features.main.ui

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.flow.launchIn


@HiltViewModel
class BikeViewModel @Inject constructor(
    // Real implementations
    @Named("real") private val realConnectivityRepository: BikeConnectivityRepository,
    @Named("real") private val realLocationRepository: UnifiedLocationRepository,
    @Named("real") private val realCompassRepository: CompassRepository,
    @Named("real") private val realWeatherRepo: WeatherRepo,
    @Named("real") private val realBikeRideRepo: BikeRideRepo,

    // Demo implementations
    //@Named("demo") private val demoWeatherRepo: WeatherRepo,
    @Named("demo") private val demoConnectivityRepository: BikeConnectivityRepository,
    @Named("demo") private val demoLocationRepository: UnifiedLocationRepository,
    @Named("demo") private val demoCompassRepository: CompassRepository,
    @Named("demo") private val demoWeatherRepo: WeatherRepo,
    @Named("demo") private val demoBikeRideRepo: BikeRideRepo,

    ) : ViewModel() {

    // Toggle between real and demo mode.
    private val realMode = true

    // Choose the proper repository based on the mode.
    private val unifiedLocationRepository = if (realMode) realLocationRepository else demoLocationRepository
    private val compassRepository = if (realMode) realCompassRepository else demoCompassRepository
    private val connectivityRepository = if (realMode) realConnectivityRepository else demoConnectivityRepository
    private val weatherRepo = if (realMode) realWeatherRepo else demoWeatherRepo
    private val bikeRideRepo = if (realMode) realBikeRideRepo else demoBikeRideRepo

    // keep the points as we ride
    private val pathPoints = mutableListOf<Location>()
    private var rideEndTime   = 0L

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
                            batteryLevel = null,
                        )
                    )
                    /*Log.d(
                        "BikeViewModel",
                        "Combined update: location=${data.location}, speed=${data.speedKmh}, " +
                                "remaining=${data.remainingDistance}, heading=${data.heading}, elevation=${data.elevation}"
                    )*/
                }
            }

            // Subscribe to raw GPS updates and record them if the ride is active
            viewModelScope.launch {
                unifiedLocationRepository.locationFlow
                    .filter { isRideActive }
                    .collect { loc ->
                        pathPoints += loc
                    }
            }


        }

        // 4) Once we actually have a Success UI state, fire off the one‐time weather load
        viewModelScope.launch {
            // suspend until loadSettings() has emitted a Success
            _uiState
                .filterIsInstance<BikeUiState.Success>()
                .first()

            // now that UI is ready, fetch and merge the weather
            refreshWeather()
        }
    }

    /** Call this whenever you want to re‑fetch the weather (button or timer). */
    fun refreshWeather() {
        viewModelScope.launch {
            // get latest known location (you might cache it in a var when collecting sensors)
            val loc = unifiedLocationRepository.locationFlow.first()

            Log.d("Weather ", "Location=$loc")
            val resp = runCatching {
                weatherRepo.openCurrentWeatherByCoords(loc.latitude, loc.longitude)
            }.getOrNull()




            val weatherInfo = resp?.let {weather ->

                // safe‑call the list, take the first element (if any):
                val first = weather.weather.firstOrNull()

                // high‑level condition, e.g. "Clear", "Rain", "Clouds", "Snow", etc.
                val conditionMain = first?.main ?: "Unknown"

                // more detailed text, e.g. "clear sky", "light rain", etc.
                val conditionDesc = first?.description ?: "Unknown"

                Log.d("Weather", "Main=$conditionMain, Desc=$conditionDesc")

                Log.d("BikeViewModel", "Weather updated inside viewmodel: $weather")
                BikeWeatherInfo(
                    windDegree = weather.wind.deg,
                    windSpeed = weather.wind.speed * 3.6f,
                    conditionText = conditionMain,
                    conditionDescription = conditionDesc,
                    conditionIcon = weather.weather.firstOrNull()?.icon, // weather.weatherOne.firstOrNull()?.main ?: "Unknown"
                    temperature = weather.main.temp,
                    feelsLike = weather.main.feels_like,
                    humidity = weather.main.humidity,
                    // ← pull the temperature (°C)
                )
            }

            // merge into the existing UI state
            (_uiState.value as? BikeUiState.Success)?.let { current ->
                _uiState.value = current.copy(
                    bikeData = current.bikeData.copy(
                        bikeWeatherInfo = weatherInfo
                    )
                )
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
            is BikeEvent.StartPauseRide -> {
                val current = _uiState.value as? BikeUiState.Success ?: return
                when (current.bikeData.rideState) {
                    RideState.NotStarted, RideState.Paused -> {
                        isRideActive = true
                        rideStartTime = System.currentTimeMillis()
                        updateRideState(RideState.Riding)
                        // reset for a fresh ride
                        pathPoints.clear()
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
                viewModelScope.launch {
                    try {
                        // a) build the summary entity
                        val ride = BikeRideEntity(
                            startTime = rideStartTime,
                            endTime = rideEndTime,
                            totalDistance = (pathPoints.last()
                                .distanceTo(pathPoints.first())).toFloat(),
                            averageSpeed = 0f, // or compute from your flows
                            maxSpeed = 0f,
                            elevationGain = 0f,
                            elevationLoss = 0f,
                            caloriesBurned = 0,
                            startLat = pathPoints.first().latitude,
                            startLng = pathPoints.first().longitude,
                            endLat = pathPoints.last().latitude,
                            endLng = pathPoints.last().longitude
                        )

                        // b) map each Location → RideLocationEntity
                        val locations = pathPoints.map { loc ->
                            RideLocationEntity(
                                rideId = ride.rideId,
                                timestamp = loc.time,
                                lat = loc.latitude,
                                lng = loc.longitude,
                                elevation = loc.altitude.toFloat()
                            )
                        }

                        // c) call your repo
                        bikeRideRepo.insertRideWithLocations(ride, locations)
                    } catch (e: Exception) {
                        // handle errors (maybe set an error UI state)
                    }
                }
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
