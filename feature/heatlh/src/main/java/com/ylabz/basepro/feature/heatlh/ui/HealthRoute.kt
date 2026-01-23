package com.ylabz.basepro.feature.heatlh.ui

////import androidx.compose.ui.tooling.preview.Preview
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ylabz.basepro.feature.health.R
import com.ylabz.basepro.feature.heatlh.ui.components.ErrorScreen

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
                Log.d(
                    "SettingsHealthRoute",
                    "LaunchPermissions side effect received IN SETTINGS. Launching its permissionsLauncher."
                ) // <-- ADD THIS LINE
                permissionsLauncher.launch(effect.permissions)
            }
        }
    }

    // The new, simpler UI
    Box(modifier = modifier
        .fillMaxSize()
        .padding(16.dp), contentAlignment = Alignment.Center) {
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
                ErrorScreen(
                    message = state.message,
                    onRetry = { viewModel.onEvent(HealthEvent.Retry) })
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
        Text(
            stringResource(id = R.string.health_route_enable_health_connect_title),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(id = R.string.health_route_enable_description),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onEnableClick) {
            Text(stringResource(id = R.string.health_route_action_enable))
        }
    }
}

@Composable
private fun PermissionsGrantedScreen() {
    // 1. Get the current context and create a launcher for the settings Intent
    LocalContext.current
    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { } // No action needed on result
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = stringResource(id = R.string.health_route_cd_success_icon),
            tint = Color.Green,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(id = R.string.health_route_health_connect_enabled_title),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 2. Add the new button to open settings
        OutlinedButton(
            onClick = {
                val intent = Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS)
                settingsLauncher.launch(intent)
            }
        ) {
            Text(stringResource(id = R.string.health_route_action_manage_permissions))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            stringResource(id = R.string.health_route_revoke_permissions_info),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// Commented out as flagged unused by IDE analysis (YYYY-MM-DD - AI Assistant)
/*
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
            Text(stringResource(id = R.string.health_route_permissions_required_message))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRequestPermissions) {
                Text(stringResource(id = R.string.health_route_action_grant_permissions))
            }
        }
    }
}
*/

/*
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
 */