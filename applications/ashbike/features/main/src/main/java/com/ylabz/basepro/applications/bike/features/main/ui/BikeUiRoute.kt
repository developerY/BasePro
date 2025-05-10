package com.ylabz.basepro.applications.bike.features.main.ui

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.features.main.ui.components.ErrorScreen
import com.ylabz.basepro.applications.bike.features.main.ui.components.IdleScreen
import com.ylabz.basepro.applications.bike.features.main.ui.components.LoadingScreen
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.BikeDashboardContent
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.WaitingForGpsScreen
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.ConnectionStatus
import com.ylabz.basepro.core.model.bike.NfcData
import com.ylabz.basepro.core.model.health.HealthScreenState
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import com.ylabz.basepro.feature.heatlh.ui.HealthViewModel
import com.ylabz.basepro.feature.nfc.ui.NfcUiState
import com.ylabz.basepro.feature.nfc.ui.NfcViewModel
import java.util.UUID


/**
 * Top-level route: wires ViewModels, collects their UI state,
 * and hands only UI state & events to child Composables.
 */
@OptIn(ExperimentalPermissionsApi::class)
@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun BikeUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit
) {
    // obtain view models
    val bikeViewModel   = hiltViewModel<BikeViewModel>()
    val healthViewModel = hiltViewModel<HealthViewModel>()
    val nfcViewModel    = hiltViewModel<NfcViewModel>()

    // collect states
    val bikeUiState   by bikeViewModel.uiState.collectAsState()
    val healthUiState by healthViewModel.uiState.collectAsState()
    val nfcUiState    by nfcViewModel.uiState.collectAsState()

    // Health Connect permission launcher
    val isHcAvailable = healthViewModel.healthSessionManager.availability.value ==
            HealthConnectClient.SDK_AVAILABLE
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = healthViewModel.permissionsLauncher
    ) { healthViewModel.onEvent(HealthEvent.RequestPermissions) }

    // kick off initial health permissions request if needed
    LaunchedEffect(healthUiState) {
        if (healthUiState is HealthUiState.Uninitialized) {
            healthViewModel.onEvent(HealthEvent.RequestPermissions)
        }
    }



    // 2) for location permission
    val context = LocalContext.current
    val permissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // choose screen based on bikeUiState
    when (bikeUiState) {
        // ————————————————————————————————————————————
        // 3) Loading / no GPS yet → ask for perms / show spinner
        is BikeUiState.WaitingForGps,
        BikeUiState.Idle -> {
            WaitingForGpsScreen(
                onRequestPermission = { permissionState.launchPermissionRequest() },
                onEnableGpsSettings = {
                    context.startActivity(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    )
                }
            )
        }

        // ————————————————————————————————————————————
        // 4) Error → show retry UI
        is BikeUiState.Error -> {
            ErrorScreen(
                errorMessage = (bikeUiState as BikeUiState.Error).message,
                onRetry = { bikeViewModel.onEvent(BikeEvent.StartRide) }
            )
        }

        // ————————————————————————————————————————————
        // 5) Success → drive the dashboard purely from UI state & events
        is BikeUiState.Success -> {
            val bikeData = (bikeUiState as BikeUiState.Success).bikeData
            BikeDashboardContent(
                modifier    = modifier.fillMaxSize(),
                bikeRideInfo = bikeData,
                onBikeEvent = bikeViewModel::onEvent,
                navTo       = navTo
            )
        }
        BikeUiState.Loading -> LoadingScreen()
    }
}