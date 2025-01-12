package com.ylabz.basepro.feature.wearos.health.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.feature.wearos.health.ui.components.ErrorScreenWear
import com.ylabz.basepro.feature.wearos.health.ui.components.HealthFeatureWithPermissionsWear
import com.ylabz.basepro.feature.wearos.health.ui.components.HealthStartScreenWear
import com.ylabz.basepro.feature.wearos.health.ui.components.LoadingScreenWear
import java.util.UUID

@Composable
fun WearHealthRoute(
    navController: NavController,
    viewModel: HealthViewModel = hiltViewModel()
) {
    val healthUiState by viewModel.uiState.collectAsState()
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }
    val onPermissionsResult = { viewModel.initialLoad() }
    val permissionsLauncher =
        rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
            onPermissionsResult()
        }

    LaunchedEffect(healthUiState) {
        if (healthUiState is HealthUiState.Uninitialized) {
            onPermissionsResult()
        }

        if (
            healthUiState is HealthUiState.Error &&
            errorId.value != (healthUiState as HealthUiState.Error).uuid
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
            is HealthUiState.PermissionsRequired -> {
                HealthFeatureWithPermissionsWear {
                    permissionsLauncher.launch(viewModel.permissions)
                }
            }

            is HealthUiState.Success -> {
                // This is your “healthy data” screen.
                // On Wear OS you might use a ScalingLazyColumn, etc.
                HealthStartScreenWear(
                    navController = navController,
                    healthData = (healthUiState as HealthUiState.Success).healthData,
                    onEvent = { event -> viewModel.onEvent(event) },
                    onRequestPermissions = { values ->
                        //permissionsLauncher.launch(values)
                    }
                )
            }

            is HealthUiState.Error -> {
                // Show an error UI
                ErrorScreenWear(
                    message = "Error: ${(healthUiState as HealthUiState.Error).message}",
                    onRetry = { viewModel.initialLoad() },
                )
            }

            is HealthUiState.Uninitialized -> {
                // If still uninitialized, try initial load
                viewModel.initialLoad()
            }

            is HealthUiState.Loading -> {
                LoadingScreenWear()
            }
        }
    }
}
