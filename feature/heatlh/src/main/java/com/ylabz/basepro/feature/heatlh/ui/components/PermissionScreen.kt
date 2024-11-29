package com.ylabz.basepro.feature.heatlh.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.health.connect.client.HealthConnectClient
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.feature.heatlh.ui.HealthViewModel


@Composable
fun PermissionScreen(
    modifier: Modifier = Modifier,
    onPermissionsLaunch: (Set<String>) -> Unit,
    viewModel: HealthViewModel = hiltViewModel(),

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
    if (!permissionsGranted) {

        Button(
            onClick = {
                onPermissionsLaunch(permissions)
            }
        ) {
            Text(text = "Request Permissions")
        }
    }
}
