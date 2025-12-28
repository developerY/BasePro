package com.ylabz.basepro.ashbike.mobile.features.glass.ui
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.HomeScreen


// Simple Enum to handle local navigation
enum class ScreenState {
    HOME,
    GEAR_LIST
}

@Composable
fun GlassApp(
    onClose: () -> Unit,
    viewModel: GlassViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Top-level state for the app
    var currentScreen by remember { mutableStateOf(ScreenState.HOME) }



    // Simple Navigation Switcher
    when (currentScreen) {
        ScreenState.HOME -> {
            HomeScreen(
                uiState = uiState,
                onEvent = { event ->
                    // ROUTING LOGIC:
                    when (event) {
                        // 1. Navigation Events -> Handle Locally
                        GlassUiEvent.OpenGearList -> currentScreen = ScreenState.GEAR_LIST
                        GlassUiEvent.CloseApp -> onClose()

                        // 2. Business Events -> Send to ViewModel
                        else -> viewModel.onEvent(event)
                    }
                }
            )
        }
        ScreenState.GEAR_LIST -> {
            // Preserving your logic for Gear Selection
            /*GearSelectionScreen(
                currentGear = uiState.currentGear,
                onGearSelected = { selectedGear ->
                    // Send change to VM, then nav back
                    uiState.currentGear = selectedGear
                    currentScreen = ScreenState.HOME // Go back after selection
                }
            )*/
        }
    }
}