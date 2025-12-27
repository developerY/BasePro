package com.ylabz.basepro.ashbike.mobile.features.glass.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.repository.bike.BikeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel // 1. Add this annotation
class GlassViewModel @Inject constructor(
    private val repository: BikeRepository // 2. Inject the repository instance
) : ViewModel() {

    // UI State for the Glass Screen
    private val _uiState = MutableStateFlow(GlassUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Combine flows from Repository to update UI automatically
            combine(
                repository.currentGear,
                repository.suspensionState,
                repository.rideInfo, // <--- ADD THIS
            ) { gear, susp, info ->

                // Update the UI State ->

                // Helper to format "350.0" -> "350° N"
                val bearingText = formatBearing(info.heading)

                // Create new state whenever either value changes
                _uiState.value.copy(
                    currentGear = gear,
                    suspension = susp,
                            // Format the speed from the Repository info
                    currentSpeed = String.format("%.1f", info.currentSpeed),
                    heading = bearingText, // <--- Map it here
                    // 1. Format Power (Watts)
                    motorPower = info.motorPower?.let { "%.0f".format(it) } ?: "--",
                    // 2. Format Heart Rate (BPM)
                    heartRate = info.heartbeat?.toString() ?: "--", // <--- Add BPM
                    tripDistance = String.format("%.1f", info.currentTripDistance), // e.g. "12.5"
                    calories = info.caloriesBurned.toString(), // e.g. "350"
                    // Format Duration (assuming info.rideDuration is already a String "HH:MM:SS" or similar)
                    rideDuration = info.rideDuration,
                    // Format Avg Speed
                    averageSpeed = String.format("%.1f", info.averageSpeed),
                    isBikeConnected = info.isBikeConnected ,// <--- Update the state
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun onEvent(event: GlassUiEvent) {
        when(event) {
            // 4. Call methods on the injected instance
            GlassUiEvent.GearUp -> { viewModelScope.launch { repository.gearUp() } }
            GlassUiEvent.GearDown -> { viewModelScope.launch { repository.gearDown() } }
            GlassUiEvent.ToggleSuspension -> { viewModelScope.launch { repository.toggleSuspension() } }

            // For now, these might be handled by the UI/Activity directly,
            // or you can add logic here if needed.
            GlassUiEvent.CloseApp -> { /* handled in UI/Activity */ }
            is GlassUiEvent.SelectGear -> {
                // If you want the list to update the gear:
                // repository.setGear(event.gear)
            }

            // Navigation events are handled by the Compose UI layer (GlassApp),
            // but we list them here to be exhaustive or if we wanted to log them.
            GlassUiEvent.OpenGearList -> {}
        }
    }

    // --- HELPER: Convert Degrees to Direction ---
    private fun formatBearing(bearing: Float): String {
        if (bearing < 0) return "---"
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        val index = ((bearing + 22.5) / 45.0).toInt() and 7
        return "${bearing.toInt()}° ${directions[index]}"
    }
}