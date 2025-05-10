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
    @HighPower    private val locationRepo: UnifiedLocationRepository,
    private val tracker: RideSessionUseCase,
    private val weatherUseCase: WeatherUseCase,
    private val bikeRideRepo: BikeRideRepo
) : ViewModel() {

    // 1) What ride‐state are we in? (NotStarted / Riding / Ended)
    private val _rideState       = MutableStateFlow(RideState.NotStarted)

    // 2) One‐shot weather at ride start (null until we fetch it)
    private val _weatherInfo     = MutableStateFlow<BikeWeatherInfo?>(null)

    // 3) In‐memory UI override for “total distance” (never saved)
    private val _uiPathDistance  = MutableStateFlow<Float?>(null)

    // 4) Single source‐of‐truth UI state
    private val _uiState         = MutableStateFlow<BikeUiState>(BikeUiState.Loading)
    val uiState: StateFlow<BikeUiState> = _uiState

    init {
        viewModelScope.launch {
            // Stage 1: combine the “real” data streams into a BikeRideInfo *without* the user override
            val baseFlow: Flow<BikeRideInfo> = combine(
                tracker.sessionFlow,        // RideSession
                tracker.distanceFlow,       // Float
                locationRepo.speedFlow,     // Float
                _weatherInfo,               // BikeWeatherInfo?
                _rideState                  // RideState
            ) { session, gpsKm, rawSpeed, weather, rideState ->
                session
                    .toBikeRideInfo(
                        weather       = weather,
                        totalDistance = null     // no override yet
                    )
                    .copy(
                        currentTripDistance = if (rideState == RideState.Riding) gpsKm else 0f,
                        currentSpeed        = rawSpeed.toDouble(),
                        heading             = session.heading,
                        rideState           = rideState
                    )
            }

            // Stage 2: merge in the user’s “fake” total‐distance override
            combine(
                baseFlow,                   // BikeRideInfo from stage 1
                _uiPathDistance            // Float? (null until the user enters something)
            ) { baseInfo, uiTotalKm ->
                baseInfo.copy(totalTripDistance = uiTotalKm)
            }
                .map<BikeRideInfo, BikeUiState> { BikeUiState.Success(it) }
                .catch { e -> emit(BikeUiState.Error(e.message ?: "Unknown error")) }
                .collect { _uiState.value = it }
        }
    }



    fun onEvent(event: BikeEvent) {
        when (event) {
            is BikeEvent.StartRide -> startRide()
            is BikeEvent.StopRide  -> stopAndSaveRide()
            is BikeEvent.SetTotalDistance -> {
                // only ever update this in‐memory override
                _uiPathDistance.value = event.distanceKm
            }
        }
    }

    private fun startRide() {
        if (_rideState.value == RideState.NotStarted) {
            viewModelScope.launch(Dispatchers.IO) {
                // grab a location fix so we can fetch weather once
                val loc = locationRepo.locationFlow.first()
                _weatherInfo.value = weatherUseCase.getWeather(
                    lat = loc.latitude, lng = loc.longitude
                )

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
                val rideEnt = session.toBikeRideEntity()
                val locRows = session.path.map { fix ->
                    RideLocationEntity(
                        rideId    = rideEnt.rideId,
                        timestamp = fix.time,
                        lat       = fix.latitude,
                        lng       = fix.longitude,
                        elevation = fix.altitude.toFloat()
                    )
                }
                bikeRideRepo.insertRideWithLocations(rideEnt, locRows)
                _rideState.value = RideState.NotStarted
            }
        }
    }
}
