package com.ylabz.basepro.applications.bike.features.main.ui

import android.location.Location
import android.util.Log
import androidx.core.util.TimeUtils.formatDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.math.MathUtils.dist
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.applications.bike.database.repository.UserProfileRepository
import com.ylabz.basepro.applications.bike.features.main.usecase.RideSession
import com.ylabz.basepro.applications.bike.features.main.usecase.RideSessionUseCase
import com.ylabz.basepro.applications.bike.features.main.usecase.RideStatsUseCase
import com.ylabz.basepro.applications.bike.features.main.usecase.UserStats
import com.ylabz.basepro.applications.bike.features.main.usecase.toBikeRideEntity
import com.ylabz.basepro.applications.bike.features.main.usecase.toBikeRideInfo
import com.ylabz.basepro.applications.bike.features.main.usecase.toRideLocationEntity
import com.ylabz.basepro.core.data.di.HighPower
import com.ylabz.basepro.core.data.di.LowPower
import com.ylabz.basepro.core.data.repository.bikeConnectivity.BikeConnectivityRepository
import com.ylabz.basepro.core.data.repository.timer.TimerRepository
import com.ylabz.basepro.core.data.repository.travel.compass.CompassRepository
import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationRepository
import com.ylabz.basepro.core.data.repository.weather.WeatherRepo
import com.ylabz.basepro.core.data.service.health.HealthSessionManager
// import com.ylabz.basepro.core.database.BaseProRepo  // Import your repository
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.CombinedSensorData
import com.ylabz.basepro.core.model.bike.CombinedSensorDataOld
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Qualifier
import kotlin.concurrent.timer


@HiltViewModel
class BikeViewModel @Inject constructor(
    @HighPower private val locationRepo: UnifiedLocationRepository,
    private val tracker: RideSessionUseCase,
    private val weatherUseCase: WeatherUseCase,
    private val bikeRideRepo: BikeRideRepo
) : ViewModel() {

    private val _rideState   = MutableStateFlow(RideState.NotStarted)
    private val _weatherInfo = MutableStateFlow<BikeWeatherInfo?>(null)


    // 1a) the raw, auto-distance (from your tracker)
    private val autoDistance = tracker.distanceFlow

    // 1b) the user’s typed override
    private val _manualDistance = MutableStateFlow<Float?>(null)
    // If user has entered a value, use that; otherwise fall back to autoDistance
    private val totalDistanceFlow: Flow<Float> =
        _manualDistance
            .flatMapLatest { manually ->
                if (manually != null) flowOf(manually)
                else autoDistance
            }
            .distinctUntilChanged()

    // seed with a zeroed-out "empty" ride
    private val initialRideInfo = RideSession(
        startTimeMs     = 0L,
        path            = emptyList(),
        elapsedMs       = 0L,
        totalDistanceKm = 0f,
        averageSpeedKmh = 0.0,
        maxSpeedKmh     = 0f,
        elevationGainM  = 0f,
        elevationLossM  = 0f,
        caloriesBurned  = 0,
        heading         = 0f
    ).toBikeRideInfo(
        weather       = null,
        totalDistance = null
    )


    private val _uiState = MutableStateFlow<BikeUiState>(BikeUiState.Success(initialRideInfo))
    val uiState: StateFlow<BikeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {

            // first wait for the initial fix & fetch weather
            val loc = locationRepo.locationFlow.first()
            _weatherInfo.value = weatherUseCase.getWeather(loc.latitude, loc.longitude)

            // then start your infinite combine/collect loop
            combine(
                tracker.sessionFlow,        // avg, elevation, calories, heading
                totalDistanceFlow,
                locationRepo.speedFlow,     // raw instantaneous speed (m/s or km/h)
                _weatherInfo,
                _rideState
            ) { session,totalDist, rawSpeed, weather, rideState ->

                // Build the base info from your session
                val base = session
                    .toBikeRideInfo(weather = weather, totalDistance = null)
                    .copy(
                        // Always feed in the live speed, regardless of state:
                        currentSpeed        = rawSpeed.toDouble(),
                        heading             = session.heading,
                        rideState           = rideState,
                        // Lift in the live distance only once riding
                        currentTripDistance = if (rideState == RideState.NotStarted) 0f else totalDist
                    )

                // Reset only trip‐related metrics pre‐start:
                if (rideState == RideState.NotStarted) {
                    base.copy(
                        averageSpeed   = 0.0,
                        maxSpeed       = 0.0,
                        elevationGain  = 0.0,
                        elevationLoss  = 0.0,
                        caloriesBurned = 0,
                        rideDuration   = "0 m"
                    )
                } else base
            }
            // ► map BikeRideInfo → BikeUiState
            .map<BikeRideInfo, BikeUiState> { info ->
                BikeUiState.Success(info)
            }
            // ► now catch can emit BikeUiState.Error
            .catch { e ->
                emit(BikeUiState.Error(e.localizedMessage ?: "Unknown error"))
            }
            // ► collect BikeUiState
            .collect { uiState ->
                _uiState.value = uiState
            }
        }
    }




    fun onEvent(event: BikeEvent) {
        when (event) {
            is BikeEvent.StartRide        -> startRide()
            is BikeEvent.StopRide         -> stopAndSaveRide()
            is BikeEvent.SetTotalDistance -> {
                _uiState.update { state ->
                    if (state is BikeUiState.Success) {
                        state.copy(
                            bikeData = state.bikeData.copy(
                                totalTripDistance = event.distanceKm
                            )
                        )
                    } else state
                }
            }
        }
    }

    private fun startRide() {
        if (_rideState.value == RideState.NotStarted) {
            viewModelScope.launch(Dispatchers.IO) {
                tracker.start()
                _rideState.value = RideState.Riding
            }
        }
    }

    private fun stopAndSaveRide() {
        if (_rideState.value == RideState.Riding) {
            _rideState.value = RideState.Ended
            viewModelScope.launch(Dispatchers.IO) {
                val session = tracker.stopAndGetSession()
                val entity  = session.toBikeRideEntity()
                val locs    = session.path.map { loc ->
                    RideLocationEntity(
                        rideId    = entity.rideId,
                        timestamp = loc.time,
                        lat       = loc.latitude,
                        lng       = loc.longitude,
                        elevation = loc.altitude.toFloat()
                    )
                }
                bikeRideRepo.insertRideWithLocations(entity, locs)
                _rideState.value = RideState.NotStarted
            }
        }
    }
}