package com.ylabz.basepro.applications.bike.features.main.ui

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.applications.bike.database.repository.UserProfileRepository
import com.ylabz.basepro.applications.bike.features.main.usecase.RideSession
import com.ylabz.basepro.applications.bike.features.main.usecase.RideStatsUseCase
import com.ylabz.basepro.applications.bike.features.main.usecase.RideTracker
import com.ylabz.basepro.applications.bike.features.main.usecase.UserStats
import com.ylabz.basepro.applications.bike.features.main.usecase.toBikeRideEntity
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
    private val tracker: RideTracker,
    private val timerRepo: TimerRepository,
    private val weatherRepo: WeatherRepo,
    private val bikeRideRepo: BikeRideRepo
) : ViewModel() {

    private data class RideSnapshot(
        val fix:     Location,
        val speed:   Float,
        val session: RideSession,
        val elapsed: Long
    )

    private val snapshotFlow: Flow<RideSnapshot> = combine(
        locationRepo.locationFlow,
        locationRepo.speedFlow,
        tracker.sessionFlow,
        timerRepo.elapsedTime
    ) { fix, speed, session, elapsed ->
        RideSnapshot(fix, speed, session, elapsed)
    }

    // Ride vs NotStarted
    private val _rideState = MutableStateFlow(RideState.NotStarted)

    // One-shot weather
    private val _weatherInfo = MutableStateFlow<BikeWeatherInfo?>(null)

    init {
        // Fetch weather once we get the first GPS fix
        viewModelScope.launch {
            val loc = locationRepo.locationFlow.first()
            val resp = runCatching {
                weatherRepo.openCurrentWeatherByCoords(loc.latitude, loc.longitude)
            }.getOrNull()

            val info = resp
                ?.weather
                ?.firstOrNull()
                ?.let { w ->
                    BikeWeatherInfo(
                        windDegree           = resp.wind.deg,
                        windSpeed            = resp.wind.speed * 3.6f,
                        conditionText        = w.main,
                        conditionDescription = w.description,
                        conditionIcon        = w.icon,
                        temperature          = resp.main.temp,
                        feelsLike            = resp.main.feels_like,
                        humidity             = resp.main.humidity
                    )
                }

            _weatherInfo.value = info
        }
    }

    // Combined UI state
    val uiState: StateFlow<BikeUiState> = combine(
        snapshotFlow,
        _weatherInfo,
        _rideState
    ) { snapshot, weather, rideState ->

        val (fix, speedKmh, session, elapsedMs) = snapshot
        val distKm = if (rideState == RideState.Riding) session.totalDistanceKm else 0f

        BikeUiState.Success(
            bikeData = BikeRideInfo(
                location            = LatLng(fix.latitude, fix.longitude),
                currentSpeed        = speedKmh.toDouble(),
                averageSpeed        = session.averageSpeedKmh,
                maxSpeed            = session.maxSpeedKmh.toDouble(),
                currentTripDistance = distKm,
                totalTripDistance   = null,
                remainingDistance   = null,
                elevationGain       = session.elevationGainM.toDouble(),
                elevationLoss       = session.elevationLossM.toDouble(),
                caloriesBurned      = session.caloriesBurned,
                rideDuration        = formatDuration(elapsedMs),
                settings            = emptyMap(),    // your settings here
                heading             = session.heading,
                elevation           = fix.altitude.toDouble(),
                isBikeConnected     = false,         // wire in BLE later
                batteryLevel        = null,
                motorPower          = null,
                rideState           = rideState,
                bikeWeatherInfo     = weather
            )
        )
    }
    // RIGHT HERE we still have Flow<BikeUiState.Success>
    .map { it as BikeUiState } // ↑ now its Flow<BikeUiState>
    .catch { e ->
        emit(BikeUiState.Error(e.localizedMessage ?: "Unknown error"))
    }
    .stateIn(viewModelScope, SharingStarted.Eagerly, BikeUiState.Loading)


    /** Handle UI button presses */
    fun onEvent(event: BikeEvent) {
        when (event) {
            BikeEvent.StartPauseRide -> {
                if (_rideState.value != RideState.Riding) {
                    tracker.start()
                    timerRepo.start()
                    _rideState.value = RideState.Riding
                }
            }
            BikeEvent.StopSaveRide -> {
                if (_rideState.value == RideState.Riding) {
                    timerRepo.stop()
                    val session   = tracker.stopAndGetSession()
                    val entity    = session.toBikeRideEntity()
                    val locations = session.path.map { it.toRideLocationEntity(entity.rideId) }
                    viewModelScope.launch {
                        bikeRideRepo.insertRideWithLocations(entity, locations)
                    }
                    _rideState.value = RideState.NotStarted
                }
            }
            else -> {
                // e.g. PauseRide / ResumeRide if you add them
            }
        }
    }

    /** Convert ms → "H h M m" or "M m" */
    private fun formatDuration(ms: Long): String {
        val totalMin = (ms / 1000 / 60).toInt()
        val h = totalMin / 60
        val m = totalMin % 60
        return if (h > 0) "$h h $m m" else "$m m"
    }
}
