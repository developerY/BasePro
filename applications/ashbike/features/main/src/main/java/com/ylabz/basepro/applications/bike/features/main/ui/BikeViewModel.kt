package com.ylabz.basepro.applications.bike.features.main.ui

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.applications.bike.features.main.usecase.RideStatsUseCase
import com.ylabz.basepro.core.data.repository.bikeConnectivity.BikeConnectivityRepository
import com.ylabz.basepro.core.data.repository.travel.compass.CompassRepository
import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationRepository
import com.ylabz.basepro.core.data.repository.weather.WeatherRepo
// import com.ylabz.basepro.core.database.BaseProRepo  // Import your repository
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.CombinedSensorData
import com.ylabz.basepro.core.model.bike.CombinedSensorDataOld
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.shareIn


@HiltViewModel
class BikeViewModel @Inject constructor(
    // Real
    @Named("real") private val realConnectivityRepository: BikeConnectivityRepository,
    @Named("real") private val realLocationRepository: UnifiedLocationRepository,
    @Named("real") private val realCompassRepository: CompassRepository,
    @Named("real") private val realWeatherRepo: WeatherRepo,
    @Named("real") private val realBikeRideRepo: BikeRideRepo,

    // Demo
    @Named("demo") private val demoConnectivityRepository: BikeConnectivityRepository,
    @Named("demo") private val demoLocationRepository: UnifiedLocationRepository,
    @Named("demo") private val demoCompassRepository: CompassRepository,
    @Named("demo") private val demoWeatherRepo: WeatherRepo,
    @Named("demo") private val demoBikeRideRepo: BikeRideRepo,
    private val rideStats: RideStatsUseCase
) : ViewModel() {

    // Toggle
    private val realMode = true

    // Pick implementations
    // toggle
    private val connectivityRepo = if (realMode) realConnectivityRepository else demoConnectivityRepository
    private val locationRepo     = if (realMode) realLocationRepository     else demoLocationRepository
    private val compassRepo      = if (realMode) realCompassRepository      else demoCompassRepository
    private val weatherRepo      = if (realMode) realWeatherRepo            else demoWeatherRepo
    private val bikeRideRepo     = if (realMode) realBikeRideRepo           else demoBikeRideRepo


    // State
    private val _uiState = MutableStateFlow<BikeUiState>(BikeUiState.Loading)
    val uiState: StateFlow<BikeUiState> = _uiState

    private var isRideActive = false
    private var rideStartTime = 0L
    private var rideEndTime = 0L
    private val pathPoints = mutableListOf<Location>()

    // Derived flows
    val traveledDistanceFlow: Flow<Float> = locationRepo.traveledDistanceFlow
    private val _totalRouteDistance = MutableStateFlow<Float?>(null)
    val totalRouteDistance: StateFlow<Float?> = _totalRouteDistance
    val remainingDistanceFlow: Flow<Float?> = combine(
        traveledDistanceFlow,
        totalRouteDistance
    ) { traveled, total -> total?.let { (it - traveled).coerceAtLeast(0f) } }


    // reset trigger for stats
    private val resetTrigger = MutableSharedFlow<Unit>(replay = 1)



    // stats flows
    private val distanceFlow      = rideStats.distanceKmFlow(resetTrigger, locationRepo.locationFlow)
    private val maxSpeedFlow      = rideStats.maxSpeedFlow(resetTrigger, locationRepo.speedFlow)
    private val avgSpeedFlow      = rideStats.averageSpeedFlow(resetTrigger, locationRepo.speedFlow)
    private val elevationGainFlow = rideStats.elevationGainFlow(resetTrigger, locationRepo.locationFlow)
    private val elevationLossFlow = rideStats.elevationLossFlow(resetTrigger, locationRepo.locationFlow)
    private val caloriesFlow      = rideStats.caloriesFlow(resetTrigger, distanceFlow)

    // keep a running list of speeds too
    private val speedHistory = mutableListOf<Float>()

    // combined sensor data
    private val sensorDataFlow = combine(
        locationRepo.locationFlow,    // 0
        locationRepo.speedFlow,       // 1
        distanceFlow,                 // 2
        avgSpeedFlow,                 // 3
        maxSpeedFlow,                 // 4
        elevationGainFlow,            // 5
        elevationLossFlow,            // 6
        caloriesFlow,                 // 7
        compassRepo.headingFlow       // 8
    ) { values: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val loc: Location    = values[0] as Location
        val speed: Float     = values[1] as Float
        val dist: Float      = values[2] as Float
        val avg: Double      = values[3] as Double
        val max: Float       = values[4] as Float
        val gain: Float      = values[5] as Float
        val loss: Float      = values[6] as Float
        val cal: Int         = values[7] as Int
        val heading: Float   = values[8] as Float

        CombinedSensorData(
            location          = loc,
            speedKmh          = speed,
            traveledDistance  = dist,
            averageSpeed      = avg,
            maxSpeed          = max,
            elevationGain     = gain,
            elevationLoss     = loss,
            caloriesBurned    = cal,
            heading           = heading
        )
    }
        .onEach { data ->
            if ((_uiState.value as? BikeUiState.Success)?.bikeData?.rideState == RideState.Riding) {
                pathPoints += data.location
            }
        }
        .shareIn(viewModelScope, SharingStarted.Lazily, replay = 0)



    init {
        // Load initial settings
        onEvent(BikeEvent.LoadBike)

        // A) raw GPS collector
        viewModelScope.launch {
            locationRepo.locationFlow
                .filter { isRideActive }                    // from kotlinx.coroutines.flow
                .onEach { loc ->                            // from kotlinx.coroutines.flow
                    Log.d("BikeViewModel", "raw loc: $loc")
                    pathPoints += loc
                }
                .catch { e -> Log.e("BikeViewModel", "locationFlow error", e) }
                .collect()                                  // from kotlinx.coroutines.flow
        }

        // when collecting CombinedSensorData:
        viewModelScope.launch {
            sensorDataFlow
                .onEach { data ->
                    if (isRideActive) {
                        pathPoints += data.location
                        speedHistory += data.speedKmh
                    }
                }
                .collect()
        }

        /*
        viewModelScope.launch {
            unifiedLocationRepository.locationFlow
                .filter { isRideActive }
                .collect { loc ->
                    Log.d("BikeViewModel", "raw loc: $loc")
                    pathPoints += loc
                }
        }
        */

        // B) sensor data collector
        viewModelScope.launch {
            sensorDataFlow.collect { data ->
                val curState = _uiState.value as? BikeUiState.Success ?: return@collect
                _uiState.value = curState.copy(
                    bikeData = curState.bikeData.copy(
                        location             = LatLng(data.location.latitude, data.location.longitude),
                        currentSpeed         = data.speedKmh.toDouble(),
                        averageSpeed         = data.averageSpeed,
                        maxSpeed             = data.maxSpeed.toDouble(),
                        currentTripDistance  = data.traveledDistance,
                        elevationGain        = data.elevationGain.toDouble(),
                        elevationLoss        = data.elevationLoss.toDouble(),
                        caloriesBurned       = data.caloriesBurned,
                        heading              = data.heading
                    )
                )
            }
        }

        // C) duration updater
        startRideDurationUpdates()

        // D) one-time weather refresh after UI ready
        viewModelScope.launch {
            _uiState.filterIsInstance<BikeUiState.Success>()
                .first()
            refreshWeather()
        }
    }

    /** Handle UI events */
    fun onEvent(event: BikeEvent) {
        when (event) {
            is BikeEvent.LoadBike -> loadSettings()

            is BikeEvent.StartPauseRide -> {
                val current = (_uiState.value as? BikeUiState.Success) ?: return
                when (current.bikeData.rideState) {
                    RideState.NotStarted, RideState.Paused -> {
                        // 1) clear old path
                        pathPoints.clear()
                        // 2) reset the stats flows
                        rideStartTime = System.currentTimeMillis()
                        isRideActive  = true
                        viewModelScope.launch { resetTrigger.emit(Unit) }
                        updateRideState(RideState.Riding)
                    }
                    RideState.Riding -> {
                        isRideActive = false
                        updateRideState(RideState.Paused)
                    }
                    RideState.Ended -> {
                        resetRideData()
                        pathPoints.clear()
                        rideStartTime = System.currentTimeMillis()
                        isRideActive = true
                        updateRideState(RideState.Riding)
                        Log.d("BikeViewModel", "Ride restarted")
                    }
                }
            }

            BikeEvent.StopSaveRide -> {
                isRideActive = false
                rideEndTime  = System.currentTimeMillis()
                updateRideState(RideState.Ended)

                viewModelScope.launch {
                    if (pathPoints.size < 2) return@launch

                    // 1) total distance (meters)
                    val totalDistanceMeters = pathPoints
                        .zipWithNext { a, b -> a.distanceTo(b) }
                        .sum()

                    // 2) elapsed time (hours)
                    val elapsedHours = (rideEndTime - rideStartTime).toDouble() / 3_600_000.0

                    // 3) average & max speed (km/h)
                    val avgSpeedKmh = if (elapsedHours > 0)
                        (totalDistanceMeters/1000f) / elapsedHours
                    else 0.0
                    val maxSpeedKmh = (speedHistory.maxOrNull() ?: 0f).toDouble()

                    // 4) elevation gain & loss (meters)
                    var gain = 0f
                    var loss = 0f
                    pathPoints.zipWithNext { a, b ->
                        val d = (b.altitude - a.altitude).toFloat()
                        if (d > 0) gain += d else loss += -d
                    }

                    // 5) calories (kcal)
                    val calories = ((totalDistanceMeters/1000f) * 50).toInt()

                    // build the ride
                    val ride = BikeRideEntity(
                        startTime      = rideStartTime,
                        endTime        = rideEndTime,
                        totalDistance  = totalDistanceMeters,
                        averageSpeed   = avgSpeedKmh.toFloat(),
                        maxSpeed       = maxSpeedKmh.toFloat(),
                        elevationGain  = gain,
                        elevationLoss  = loss,
                        caloriesBurned = calories,
                        startLat       = pathPoints.first().latitude,
                        startLng       = pathPoints.first().longitude,
                        endLat         = pathPoints.last().latitude,
                        endLng         = pathPoints.last().longitude
                    )

                    // save the locations
                    val locs = pathPoints.map { loc ->
                        RideLocationEntity(
                            rideId    = ride.rideId,
                            timestamp = loc.time,
                            lat       = loc.latitude,
                            lng       = loc.longitude,
                            elevation = loc.altitude.toFloat()
                        )
                    }

                    bikeRideRepo.insertRideWithLocations(ride, locs)
                }
            }

            is BikeEvent.Connect -> {
                connectBike()
            }

            else -> { /* other settings, delete, etc. */ }
        }
    }

    /** Fetch weather once after initial load */
    fun refreshWeather() {
        viewModelScope.launch {
            val loc = locationRepo.locationFlow.first()
            Log.d("BikeViewModel", "refreshWeather at $loc")
            val resp = runCatching {
                weatherRepo.openCurrentWeatherByCoords(loc.latitude, loc.longitude)
            }.getOrNull()

            val info = resp?.weather?.firstOrNull()?.let { w ->
                BikeWeatherInfo(
                    windDegree = resp.wind.deg,
                    windSpeed = resp.wind.speed * 3.6f,
                    conditionText = w.main,
                    conditionDescription = w.description,
                    conditionIcon = w.icon,
                    temperature = resp.main.temp,
                    feelsLike = resp.main.feels_like,
                    humidity = resp.main.humidity
                )
            }

            (_uiState.value as? BikeUiState.Success)?.let { cur ->
                _uiState.value = cur.copy(
                    bikeData = cur.bikeData.copy(bikeWeatherInfo = info)
                )
                Log.d("BikeViewModel", "Weather merged: $info")
            }
        }
    }

    private fun updateRideState(state: RideState) {
        val cur = _uiState.value as? BikeUiState.Success ?: return
        _uiState.value = cur.copy(
            bikeData = cur.bikeData.copy(rideState = state)
        )
    }

    private fun resetRideData() {
        val currentState = _uiState.value as? BikeUiState.Success ?: return

        _uiState.value = currentState.copy(
            bikeData = currentState.bikeData.copy(
                // Live‐tracking fields
                rideState           = RideState.NotStarted,
                location            = null,
                currentSpeed        = 0.0,
                averageSpeed        = 0.0,
                currentTripDistance = 0f,
                totalTripDistance   = null,
                remainingDistance   = null,
                rideDuration        = "00:00",

                // Leave settings alone
                // currentState.bikeData.settings

                // Sensor fields
                heading        = 0f,
                elevation      = 0.0,

                // Bike connection
                isBikeConnected = false,
                batteryLevel    = null,
                motorPower      = null,

                // Weather
                bikeWeatherInfo = null
            )
        )
    }


    private fun startRideDurationUpdates() {
        viewModelScope.launch {
            while (true) {
                if (isRideActive) {
                    val elapsed = System.currentTimeMillis() - rideStartTime
                    val mins = elapsed / 1000 / 60
                    val hrs = mins / 60
                    val rem = mins % 60
                    val text = if (hrs > 0) "$hrs h $rem m" else "$rem m"
                    val cur = _uiState.value as? BikeUiState.Success ?: return@launch
                    _uiState.value = cur.copy(
                        bikeData = cur.bikeData.copy(rideDuration = text)
                    )
                }
                delay(1_000L)
            }
        }
    }

    private fun connectBike() {
        viewModelScope.launch {
            try {
                val address = connectivityRepo.getBleAddressFromNfc()
                connectivityRepo.connectBike(address)
                    .filter { it.batteryLevel != null }
                    .distinctUntilChanged()
                    .collect { data ->
                        val cur = _uiState.value as? BikeUiState.Success ?: return@collect
                        _uiState.value = cur.copy(
                            bikeData = cur.bikeData.copy(
                                batteryLevel = data.batteryLevel,
                                motorPower = data.motorPower,
                                isBikeConnected = true
                            )
                        )
                        Log.d("BikeViewModel", "Bike connected: $data")
                    }
            } catch (e: Exception) {
                Log.e("BikeViewModel", "connectBike failed", e)
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
                    // Core location & speeds
                    location            = LatLng(37.4219999, -122.0862462),
                    currentSpeed        = 0.0,
                    averageSpeed        = 0.0,
                    maxSpeed            = 0.0,

                    // Distances (km)
                    currentTripDistance = 0.0f,
                    totalTripDistance   = null,
                    remainingDistance   = null,

                    // Elevation (m)
                    elevationGain       = 0.0,
                    elevationLoss       = 0.0,

                    // Calories
                    caloriesBurned      = 0,

                    // UI state
                    rideDuration        = "00:00",
                    settings            = mapOf(
                        "Theme" to listOf("Light", "Dark", "System Default"),
                        "Language" to listOf("English", "Spanish", "French"),
                        "Notifications" to listOf("Enabled", "Disabled")
                    ),
                    heading             = 0f,
                    elevation           = 0.0,

                    // Bike connectivity
                    isBikeConnected     = false,
                    batteryLevel        = null,
                    motorPower          = null,

                    // rideState & weatherInfo use their defaults
                )
            )
        }
    }

}