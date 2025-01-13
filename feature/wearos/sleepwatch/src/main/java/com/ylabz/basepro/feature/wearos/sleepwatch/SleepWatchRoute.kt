package com.ylabz.basepro.feature.wearos.sleepwatch

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.feature.wearos.sleepwatch.components.ErrorScreenWear
import com.ylabz.basepro.feature.wearos.sleepwatch.components.LoadingScreenWear
import com.ylabz.basepro.feature.wearos.sleepwatch.components.SleepWatchStartScreenWear

import java.util.UUID

@Composable
fun SleepWatchRoute(
    navController: NavController,
    viewModel: SleepWatchViewModel = hiltViewModel()
) {
    val healthUiState by viewModel.uiState.collectAsState()
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }
    val onPermissionsResult = { viewModel.initialLoad() }
    val permissionsLauncher =
        rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
            onPermissionsResult()
        }

    LaunchedEffect(healthUiState) {
        if (healthUiState is SleepWatchUiState.Uninitialized) {
            onPermissionsResult()
        }

        if (
            healthUiState is SleepWatchUiState.Error &&
            errorId.value != (healthUiState as SleepWatchUiState.Error).uuid
        ) {
            // Example: You can show a Wear OS-specific error UI or toast
            // errorId.value = (healthUiState as HealthUiState.Error).uuid
        }
    }

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
        when (healthUiState) {
            is SleepWatchUiState.Success -> {
                // This is your “healthy data” screen.
                // On Wear OS you might use a ScalingLazyColumn, etc.
                SleepWatchStartScreenWear(
                    navController = navController,
                    onEvent = { event -> viewModel.onEvent(event) },
                    onRequestPermissions = { values ->
                        //permissionsLauncher.launch(values)
                    }
                )
            }

            is SleepWatchUiState.Error -> {
                // Show an error UI
                ErrorScreenWear(
                    message = "Error: ${(healthUiState as SleepWatchUiState.Error).message}",
                    onRetry = { viewModel.initialLoad() },
                )
            }

            is SleepWatchUiState.Uninitialized -> {
                // If still uninitialized, try initial load
                viewModel.initialLoad()
            }

            is SleepWatchUiState.Loading -> {
                LoadingScreenWear()
            }
        }
    }
}
