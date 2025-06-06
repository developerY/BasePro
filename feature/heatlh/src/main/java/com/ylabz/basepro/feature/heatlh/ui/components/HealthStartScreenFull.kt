package com.ylabz.basepro.feature.heatlh.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.ylabz.basepro.core.model.health.HealthScreenState
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
import kotlinx.coroutines.CoroutineScope

@Composable
fun HealthStartScreenFull(
    modifier: Modifier = Modifier,
    healthPermState: HealthScreenState,
    sessionsList: List<ExerciseSessionRecord>,
    scope: CoroutineScope = rememberCoroutineScope(),
    onEvent: (HealthEvent) -> Unit,
    onPermissionsLaunch: (Set<String>) -> Unit,
    navTo: (String) -> Unit,
) {

    val activity = LocalContext.current as? ComponentActivity

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header: Availability and Settings
        /*HealthHeader(
            healthPermState = healthPermState,
            onPermissionsLaunch = onPermissionsLaunch,
            backgroundReadPermissions = healthPermState.backgroundReadPermissions,
            activity = activity
        )*/

        // Action Buttons: Add and Delete
        HealthActions(
            onInsertClick = {onEvent(HealthEvent.TestInsert) },
            onDeleteAllClick = { onEvent(HealthEvent.DeleteAll) },
            onReadAllClick = { onEvent(HealthEvent.ReadAll) }
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
            navTo = navTo
        )
        Text("Session List")
    }
}

@Preview
@Composable
fun HealthStartScreenFullPreview() {
    val healthPermState = HealthScreenState(
        isHealthConnectAvailable = true,
        permissionsGranted = true,
        permissions = setOf("permission1", "permission2"),
        backgroundReadPermissions = setOf("backgroundReadPermission1"),
        backgroundReadAvailable = true,
        backgroundReadGranted = true,
    )
    val sessionsList = emptyList<ExerciseSessionRecord>()
    HealthStartScreenFull(
        healthPermState = healthPermState,
        sessionsList = sessionsList,
        onEvent = {},
        onPermissionsLaunch = {},
        navTo = {})
}
