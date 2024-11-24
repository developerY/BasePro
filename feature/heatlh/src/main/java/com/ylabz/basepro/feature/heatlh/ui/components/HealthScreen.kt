package com.ylabz.basepro.feature.heatlh.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ylabz.basepro.feature.heatlh.R
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import com.ylabz.basepro.feature.heatlh.ui.HealthViewModel
import kotlinx.coroutines.CoroutineScope
import java.time.ZonedDateTime

@Composable
fun HealthStartScreen(
    viewModel: HealthViewModel = hiltViewModel(),
    navController: NavController,
    scope: CoroutineScope = rememberCoroutineScope()
) {
    val isHealthConnectAvailable = remember { viewModel.healthSessionManager.availability.value == HealthConnectClient.SDK_AVAILABLE }
    val permissionsGranted by viewModel.permissionsGranted
    val sessionsList by viewModel.sessionsList
    val permissions = viewModel.permissions
    val backgroundReadPermissions = viewModel.backgroundReadPermissions
    val backgroundReadAvailable by viewModel.backgroundReadAvailable
    val backgroundReadGranted by viewModel.backgroundReadGranted
    val onPermissionsResult = { viewModel.initialLoad() }
    val permissionsLauncher =
        rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
            onPermissionsResult()
        }
    val healthUiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Display Health Permissions Screen with a defined height
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            HealthPermissionScreen(
                isHealthConnectAvailable = isHealthConnectAvailable,
                permissionsGranted = permissionsGranted,
                permissions = permissions,
                backgroundReadAvailable = backgroundReadAvailable,
                backgroundReadGranted = backgroundReadGranted,
                backgroundReadPermissions = backgroundReadPermissions,
                onReadClick = {
                    //viewModel.enqueueReadStepWorker()
                },
                sessionsList = sessionsList,
                uiState = healthUiState,
                onInsertClick = {
                    viewModel.insertExerciseSession()
                },
                onDetailsClick = { uid ->
                    navController.navigate("exercise_session_detail/$uid")
                },
                onError = { exception ->
                    //showExceptionSnackbar(scaffoldState, scope, exception)
                },
                onPermissionsResult = {
                    viewModel.initialLoad()
                },
                onPermissionsLaunch = { values ->
                    permissionsLauncher.launch(values)
                }
            )
        }

        // Label showing the number of items in the list
        Text(
            text = "Number of sessions: ${sessionsList.size}",
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyMedium
        )

        // Display session list with LazyColumn for a good scrollable UI
        LazyColumn(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(sessionsList) { session ->
                ExerciseSessionRow(
                    ZonedDateTime.ofInstant(session.startTime, session.startZoneOffset),
                    ZonedDateTime.ofInstant(session.endTime, session.endZoneOffset),
                    session.metadata.id,
                    session.title ?: "no title", //stringResource(R.string.no_title),
                    onDetailsClick = { uid ->
                        navController.navigate("exercise_session_detail/$uid")
                    },
                )
            }
        }
    }
}