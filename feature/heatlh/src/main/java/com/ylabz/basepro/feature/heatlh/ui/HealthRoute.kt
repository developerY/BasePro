package com.ylabz.basepro.feature.heatlh.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.WeightRecord
import androidx.navigation.NavController
import com.ylabz.basepro.core.model.health.SleepSessionData
import com.ylabz.basepro.feature.heatlh.ui.components.ErrorScreen
import com.ylabz.basepro.feature.heatlh.ui.components.HealthDataScreen
import com.ylabz.basepro.feature.heatlh.ui.components.HealthFeatureWithPermissions
import com.ylabz.basepro.feature.heatlh.ui.components.HealthStartScreen
import com.ylabz.basepro.feature.heatlh.ui.components.LoadingScreen
import java.util.UUID

@Composable
fun HealthRoute(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HealthViewModel = hiltViewModel()
) {

    //val healthUiState by remember { mutableStateOf(viewModel.uiState) }
    val healthUiState by viewModel.uiState.collectAsState()
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }


    val isHealthConnectAvailable = remember { viewModel.healthSessionManager.availability.value == HealthConnectClient.SDK_AVAILABLE }
    val permissionsGranted by viewModel.permissionsGranted
    //val sessionsList by viewModel.sessionsList
    val permissions = viewModel.permissions
    val backgroundReadPermissions = viewModel.backgroundReadPermissions
    val backgroundReadAvailable by viewModel.backgroundReadAvailable
    val backgroundReadGranted by viewModel.backgroundReadGranted
    val onPermissionsResult = { viewModel.initialLoad() }
    val permissionsLauncher =
        rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
            onPermissionsResult()
        }

    LaunchedEffect(healthUiState) {
        if (healthUiState is HealthUiState.Uninitialized) {
            viewModel.initialLoad()
        }

        /*if (healthUiState is HealthUiState.Error.Exception && errorId.value != healthUiState.uuid) {
            //onError(healthUiState.exception)
            errorId.value = healthUiState.uuid
        }*/
    }

    Column(
       modifier = Modifier
           .fillMaxSize()
           .padding(paddingValues)
    ) {
        // Display the current UI state in a Text field for debugging purposes
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(4.dp),
            onClick = {
                viewModel.insertExerciseSession()
            }
        ) {
            Text("Insert Exercise Session")
        }
        when (healthUiState) {
            is HealthUiState.PermissionsRequired -> HealthFeatureWithPermissions {
                // Launch the permissions request
                //permissionsLauncher.launch(viewModel.permissions.toTypedArray())
            }

            //is HealthUiState.Success -> HealthDataScreen((healthUiState as HealthUiState.Success).healthData)

            is HealthUiState.Success -> {
                HealthStartScreen(
                    navController = navController,
                    paddingValues = paddingValues,
                    onEvent = { event -> viewModel.onEvent(event) },
                    sessionsList = (healthUiState as HealthUiState.Success).healthData
                )
            }


            is HealthUiState.Error -> ErrorScreen(
                message = "Error: ",//(healthUiState as HealthUiState.Error).message,
                onRetry = { viewModel.initialLoad() }
            )

            is HealthUiState.Uninitialized -> {
                viewModel.initialLoad()
            }

            is HealthUiState.Loading -> LoadingScreen()
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
    Text("Here is the list of health data: ${healthData.size}")
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