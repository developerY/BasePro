package com.ylabz.basepro.feature.wearos.drunkwatch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.ylabz.basepro.feature.wearos.drunkwatch.components.DrunkWatchStartScreenWear
import com.ylabz.basepro.feature.wearos.drunkwatch.components.ErrorScreenWear
import com.ylabz.basepro.feature.wearos.drunkwatch.components.LoadingScreenWear
import java.util.UUID

@Composable
fun DrunkWatchRoute(
    navController: NavController,
    viewModel: DrunkWatchViewModel = hiltViewModel()
) {
    val drunkWatchUiState by viewModel.uiState.collectAsState()
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }
    val onPermissionsResult = { viewModel.initialLoad() }

    LaunchedEffect(drunkWatchUiState) {
        if (drunkWatchUiState is DrunkWatchUiState.Uninitialized) {
            onPermissionsResult()
        }

        if (
            drunkWatchUiState is DrunkWatchUiState.Error &&
            errorId.value != (drunkWatchUiState as DrunkWatchUiState.Error).uuid
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
        when (drunkWatchUiState) {

            is DrunkWatchUiState.Success -> {
                // This is your “healthy data” screen.
                // On Wear OS you might use a ScalingLazyColumn, etc.
                DrunkWatchStartScreenWear(
                    navController = navController,
                    onEvent = { event -> viewModel.onEvent(event) },
                    onRequestPermissions = { values ->
                        //permissionsLauncher.launch(values)
                    }
                )
            }

            is DrunkWatchUiState.Error -> {
                // Show an error UI
                ErrorScreenWear(
                    message = "Error: ${(drunkWatchUiState as DrunkWatchUiState.Error).message}",
                    onRetry = { viewModel.initialLoad() },
                )
            }

            is DrunkWatchUiState.Uninitialized -> {
                // If still uninitialized, try initial load
                //viewModel.initialLoad()
                LoadingScreenWear()
            }

            is DrunkWatchUiState.Loading -> {
                LoadingScreenWear()
            }
        }
    }
}
