package com.ylabz.basepro.applications.bike.features.main.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.database.repository.AppSettingsRepository
import com.ylabz.basepro.applications.bike.features.main.service.BikeForegroundService
import com.ylabz.basepro.applications.bike.features.main.service.BikeServiceManager
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.GlassButtonState
import com.ylabz.basepro.core.data.repository.bike.BikeRepository
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.LocationEnergyLevel
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import javax.inject.Inject

// 1. Define the Side Effects (One-time events sent to UI)

@HiltViewModel
class BikeViewModel @Inject constructor(
    private val bikeServiceManager: BikeServiceManager, // <--- Injected Manager
    private val weatherUseCase: WeatherUseCase, // Inject WeatherUseCase here
    private val appSettingsRepository: AppSettingsRepository,
    // 1. INJECT THE GLASS REPO (Even if it's an object, injecting it is cleaner for testing)
    private val bikeRepository: BikeRepository
) : ViewModel() {

    // 2. Create a Channel for Side Effects
    // Channels are perfect for one-off events (navigation, toasts)
    private val _effects = Channel<BikeSideEffect>()
    val effects = _effects.receiveAsFlow()

    // --- Navigation Channel ---
    private val _navigateTo = MutableSharedFlow<String>()
    val navigateTo: SharedFlow<String> = _navigateTo.asSharedFlow()

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

    // Helper Data Class to group Glass inputs
    data class GlassState(
        val gear: Int,
        val isSimulatedActive: Boolean,
        val buttonState: GlassButtonState // <--- The calculated state
    )

    // A temporary data holder class to make the logic cleaner
    // Update CombinedData to hold the new state
    data class CombinedData(
        val rideInfo: BikeRideInfo,
        val totalDistance: Float?,
        val weather: BikeWeatherInfo?,
        val showDialog: Boolean,
        val showGpsCountdown: Boolean,
        val gpsAccuracy: LocationEnergyLevel,
        val glassGear: Int,
        val isGlassActive: Boolean,
        val glassButtonState: GlassButtonState // <--- Added
    )

    init {
        observeBikeData()
        fetchWeatherForDashboard()
    }



    // NOTE: The BikeViewModel starts it but GlassViewModel also uses it. We might want it to start elsewhere?
    private fun observeBikeData() {
        Log.d("BikeViewModel", "observeServiceData called.")
        viewModelScope.launch {
                Log.d("BikeViewModel", "Starting to collect from service.rideInfo.")

                // 1. HELPER FLOW A: Group the Glass Data (Reduces 2 flows -> 1 flow)
                // 1. UPDATED GLASS FLOW: Calculates the 3-State Logic
                val glassStateFlow = combine(
                    bikeRepository.currentGear,
                    bikeRepository.isConnected,       // Simulated Data Connection
                    bikeRepository.isGlassConnected,  // Hardware Connection
                ) { gear, simActive, hwConnected ->

                    // CALCULATE BUTTON STATE
                    val btnState = when {
                        !hwConnected -> GlassButtonState.NO_GLASSES
                        // isProjecting -> GlassButtonState.PROJECTING
                        else -> GlassButtonState.READY_TO_START
                    }

                    GlassState(gear, simActive, btnState)
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
                    bikeServiceManager.rideInfo.sample(1000L), // <--- Use Manager Flow
                    _weatherInfo,
                    appSettingsRepository.gpsAccuracyFlow,
                    glassStateFlow, // The grouped Glass data
                    uiFlagsFlow     // The grouped UI flags
                ) { rideInfo, weather, gpsAccuracy, glassState, uiFlags ->

                    // Unpack the helper objects
                    // val (glassGear, isGlassActive) = glassState
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
                        // Pass Glass Data
                        glassGear = glassState.gear,
                        isGlassActive = glassState.isSimulatedActive,
                        glassButtonState = glassState.buttonState // <--- Pass to Holder
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
                            isGlassActive = data.isGlassActive,
                            glassButtonState = data.glassButtonState // Ensure this field exists in BikeUiState.Success
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
            }

    }


    private fun fetchWeatherForDashboard() {
        // Prevent re-fetching
        if (_weatherInfo.value != null) return

        viewModelScope.launch {
            // Use Manager to get location safely
            try {
                // We wait for the first valid location from the service manager
                val rideInfo = bikeServiceManager.rideInfo.first { it.location != null }
                val location = rideInfo.location ?: return@launch

                _weatherInfo.value = weatherUseCase(location.latitude, location.longitude)
                Log.d("BikeViewModel", "Weather fetched successfully.")
            } catch (e: Exception) {
                Log.e("BikeViewModel", "Error fetching weather", e)
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

            BikeEvent.StartRide -> bikeServiceManager.sendCommand(BikeForegroundService.ACTION_START_RIDE)
            BikeEvent.StopRide -> {
                bikeServiceManager.sendCommand(BikeForegroundService.ACTION_STOP_RIDE)
                _uiPathDistance.value = null
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
                viewModelScope.launch {
                    _navigateTo.emit("settings/${event.cardKey}")
                }
                // TODO: Add any ViewModel-specific logic for this event if required in the future.
            }


            is BikeEvent.ToggleGlassProjection -> {
                // Perform any business logic checks here (e.g., isConnected?)
                viewModelScope.launch {
                    // Tell the UI to launch the activity
                    _effects.send(BikeSideEffect.LaunchGlassProjection)
                }
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

}
