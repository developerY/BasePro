package com.ylabz.basepro.ashbike.mobile.features.glass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.ashbike.mobile.features.glass.data.GlassBikeRepository
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GlassEvent
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GlassUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
            repository.currentGear.collect { gear ->
                _uiState.update { it.copy(currentGear = gear) }
            }
        }
    }

    fun onEvent(event: GlassEvent) {
        when(event) {
            // 4. Call methods on the injected instance
            GlassEvent.GearUp -> repository.gearUp()
            GlassEvent.GearDown -> repository.gearDown()

            // For now, these might be handled by the UI/Activity directly,
            // or you can add logic here if needed.
            GlassEvent.CloseApp -> { /* handled in UI/Activity */ }
            is GlassEvent.SelectGear -> {
                // If you want the list to update the gear:
                // repository.setGear(event.gear)
            }
        }
    }
}