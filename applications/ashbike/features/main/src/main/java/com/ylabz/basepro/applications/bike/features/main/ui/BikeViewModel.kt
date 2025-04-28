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
import com.ylabz.basepro.applications.bike.features.main.usecase.RideTracker
import com.ylabz.basepro.applications.bike.features.main.usecase.toBikeRideEntity
import com.ylabz.basepro.applications.bike.features.main.usecase.toRideLocationEntity
import com.ylabz.basepro.core.data.repository.bikeConnectivity.BikeConnectivityRepository
import com.ylabz.basepro.core.data.repository.timer.TimerRepository
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.concurrent.timer


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

    private val timerRepo: TimerRepository,
    private val rideStats: RideStatsUseCase,
    private val tracker: RideTracker
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

    // ---- 2) UI‐only ride state ----
    private val rideStateFlow = MutableStateFlow(RideState.NotStarted)

    //private var isRideActive = false
    //private val pathPoints = mutableListOf<Location>()

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
        .shareIn(viewModelScope, SharingStarted.Lazily, replay = 0)



    init {

        _uiState.value = BikeUiState.Success(
            bikeData = BikeRideInfo(
                location            = null,
                currentSpeed        = 0.0,
                averageSpeed        = 0.0,
                maxSpeed            = 0.0,
                currentTripDistance = 0f,
                totalTripDistance   = null,
                remainingDistance   = null,
                elevationGain       = 0.0,
                elevationLoss       = 0.0,
                caloriesBurned      = 0,
                rideDuration        = "00:00",
                settings            = emptyMap(),
                heading             = 0f,
                elevation           = 0.0,
                isBikeConnected     = false,
                batteryLevel        = null,
                motorPower          = null,
                rideState           = RideState.NotStarted,
                bikeWeatherInfo     = null
            )
        )

        // Load initial settings
        onEvent(BikeEvent.LoadBike)

        // Keep the tracker’s sessionFlow active so it gathers path points
        viewModelScope.launch {
            { /* log/trap errors here if you want */ }
            tracker.sessionFlow
                .collect { /* no-op; we just need it running */ }
        }

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

            BikeEvent.StartPauseRide -> {
                timerRepo.start()
                tracker.start()
                updateRideState(RideState.Riding)
            }

            BikeEvent.StopSaveRide -> {
                updateRideState(RideState.Ended)
                timerRepo.stop()
                viewModelScope.launch {
                    val session    = tracker.stopAndGetSession()
                    val rideEntity = session.toBikeRideEntity()
                    val locations  = session.path.map { it.toRideLocationEntity(rideEntity.rideId) }
                    bikeRideRepo.insertRideWithLocations(rideEntity, locations)
                }
            }

            is BikeEvent.Connect -> connectBike()

            is BikeEvent.SetTotalDistance -> {
                _uiState.update { current ->
                    if (current is BikeUiState.Success) {
                        // produce a new Success with updated totalTripDistance
                        current.copy(
                            bikeData = current.bikeData.copy(
                                totalTripDistance = event.totalDistance
                            )
                        )
                    } else {
                        current
                    }
                }
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

    // --- Helpers ------------------------------------------------------------
    /** Helper to merge the new state into your UI model */
    private fun updateRideState(newState: RideState) {
        _uiState.update { state ->
            if (state is BikeUiState.Success) {
                state.copy(
                    bikeData = state.bikeData.copy(rideState = newState)
                )
            } else state
        }
    }


    // Observe elapsed time and push to your UI state
    private fun startRideDurationUpdates() {
        viewModelScope.launch {
            timerRepo.elapsedTime.collect { elapsedMs ->
                val mins = (elapsedMs / 1000 / 60).toInt()
                val hrs = mins / 60
                val rem = mins % 60
                val text = if (hrs > 0) "$hrs h $rem m" else "$rem m"
                (_uiState.value as? BikeUiState.Success)?.let { cur ->
                    _uiState.value = cur.copy(
                        bikeData = cur.bikeData.copy(rideDuration = text)
                    )
                }
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
            // seed the UI with your default / initial BikeRideInfo
            _uiState.value = BikeUiState.Success(
                bikeData = BikeRideInfo(
                    // initial coordinates, zeros everywhere, plus your settings map…
                    location            = LatLng(37.4219999, -122.0862462),
                    currentSpeed        = 0.0,
                    averageSpeed        = 0.0,
                    maxSpeed            = 0.0,
                    currentTripDistance = 0f,
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
                    isBikeConnected     = false,
                    batteryLevel        = null,
                    motorPower          = null
                    // rideState & bikeWeatherInfo use their defaults
                )
            )
        }
    }


}