package com.ylabz.basepro.applications.bike.features.main.ui

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.database.repository.AppSettingsRepository
import com.ylabz.basepro.applications.bike.features.main.service.BikeForegroundService
import com.ylabz.basepro.core.data.repository.bike.BikeRepository
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.LocationEnergyLevel
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BikeViewModel @Inject constructor(
    private val application: Application, // <-- Inject Application here
    private val weatherUseCase: WeatherUseCase, // Inject WeatherUseCase here
    private val appSettingsRepository: AppSettingsRepository,
    // 1. INJECT THE GLASS REPO (Even if it's an object, injecting it is cleaner for testing)
    private val bikeRepository: BikeRepository
) : ViewModel() {

    // --- Navigation Channel ---
    private val _navigateTo = MutableSharedFlow<String>()
    val navigateTo: SharedFlow<String> = _navigateTo.asSharedFlow()

    // --- State for the Service Connection ---
    private val _bound = MutableStateFlow(false)
    private var bikeService: BikeForegroundService? = null // Renamed property

    // --- State exposed to the UI ---
    private val _uiState = MutableStateFlow<BikeUiState>(BikeUiState.WaitingForGps)
    val uiState: StateFlow<BikeUiState> = _uiState.asStateFlow()

    // --- In-memory UI-only override for total distance ---
    private val _uiPathDistance = MutableStateFlow<Float?>(null)

    // 2) One‐shot weather at ride start (null until we fetch it)
    private val _weatherInfo = MutableStateFlow<BikeWeatherInfo?>(null)

    // 1. ADD THIS NEW STATE FLOW FOR THE DIALOG
    private val _showSetDistanceDialog = MutableStateFlow(false)

    // 1. ADD THIS NEW STATE FLOW FOR THE DIALOG
    private val _showGpsCountdownFlow = MutableStateFlow(true)


    // --- Service Connection ---
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("BikeViewModel", "Service Connected. Name: $name")
            val binder = service as BikeForegroundService.LocalBinder
            bikeService = binder.getService() // Updated usage
            _bound.value = true
            Log.d(
                "BikeViewModel",
                "BikeForegroundService instance obtained: $bikeService"
            ) // Updated usage
            // CORRECT PLACE: Start both observers after bikeService is guaranteed to be non-null.
            observeServiceData()
            fetchWeatherForDashboard()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("BikeViewModel", "Service Disconnected. Name: $name")
            _bound.value = false
            bikeService = null // Updated usage
        }
    }


    // A temporary data holder class to make the logic cleaner
    data class CombinedData(
        val rideInfo: BikeRideInfo,
        val totalDistance: Float?,
        val weather: BikeWeatherInfo?,
        val showDialog: Boolean,
        val showGpsCountdown: Boolean,
        val gpsAccuracy: LocationEnergyLevel,
        val glassGear: Int,       // <--- NEW
        val isGlassActive: Boolean // <--- NEW
    )

    // NOTE: The BikeViewModel starts it but GlassViewModel also uses it. We might want it to start elsewhere?
    private fun observeServiceData() {
        Log.d("BikeViewModel", "observeServiceData called.")
        viewModelScope.launch {
            bikeService?.let { service ->
                Log.d("BikeViewModel", "Starting to collect from service.rideInfo.")

                // 1. HELPER FLOW A: Group the Glass Data (Reduces 2 flows -> 1 flow)
                val glassStateFlow = combine(
                    bikeRepository.currentGear,
                    bikeRepository.isConnected
                ) { gear, active ->
                    Pair(gear, active) // Or create a data class GlassState(gear, active)
                }

                // 2. HELPER FLOW B: Group the UI Flags (Reduces 3 flows -> 1 flow)
                val uiFlagsFlow = combine(
                    _showSetDistanceDialog,
                    _showGpsCountdownFlow,
                    _uiPathDistance
                ) { showDialog, showCountdown, distance ->
                    Triple(showDialog, showCountdown, distance)
                }

                // 3. MAIN COMBINE: Now we only have 5 inputs! (Safe & Clean)
                combine(
                    service.rideInfo.sample(1000L),
                    _weatherInfo,
                    appSettingsRepository.gpsAccuracyFlow,
                    glassStateFlow, // The grouped Glass data
                    uiFlagsFlow     // The grouped UI flags
                ) { rideInfo, weather, gpsAccuracy, glassState, uiFlags ->

                    // Unpack the helper objects
                    val (glassGear, isGlassActive) = glassState
                    val (showDialog, showCountdown, totalDistance) = uiFlags
                    // Log inside the combine lambda
                    Log.d(
                        "BikeViewModel_DEBUG",
                        "Combine lambda. rideInfo.location: ${rideInfo.location}, gpsAccuracy (from settings): $gpsAccuracy, showGpsCountdown: $showCountdown"
                    )
                    CombinedData(
                        rideInfo,
                        totalDistance,
                        weather,
                        showDialog,
                        showCountdown,
                        gpsAccuracy,
                        glassGear,     // Pass to data holder
                        isGlassActive  // Pass to data holder
                    )
                }
                    // 2. MAP: This block's job is to transform the raw data into the final UI State.
                    //    Crucially, its return type is declared as the supertype, 'BikeUiState'.
                    .map<CombinedData, BikeUiState> { data ->
                        // Log inside the map lambda
                        Log.d("BikeViewModel_DEBUG", "Map lambda. Input CombinedData: $data.")
                        val stateToEmit = BikeUiState.Success(
                            bikeData = data.rideInfo.copy(
                                totalTripDistance = data.totalDistance,
                                bikeWeatherInfo = data.weather
                            ),
                            showSetDistanceDialog = data.showDialog,
                            showGpsCountdown = data.showGpsCountdown,
                            locationEnergyLevel = data.gpsAccuracy, // <<< USE data.gpsAccuracy HERE
                            // 4. POPULATE UI STATE
                            // (Ensure you added these fields to BikeUiState.Success data class)
                            glassGear = data.glassGear,
                            isGlassActive = data.isGlassActive
                        )
                        // Log the state being emitted and the key gpsAccuracy value from CombinedData
                        Log.d(
                            "BikeViewModel_DEBUG",
                            "Map lambda. Emitting BikeUiState.Success: $stateToEmit. data.gpsAccuracy (energy level from settings) was: ${data.gpsAccuracy}"
                        )
                        stateToEmit
                    }
                    // 3. CATCH: This now works perfectly, because the flow is of type Flow<BikeUiState>.
                    .catch { e ->
                        Log.e(
                            "BikeViewModel_DEBUG",
                            "Error in UI state flow: ${e.message}",
                            e
                        ) // Added _DEBUG to tag
                        emit(BikeUiState.Error(e.localizedMessage ?: "Service error"))
                    }
                    .collect { state ->
                        Log.d(
                            "BikeViewModel_DEBUG",
                            "Collected final UI state: $state"
                        ) // Added _DEBUG to tag
                        _uiState.value = state
                    }
            } ?: run {
                Log.w("BikeViewModel", "observeServiceData: bikeService is null, cannot collect.")
            }
        }
    }


    private fun fetchWeatherForDashboard() {
        // This check prevents re-fetching if the service reconnects
        if (_weatherInfo.value != null) return

        viewModelScope.launch {
            // Now we know bikeService is not null when this is called.
            val location =
                bikeService?.rideInfo?.first { it.location != null }?.location ?: return@launch

            try {
                // CORRECTED: Use the class-level property directly, which Kotlin can now resolve.
                // The previous `this.` was ambiguous inside the coroutine scope.
                _weatherInfo.value = weatherUseCase(location.latitude, location.longitude)
                Log.d("BikeViewModel", "Weather fetched successfully for dashboard.")
            } catch (e: Exception) {
                Log.e("BikeViewModel", "Error fetching weather for dashboard", e)
            }
        }
    }

    // The context parameter is now gone!
    fun onEvent(event: BikeEvent) {
        // This function's only job is to update the raw "source of truth" StateFlows.
        // The `combine` block in `observeServiceData` will automatically react to these
        // changes and produce the new, correct UI state.
        // Ensure this when statement is exhaustive by covering all event types
        // defined in the BikeEvent sealed class.
        when (event) {
            is BikeEvent.SetTotalDistance -> {
                _uiPathDistance.value = event.distanceKm
                // Hide the dialog when the user confirms a new distance
                _showSetDistanceDialog.value = false
            }

            BikeEvent.StartRide -> {
                sendCommandToService(BikeForegroundService.ACTION_START_RIDE)
            }

            BikeEvent.StopRide -> {
                sendCommandToService(BikeForegroundService.ACTION_STOP_RIDE)
                _uiPathDistance.value = null // Resetting the UI path distance
            }

            BikeEvent.OnBikeClick -> {
                // The user clicked the bike icon, so we need to show the dialog.
                _showSetDistanceDialog.value = true
            }

            BikeEvent.DismissSetDistanceDialog -> {
                // The user dismissed the dialog, so we need to hide it.
                _showSetDistanceDialog.value = false
            }

            is BikeEvent.NavigateToSettingsRequested -> {
                // Primarily handled by BikeUiRoute for navigation.
                // ViewModel can perform other actions if needed (e.g., logging, analytics).
                Log.d(
                    "BikeViewModel",
                    "NavigateToSettingsRequested event received. CardKey: ${event.cardKey}"
                )
                // TODO: Add any ViewModel-specific logic for this event if required in the future.
            }

            // 5. HANDLE GLASS EVENTS
            // If the Phone UI has controls to change gears (e.g. testing buttons), handle them here.
            // If the 'Launch' logic is purely UI-side (Activity start), you might not need an event here,
            // but if you want to track it or reset state:
            /*
            BikeEvent.GearUp -> glassRepository.gearUp()
            BikeEvent.GearDown -> glassRepository.gearDown()
            */
        }
    }

    // This function now uses the injected 'application' context
    private fun sendCommandToService(action: String) {
        Log.d("BikeViewModel", "sendCommandToService: $action")
        val intent =
            Intent(application, BikeForegroundService::class.java).apply { this.action = action }
        application.startService(intent) // Ensures service is running if not already
    }

    // --- Service Lifecycle Management ---
    // Note: bind/unbind still need the Activity/Fragment context, which is fine
    // because these methods are called directly from the UI layer's lifecycle.
    fun bindToService(context: Context) {
        if (!_bound.value) {
            Log.d("BikeViewModel", "bindToService called. Current bound state: ${_bound.value}")
            Intent(context, BikeForegroundService::class.java).also { intent ->
                try {
                    Log.d("BikeViewModel", "Attempting to start service.")
                    context.startService(intent) // Good to ensure it's started, especially if it might not be running
                    Log.d("BikeViewModel", "Attempting to bind service.")
                    val didBind =
                        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                    Log.d("BikeViewModel", "bindService call returned: $didBind")
                    if (!didBind) {
                        Log.e(
                            "BikeViewModel",
                            "bindService returned false. Service may not be available or manifest declaration missing."
                        )
                    }
                } catch (e: Exception) {
                    Log.e("BikeViewModel", "Exception during bindToService: ${e.message}", e)
                }
            }
        } else {
            Log.d("BikeViewModel", "bindToService called, but already bound.")
        }
    }

    fun unbindFromService(context: Context) {
        Log.d(
            "BikeViewModel",
            "unbindFromService called. Current internal _bound state: ${_bound.value}"
        )
        try {
            // Always attempt to unbind. The Android system tracks the actual binding.
            // If it's not bound, context.unbindService will throw IllegalArgumentException.
            context.unbindService(serviceConnection)
            Log.d("BikeViewModel", "context.unbindService() called successfully.")
        } catch (e: IllegalArgumentException) {
            // This is expected if the service was already unbound or was never bound with this connection.
            Log.w(
                "BikeViewModel",
                "IllegalArgumentException during unbindService: ${e.message}. Service might have been already unbound or not registered."
            )
        } catch (e: Exception) {
            // Catch any other unexpected exceptions during unbinding.
            Log.e("BikeViewModel", "Generic exception during unbindFromService: ${e.message}", e)
        }
        // Regardless of whether unbindService threw an exception (e.g., already unbound),
        // update our internal state to reflect that we've processed the unbind request.
        _bound.value = false
        bikeService = null
    }
}
