package com.ylabz.basepro.feature.heatlh.ui

import android.R.id.message
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import com.ylabz.basepro.core.model.health.HealthScreenState
import com.ylabz.basepro.feature.heatlh.ui.components.ErrorScreen
import com.ylabz.basepro.feature.heatlh.ui.components.HealthHeader
import com.ylabz.basepro.feature.heatlh.ui.components.HealthStartScreenFull
import com.ylabz.basepro.feature.heatlh.ui.components.LoadingScreen
import java.util.UUID

@Composable
fun HealthRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    viewModel: HealthViewModel = hiltViewModel()
) {

    // Gather state from the ViewModel.
    val isHealthConnectAvailable = viewModel.healthSessionManager.availability.value == HealthConnectClient.SDK_AVAILABLE
    val permissionsGranted by viewModel.permissionsGranted
    val permissions = viewModel.permissions
    val backgroundReadPermissions = viewModel.backgroundReadPermissions
    val backgroundReadAvailable by viewModel.backgroundReadAvailable
    val backgroundReadGranted by viewModel.backgroundReadGranted
    val healthUiState by viewModel.uiState.collectAsState()

    // Bundle everything into one state object.
    val bundledState = HealthScreenState(
        isHealthConnectAvailable = isHealthConnectAvailable,
        permissionsGranted = permissionsGranted,
        permissions = permissions,
        backgroundReadPermissions = backgroundReadPermissions,
        backgroundReadAvailable = backgroundReadAvailable,
        backgroundReadGranted = backgroundReadGranted,
    )


    //val healthUiState by remember { mutableStateOf(viewModel.uiState) }
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }
    val onPermissionsResult = { viewModel.initialLoad() }
    val permissionsLauncher =
        rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
            onPermissionsResult()
        }


    LaunchedEffect(healthUiState) {
        // If the initial data load has not taken place, attempt to load the data.
        if (healthUiState is HealthUiState.Uninitialized) {
            onPermissionsResult()
        }

        // The [ExerciseSessionViewModel.UiState] provides details of whether the last action was a
        // success or resulted in an error. Where an error occurred, for example in reading and
        // writing to Health Connect, the user is notified, and where the error is one that can be
        // recovered from, an attempt to do so is made.
        if (healthUiState is HealthUiState.Error && errorId.value != (healthUiState as HealthUiState.Error).uuid) {
            //onError(healthUiState.exception)
            //errorId.value = healthUiState.uuid
        }
    }

    Column(
       modifier = modifier
           .fillMaxSize()

    ) {
        // Display the current UI state in a Text field for debugging purposes
        val activity = LocalContext.current as? ComponentActivity

        when (healthUiState) {
            is HealthUiState.PermissionsRequired -> HealthFeatureWithPermissions {
                // Launch the permissions request
                permissionsLauncher.launch(viewModel.permissions)
            }

            //is HealthUiState.Success -> HealthDataScreen((healthUiState as HealthUiState.Success).healthData)


            is HealthUiState.Success -> {
                Column() {
                    HealthStartScreenFull(
                        modifier = modifier,
                        healthPermState = bundledState,
                        sessionsList = (healthUiState as HealthUiState.Success).healthData,
                        onPermissionsLaunch = { values ->
                            permissionsLauncher.launch(values)
                        },
                        onEvent = { event -> viewModel.onEvent(event) },
                        navTo = navTo,
                    )
                }
            }


            /*is HealthUiState.Success -> {
                HealthHeader(
                    healthPermState = bundledState,
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    backgroundReadPermissions = viewModel.backgroundReadPermissions,
                    activity = activity
                )
            }*/

            is HealthUiState.Error -> ErrorScreen(
                message = "Error: ${(healthUiState as HealthUiState.Error).message}",
                onRetry = { viewModel.initialLoad() },
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