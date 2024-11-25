package com.ylabz.basepro.feature.heatlh.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.WeightRecord
import androidx.navigation.NavController
import com.ylabz.basepro.core.model.health.SleepSessionData
import com.ylabz.basepro.feature.heatlh.ui.components.ErrorScreen
import com.ylabz.basepro.feature.heatlh.ui.components.HealthStartScreen
import com.ylabz.basepro.feature.heatlh.ui.components.LoadingScreen

@Composable
fun HealthRoute(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HealthViewModel = hiltViewModel()
) {

    //val healthUiState by remember { mutableStateOf(viewModel.uiState) }
    val healthUiState by viewModel.uiState.collectAsState()


    val permissionsGranted by viewModel.permissionsGranted
    val sessionsList by viewModel.sessionsList
    val permissions = viewModel.permissions
    val backgroundReadPermissions = viewModel.backgroundReadPermissions
    val backgroundReadAvailable by viewModel.backgroundReadAvailable
    val backgroundReadGranted by viewModel.backgroundReadGranted
    val onPermissionsResult = { viewModel.initialLoad() }

    Column {
        // Display the current UI state in a Text field for debugging purposes
        Text(
            text = "Current UI State: ${healthUiState}",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        when (healthUiState) {
            is HealthUiState.Success -> LoadingScreen()
            is HealthUiState.PermissionsRequired -> HealthFeatureWithPermissions {
                // Launch the permissions request
                //permissionsLauncher.launch(viewModel.permissions.toTypedArray())
            }

            is HealthUiState.Success -> HealthDataScreen((healthUiState as HealthUiState.Success).healthData)
            is HealthUiState.Error -> ErrorScreen(
                message = "Error: ",//(healthUiState as HealthUiState.Error).message,
                onRetry = { viewModel.initialLoad() }
            )

            is HealthUiState.Uninitialized -> {
                viewModel.initialLoad()
            }

            is HealthUiState.Done -> {
                HealthStartScreen(navController = navController)
            }

            HealthUiState.Loading -> TODO()
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
fun HealthDataScreen(healthData: List<WeightRecord>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(healthData) { session ->
            Text("Exercise Session: ${session.weight}")
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
