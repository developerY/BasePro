package com.ylabz.basepro.feature.heatlh.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ylabz.basepro.core.model.health.SleepSessionData

@Composable
fun HealthRoute(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HealthViewModel = hiltViewModel()
) {

    val healthUiState by remember { mutableStateOf(viewModel.healthUiState) }
    val permissionsGranted by viewModel.permissionsGranted
    val sessionsList by viewModel.sessionsList
    val permissions = viewModel.permissions
    val backgroundReadPermissions = viewModel.backgroundReadPermissions
    val backgroundReadAvailable by viewModel.backgroundReadAvailable
    val backgroundReadGranted by viewModel.backgroundReadGranted
    val onPermissionsResult = { viewModel.initialLoad() }

    viewModel.initialLoad()

    // UI State Handling
    when (healthUiState) {
        is HealthUiState.Loading -> LoadingScreen()
        is HealthUiState.PermissionsRequired -> HealthFeatureWithPermissions {
            // Launch the permissions request
            //permissionsLauncher.launch(viewModel.permissions.toTypedArray())
        }
        is HealthUiState.Success -> HealthDataScreen((healthUiState as HealthUiState.Success).healthData)
        is HealthUiState.Error -> ErrorScreen(
            message = "Error: ",//(healthUiState as HealthUiState.Error).message,
            onRetry = { viewModel.initialLoad() }
        )
        is HealthUiState.Uninitialized -> Text("Not initialized")
        HealthUiState.Done -> {
            // Handle done state if needed
        }
    }
}

@Composable
fun HealthFeatureWithPermissions(onRequestPermissions: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Health permissions are required to proceed.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRequestPermissions) {
                Text("Grant Permissions")
            }
        }
    }
}

@Composable
fun HealthDataScreen(healthData: List<SleepSessionData>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(healthData) { session ->
            Text("Exercise Session: ${session.title}")
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Loading...")
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Error: $message")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
