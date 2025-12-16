package com.ylabz.basepro.ashbike.mobile.features.glass.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.ashbike.mobile.features.glass.data.GlassBikeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel // 1. Add this annotation
class GlassViewModel @Inject constructor(
    private val repository: GlassBikeRepository // 2. Inject the repository instance
) : ViewModel() {

    // UI State for the Glass Screen
    private val _uiState = MutableStateFlow(GlassUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Listen to the shared repository
        viewModelScope.launch {
            // 3. Use 'repository' instance, not the class name
            // Combine Gear AND Suspension updates
            combine(
                repository.currentGear,
                repository.suspensionState
            ) { gear, susp ->
                GlassUiState(currentGear = gear, suspension = susp)
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun onEvent(event: GlassUiEvent) {
        when(event) {
            // 4. Call methods on the injected instance
            GlassUiEvent.GearUp -> repository.gearUp()
            GlassUiEvent.GearDown -> repository.gearDown()

            // For now, these might be handled by the UI/Activity directly,
            // or you can add logic here if needed.
            GlassUiEvent.CloseApp -> { /* handled in UI/Activity */ }
            is GlassUiEvent.SelectGear -> {
                // If you want the list to update the gear:
                // repository.setGear(event.gear)
            }

            GlassUiEvent.ToggleSuspension -> repository.toggleSuspension()
        }
    }
}