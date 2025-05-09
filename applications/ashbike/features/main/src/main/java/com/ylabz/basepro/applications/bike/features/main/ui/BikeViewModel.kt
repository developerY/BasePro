package com.ylabz.basepro.applications.bike.features.main.ui

import android.location.Location
import android.util.Log
import androidx.core.util.TimeUtils.formatDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
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
    private val _uiState     = MutableStateFlow<BikeUiState>(BikeUiState.Loading)
    val uiState: StateFlow<BikeUiState> = _uiState.asStateFlow()

    // Expose live distance from the tracker
    private val distanceFlow: Flow<Float> = tracker.distanceFlow

    init {
        viewModelScope.launch {
            combine(
                tracker.sessionFlow,
                distanceFlow,
                _weatherInfo,
                _rideState
            ) { session, curDistance, weather, rideState ->
                when (rideState) {
                    RideState.NotStarted -> BikeUiState.Loading
                    RideState.Riding,
                    RideState.Ended     -> {
                        // Base info from session, override current distance
                        session.toBikeRideInfo(
                            weather       = weather,
                            totalDistance = null
                        ).copy(
                            currentTripDistance = curDistance
                        ).let { info ->
                            BikeUiState.Success(info)
                        }
                    }
                }
            }
                .catch { e -> emit(BikeUiState.Error(e.localizedMessage ?: "Unknown error")) }
                .collect { state -> _uiState.value = state }
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
                // one-shot weather at ride start
                val loc: Location = locationRepo.locationFlow.first()
                _weatherInfo.value = weatherUseCase.getWeather(
                    lat = loc.latitude,
                    lng = loc.longitude
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