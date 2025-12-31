package com.ylabz.basepro.ashbike.mobile.features.glass.ui
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ylabz.basepro.ashbike.mobile.features.glass.newui.AshGlassLayout
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.screens.HomeScreen


// Simple Enum to handle local navigation
enum class ScreenState {
    HOME,
    BIKE,
    GEAR_LIST
}

@Composable
fun GlassApp(
    onClose: () -> Unit,
    viewModel: GlassViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Top-level state for the app

    // Simple Navigation Switcher
    when (uiState.currentScreen) {
        ScreenState.HOME -> {
            HomeScreen(
                uiState = uiState,
                onEvent = { event ->
                    // ROUTING LOGIC:
                    when (event) {
                        // 1. Navigation Events -> Handle Locally
                        GlassUiEvent.OpenGearList -> uiState.currentScreen = ScreenState.GEAR_LIST
                        GlassUiEvent.CloseApp -> onClose()

                        // 2. Business Events -> Send to ViewModel
                        else -> viewModel.onEvent(event)
                    }
                }
            )
        }
        ScreenState.BIKE -> {
            AshGlassLayout(
                uiState = uiState,
                onEvent = { event ->
                    // ROUTING LOGIC:
                    when (event) {
                        // 1. Navigation Events -> Handle Locally
                        GlassUiEvent.OpenGearList -> uiState.currentScreen = ScreenState.GEAR_LIST
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