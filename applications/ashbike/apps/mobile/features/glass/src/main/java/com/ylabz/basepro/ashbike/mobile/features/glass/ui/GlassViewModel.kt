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
                repository.rideInfo // <--- ADD THIS
            ) { gear, susp, info ->
                // Create new state whenever either value changes
                _uiState.value.copy(
                    currentGear = gear,
                    suspension = susp,
                            // Format the speed from the Repository info
                    currentSpeed = String.format("%.1f", info.currentSpeed)
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
            GlassUiEvent.CloseApp -> {}
            GlassUiEvent.OpenGearList -> {}
        }
    }
}