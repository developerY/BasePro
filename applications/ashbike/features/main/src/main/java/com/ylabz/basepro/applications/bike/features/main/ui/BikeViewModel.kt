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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect


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
) : ViewModel() {

    // Toggle
    private val realMode = true

    // Pick implementations
    private val connectivityRepository = if (realMode) realConnectivityRepository else demoConnectivityRepository
    private val unifiedLocationRepository = if (realMode) realLocationRepository else demoLocationRepository
    private val compassRepository = if (realMode) realCompassRepository else demoCompassRepository
    private val weatherRepo = if (realMode) realWeatherRepo else demoWeatherRepo
    private val bikeRideRepo = if (realMode) realBikeRideRepo else demoBikeRideRepo

    // State
    private val _uiState = MutableStateFlow<BikeUiState>(BikeUiState.Loading)
    val uiState: StateFlow<BikeUiState> = _uiState

    private var isRideActive = false
    private var rideStartTime = 0L
    private var rideEndTime = 0L
    private val pathPoints = mutableListOf<Location>()

    // Derived flows
    val traveledDistanceFlow: Flow<Float> = unifiedLocationRepository.traveledDistanceFlow
    private val _totalRouteDistance = MutableStateFlow<Float?>(null)
    val totalRouteDistance: StateFlow<Float?> = _totalRouteDistance
    val remainingDistanceFlow: Flow<Float?> = combine(
        traveledDistanceFlow,
        totalRouteDistance
    ) { traveled, total -> total?.let { (it - traveled).coerceAtLeast(0f) } }

    private val sensorDataFlow: Flow<CombinedSensorData> = combine(
        unifiedLocationRepository.locationFlow,
        unifiedLocationRepository.speedFlow,
        traveledDistanceFlow,
        totalRouteDistance,
        remainingDistanceFlow,
        unifiedLocationRepository.elevationFlow,
        compassRepository.headingFlow
    ) { arr ->
        CombinedSensorData(
            location = arr[0] as Location,
            speedKmh = arr[1] as Float,
            traveledDistance = arr[2] as Float,
            totalDistance = arr[3] as Float?,
            remainingDistance = arr[4] as Float?,
            elevation = arr[5] as Float,
            heading = arr[6] as Float
        )
    }

    init {
        // Load initial settings
        onEvent(BikeEvent.LoadBike)

        // A) raw GPS collector
        viewModelScope.launch {
            unifiedLocationRepository.locationFlow
                .filter { isRideActive }                    // from kotlinx.coroutines.flow
                .onEach { loc ->                            // from kotlinx.coroutines.flow
                    Log.d("BikeViewModel", "raw loc: $loc")
                    pathPoints += loc
                }
                .catch { e -> Log.e("BikeViewModel", "locationFlow error", e) }
                .collect()                                  // from kotlinx.coroutines.flow
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
                Log.d("BikeViewModel", "sensor data: $data")
                val current = _uiState.value as? BikeUiState.Success ?: return@collect

                // update UI
                val elapsedHrs = (System.currentTimeMillis() - rideStartTime) / 3_600_000.0
                val avgSpeed = if (elapsedHrs > 0) data.traveledDistance / elapsedHrs else 0.0
                _uiState.value = current.copy(
                    bikeData = current.bikeData.copy(
                        location = LatLng(data.location.latitude, data.location.longitude),
                        currentSpeed = data.speedKmh.toDouble(),
                        averageSpeed = avgSpeed,
                        currentTripDistance = data.traveledDistance,
                        totalTripDistance = data.totalDistance,
                        remainingDistance = data.remainingDistance,
                        elevation = data.elevation.toDouble(),
                        heading = data.heading
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
                        pathPoints.clear()
                        rideStartTime = System.currentTimeMillis()
                        isRideActive = true
                        updateRideState(RideState.Riding)
                        Log.d("BikeViewModel", "Ride started")
                    }
                    RideState.Riding -> {
                        isRideActive = false
                        updateRideState(RideState.Paused)
                        Log.d("BikeViewModel", "Ride paused")
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
                rideEndTime = System.currentTimeMillis()
                updateRideState(RideState.Ended)
                Log.d("BikeViewModel", "Stop pressed at $rideEndTime")

                viewModelScope.launch {
                    try {
                        // Build ride
                        val ride = BikeRideEntity(
                            startTime = rideStartTime,
                            endTime = rideEndTime,
                            totalDistance = pathPoints
                                .last().distanceTo(pathPoints.first()).toFloat(),
                            averageSpeed = 0f,
                            maxSpeed = 0f,
                            elevationGain = 0f,
                            elevationLoss = 0f,
                            caloriesBurned = 0,
                            startLat = pathPoints.first().latitude,
                            startLng = pathPoints.first().longitude,
                            endLat = pathPoints.last().latitude,
                            endLng = pathPoints.last().longitude
                        )
                        // Map locations
                        val locs = pathPoints.map {
                            RideLocationEntity(
                                rideId = ride.rideId,
                                timestamp = it.time,
                                lat = it.latitude,
                                lng = it.longitude,
                                elevation = it.altitude.toFloat()
                            )
                        }
                        Log.d("BikeViewModel", "Saving ride + ${locs.size} points")
                        bikeRideRepo.insertRideWithLocations(ride, locs)
                    } catch (e: Exception) {
                        Log.e("BikeViewModel", "Save failed", e)
                    }
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
            val loc = unifiedLocationRepository.locationFlow.first()
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
                val address = connectivityRepository.getBleAddressFromNfc()
                connectivityRepository.connectBike(address)
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
            Log.d("BikeViewModel", "Settings loaded")
        }
    }
}