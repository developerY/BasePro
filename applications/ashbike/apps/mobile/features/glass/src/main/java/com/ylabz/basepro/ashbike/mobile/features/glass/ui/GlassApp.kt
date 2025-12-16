package com.ylabz.basepro.ashbike.mobile.features.glass.ui
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ylabz.basepro.ashbike.mobile.features.glass.GlassViewModel


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
                currentGear = uiState.currentGear,
                onGearChange = { newGear ->
                    // Boundary checks
                    if (newGear in 1..12) uiState.currentGear = newGear
                },
                onOpenGearList = { currentScreen = ScreenState.GEAR_LIST },
                onClose = onClose,
                // repository = repository
            )
        }
        ScreenState.GEAR_LIST -> {
            GearSelectionScreen(
                currentGear = uiState.currentGear,
                onGearSelected = { selectedGear ->
                    uiState.currentGear = selectedGear
                    currentScreen = ScreenState.HOME // Go back after selection
                }
            )
        }
    }
}