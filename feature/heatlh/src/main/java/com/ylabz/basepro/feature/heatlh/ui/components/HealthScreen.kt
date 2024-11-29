package com.ylabz.basepro.feature.heatlh.ui.components

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ylabz.basepro.feature.heatlh.R
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import com.ylabz.basepro.feature.heatlh.ui.HealthViewModel
import kotlinx.coroutines.CoroutineScope
import java.time.ZonedDateTime

@Composable
fun HealthStartScreen(
    viewModel: HealthViewModel = hiltViewModel(),
    navController: NavController,
    paddingValues: PaddingValues,
    scope: CoroutineScope = rememberCoroutineScope(),
    onEvent: (HealthEvent) -> Unit,
    sessionsList: List<ExerciseSessionRecord>,
    onPermissionsLaunch: (Set<String>) -> Unit,
) {
    val isHealthConnectAvailable = remember { viewModel.healthSessionManager.availability.value == HealthConnectClient.SDK_AVAILABLE }
    val permissionsGranted by viewModel.permissionsGranted
    //val sessionsList by viewModel.sessionsList
    val permissions = viewModel.permissions
    val backgroundReadPermissions = viewModel.backgroundReadPermissions
    val backgroundReadAvailable by viewModel.backgroundReadAvailable
    val backgroundReadGranted by viewModel.backgroundReadGranted
    val onPermissionsResult = { viewModel.initialLoad() }
    val healthUiState by viewModel.uiState.collectAsState()
    val activity = LocalContext.current as? ComponentActivity


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row {
            Text(
                text = if (isHealthConnectAvailable) {
                    "available"
                } else {
                    "not available"
                },
                color = if (isHealthConnectAvailable) Color.Green else Color.Red,
                fontWeight = if (isHealthConnectAvailable) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.LightGray)
                    .padding(8.dp), // Extra padding inside the background for better spacing
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = {
                    val settingsIntent = Intent()
                    settingsIntent.action = HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS
                    activity?.startActivity(settingsIntent)
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Settings")
            }
            if (!backgroundReadGranted) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(4.dp),
                    onClick = {
                        onPermissionsLaunch(backgroundReadPermissions)
                    },
                    enabled = backgroundReadAvailable,
                ) {
                    if (backgroundReadAvailable) {
                        Text("Request Background Read")
                    } else {
                        Text("Background Read Not Available")
                    }
                }
            }
        }
        Row {
            // Delete All Button
            Button(
                onClick = { onEvent(HealthEvent.Insert) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Add")
            }
            Button(
                onClick = { onEvent(HealthEvent.DeleteAll) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Delete All!")
            }
        }
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
                onPermissionsLaunch = onPermissionsLaunch
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