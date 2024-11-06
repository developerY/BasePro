package com.ylabz.basepro.feature.heatlh.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.ylabz.basepro.feature.heatlh.ui.components.ErrorScreen
import com.ylabz.basepro.feature.heatlh.ui.components.HealthDataScreen
import com.ylabz.basepro.feature.heatlh.ui.components.HealthFeatureWithPermissions
import com.ylabz.basepro.feature.heatlh.ui.components.LoadingScreen

@Composable
fun HealthRoute(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HealthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is HealthUIState.Loading -> LoadingScreen()
        is HealthUIState.PermissionsRequired -> HealthFeatureWithPermissions()// { viewModel.onEvent(HealthEvent.RequestPermissions) }
        is HealthUIState.Success -> HealthDataScreen((uiState as HealthUIState.Success).healthData)
        is HealthUIState.Error -> ErrorScreen(
            message = (uiState as HealthUIState.Error).message,
            onRetry = { viewModel.onEvent(HealthEvent.Retry) }
        )
    }
}
