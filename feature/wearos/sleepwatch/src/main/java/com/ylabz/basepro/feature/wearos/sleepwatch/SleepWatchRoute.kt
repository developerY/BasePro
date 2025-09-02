package com.ylabz.basepro.feature.wearos.sleepwatch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.ylabz.basepro.feature.wearos.sleepwatch.components.ErrorScreenWear
import com.ylabz.basepro.feature.wearos.sleepwatch.components.LoadingScreenWear
import com.ylabz.basepro.feature.wearos.sleepwatch.components.SleepWatchStartScreenWear

@Composable
fun SleepWatchRoute(
    navController: NavController,
    viewModel: SleepWatchViewModel = hiltViewModel()
) {
    val sleepWatchUiState by viewModel.uiState.collectAsState()

    // A typical Wear Scaffold might use TimeText, Vignette, PositionIndicator, etc.
    Scaffold(
        vignette = {
            Vignette(vignettePosition = VignettePosition.TopAndBottom)
        },
        timeText = {
            // Shows time at the top by default
            TimeText()
        }
    ) {
        // Here’s where you show different composables based on the UI state
        when (sleepWatchUiState) {
            is SleepWatchUiState.Success -> {
                // This is your “healthy data” screen.
                // On Wear OS you might use a ScalingLazyColumn, etc.
                SleepWatchStartScreenWear(
                    navController = navController,
                    onEvent = { event -> viewModel.onEvent(event) },
                    onRequestPermissions = { values ->
                        //permissionsLauncher.launch(values)
                    },
                    data = (sleepWatchUiState as SleepWatchUiState.Success).healthData
                )
            }

            is SleepWatchUiState.Error -> {
                // Show an error UI
                ErrorScreenWear(
                    message = "Error: ${(sleepWatchUiState as SleepWatchUiState.Error).message}",
                    onRetry = { viewModel.initialLoad() },
                )
            }

            is SleepWatchUiState.Uninitialized -> {
                // If still uninitialized, try initial load
                viewModel.initialLoad()
                LoadingScreenWear()
            }

            is SleepWatchUiState.Loading -> {
                LoadingScreenWear()
            }
        }
    }
}
