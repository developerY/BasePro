package com.ylabz.basepro.applications.bike.features.main.ui

// import com.ylabz.basepro.core.database.BaseProRepo  // Import your repository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.applications.bike.features.main.usecase.RideSessionUseCase
import com.ylabz.basepro.applications.bike.features.main.usecase.toBikeRideEntity
import com.ylabz.basepro.applications.bike.features.main.usecase.toBikeRideInfo
import com.ylabz.basepro.core.data.di.HighPower
import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationRepository
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BikeViewModelOrig @Inject constructor(
    @HighPower private val locationRepo: UnifiedLocationRepository,
    private val tracker: RideSessionUseCase,
    private val weatherUseCase: WeatherUseCase,
    private val bikeRideRepo: BikeRideRepo
) : ViewModel() {

    // 1) What ride‐state are we in? (NotStarted / Riding / Ended)
    private val _rideState = MutableStateFlow(RideState.NotStarted)

    // 2) One‐shot weather at ride start (null until we fetch it)
    private val _weatherInfo = MutableStateFlow<BikeWeatherInfo?>(null)

    // 3) In‐memory UI override for “total distance” (never saved)
    private val _uiPathDistance = MutableStateFlow<Float?>(null)

    // 4) Single source‐of‐truth UI state
    private val _uiState = MutableStateFlow<BikeUiState>(BikeUiState.WaitingForGps)
    val uiState: StateFlow<BikeUiState> = _uiState

    init {
        // 1️⃣ Kick off a one-time weather fetch on app start:
        viewModelScope.launch(Dispatchers.IO) {
            // wait for first GPS fix so we have coords
            val loc = locationRepo.locationFlow.first()
            _weatherInfo.value = weatherUseCase.getWeather(
                lat = loc.latitude,
                lng = loc.longitude
            )
        }

        // 2️⃣ Build your two-stage combine pipeline
        viewModelScope.launch {
            // Stage 1: combine all “real” signals except the UI override
            val baseFlow: Flow<BikeRideInfo> = combine(
                tracker.sessionFlow,      // RideSession
                tracker.distanceFlow,     // Float (km)
                locationRepo.speedFlow,   // Float (m/s or km/h)
                _weatherInfo,             // BikeWeatherInfo?
                _rideState                // RideState
            ) { session, gpsKm, rawSpeed, weather, rideState ->
                //Log.d("StatsColorDebug", "Combine is running with rideState: $rideState") // <-- ADD THIS
                session.toBikeRideInfo(
                    weather = weather,
                    totalDistance = null             // no user override yet
                ).copy(
                    currentTripDistance = if (rideState == RideState.Riding) gpsKm else 0f,
                    currentSpeed = rawSpeed.toDouble(),
                    heading = session.heading,
                    rideState = rideState
                )
            }

            // Stage 2: merge in your UI-only “fake” total distance
            combine(
                baseFlow,                 // BikeRideInfo
                _uiPathDistance          // Float? (null until user sets it)
            ) { baseInfo, uiTotalKm ->
                baseInfo.copy(totalTripDistance = uiTotalKm)
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
            is BikeEvent.SetTotalDistance -> {
                // this only updates the in-memory UI override—no database, no API calls
                _uiPathDistance.value = event.distanceKm
            }
            BikeEvent.StartRide -> startRide()
            BikeEvent.StopRide -> stopAndSaveRide()
            // …
        }
    }

    private fun startRide() {
        //Log.d("StatsColorDebug", "startRide() called. Current state is ${_rideState.value}") // <-- ADD THIS
        if (_rideState.value == RideState.NotStarted) {
            tracker.start()
            _rideState.value = RideState.Riding
            //Log.d("StatsColorDebug", "ViewModel _rideState updated to: ${_rideState.value}") // <-- ADD THIS
        }
    }

    private fun stopAndSaveRide() {
        if (_rideState.value == RideState.Riding) {
            _rideState.value = RideState.Ended
            viewModelScope.launch(Dispatchers.IO) {
                val session = tracker.stopAndGetSession()
                val entity = session.toBikeRideEntity()
                val locs = session.path.map { loc ->
                    RideLocationEntity(
                        rideId = entity.rideId,
                        timestamp = loc.time,
                        lat = loc.latitude,
                        lng = loc.longitude,
                        elevation = loc.altitude.toFloat()
                    )
                }
                bikeRideRepo.insertRideWithLocations(entity, locs)
                _rideState.value = RideState.NotStarted
                _uiPathDistance.value = null
            }
        }
        _rideState.value = RideState.NotStarted
    }
}