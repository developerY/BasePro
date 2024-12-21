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
    val permissions = viewModel.permissions
    val backgroundReadPermissions = viewModel.backgroundReadPermissions
    val backgroundReadAvailable by viewModel.backgroundReadAvailable
    val backgroundReadGranted by viewModel.backgroundReadGranted
    val healthUiState by viewModel.uiState.collectAsState()
    val activity = LocalContext.current as? ComponentActivity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header: Availability and Settings
        HealthHeader(
            isHealthConnectAvailable = isHealthConnectAvailable,
            backgroundReadGranted = backgroundReadGranted,
            backgroundReadAvailable = backgroundReadAvailable,
            permissions = permissions,
            onPermissionsLaunch = onPermissionsLaunch,
            backgroundReadPermissions = backgroundReadPermissions,
            activity = activity
        )

        // Action Buttons: Add and Delete
        HealthActions(
            onInsertClick = { onEvent(HealthEvent.Insert) },
            onDeleteAllClick = { onEvent(HealthEvent.DeleteAll) },
        )

        // Session List Header
        Text(
            text = "Number of sessions: ${sessionsList.size}",
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyMedium
        )

        // Session List
        SessionList(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth(),
            sessionsList = sessionsList,
            navController = navController
        )
    }
}
