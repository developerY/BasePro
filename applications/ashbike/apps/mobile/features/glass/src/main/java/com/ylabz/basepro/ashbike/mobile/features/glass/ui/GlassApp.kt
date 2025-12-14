package com.ylabz.basepro.ashbike.mobile.features.glass.ui
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


// Simple Enum to handle local navigation
enum class ScreenState {
    HOME,
    GEAR_LIST
}

@Composable
fun GlassApp(onClose: () -> Unit) {
    // Top-level state for the app
    var currentScreen by remember { mutableStateOf(ScreenState.HOME) }
    var currentGear by remember { mutableIntStateOf(1) }
    val maxGear = 12

    // Simple Navigation Switcher
    when (currentScreen) {
        ScreenState.HOME -> {
            HomeScreen(
                currentGear = currentGear,
                onGearChange = { newGear ->
                    // Boundary checks
                    if (newGear in 1..maxGear) currentGear = newGear
                },
                onOpenGearList = { currentScreen = ScreenState.GEAR_LIST },
                onClose = onClose
            )
        }
        ScreenState.GEAR_LIST -> {
            GearSelectionScreen(
                currentGear = currentGear,
                onGearSelected = { selectedGear ->
                    currentGear = selectedGear
                    currentScreen = ScreenState.HOME // Go back after selection
                }
            )
        }
    }
}