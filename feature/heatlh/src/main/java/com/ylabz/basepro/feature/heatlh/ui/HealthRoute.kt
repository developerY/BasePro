package com.ylabz.basepro.feature.heatlh.ui

import android.R.id.message
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import com.ylabz.basepro.core.model.health.HealthScreenState
import com.ylabz.basepro.feature.heatlh.ui.components.ErrorScreen
import com.ylabz.basepro.feature.heatlh.ui.components.HealthHeader
import com.ylabz.basepro.feature.heatlh.ui.components.LoadingScreen
import java.util.UUID

@Composable
fun HealthRoute(
    modifier: Modifier = Modifier,
    viewModel: HealthViewModel = hiltViewModel()
) {
    val healthUiState by viewModel.uiState.collectAsState()

    // This launcher logic remains essential for handling the side effect.
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = viewModel.permissionsLauncher,
        onResult = { viewModel.onEvent(HealthEvent.LoadHealthData) }
    )

    // --- Add the initial event trigger here ---
    LaunchedEffect(Unit) {
        // 1. Tell the ViewModel to load the initial state
        viewModel.onEvent(HealthEvent.LoadHealthData)

        // 2. Continue listening for side effects
        viewModel.sideEffect.collect { effect ->
            if (effect is HealthSideEffect.LaunchPermissions) {
                permissionsLauncher.launch(effect.permissions)
            }
        }
    }

    // The new, simpler UI
    Box(modifier = modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        when (val state = healthUiState) {
            is HealthUiState.Success -> {
                PermissionsGrantedScreen()
            }
            is HealthUiState.PermissionsRequired -> {
                PermissionsNotGrantedScreen(
                    onEnableClick = { viewModel.onEvent(HealthEvent.RequestPermissions) }
                )
            }
            is HealthUiState.Error -> {
                ErrorScreen(message = state.message, onRetry = { viewModel.onEvent(HealthEvent.Retry) })
            }
            else -> { // Loading and Uninitialized
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun PermissionsNotGrantedScreen(onEnableClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Enable Health Connect", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Sync your ride data with Google Health Connect to track your progress and share it with other apps.",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onEnableClick) {
            Text("Enable")
        }
    }
}

@Composable
private fun PermissionsGrantedScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Success",
            tint = Color.Green,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Health Connect Enabled", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "To turn this off, you must revoke permissions for this app in your phone's System Settings.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
@Preview
@Composable
fun HealthRoutePreview() {
    HealthRoute(
        viewModel = hiltViewModel()
    )
}

@Preview
@Composable
fun HealthFeatureWithPermissionsPreview() {
    HealthFeatureWithPermissions(
        onRequestPermissions = {}
    )
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