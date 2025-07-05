package com.ylabz.basepro.applications.bike.features.main.ui

import android.Manifest
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.ylabz.basepro.applications.bike.features.main.ui.components.ErrorScreen
import com.ylabz.basepro.applications.bike.features.main.ui.components.LoadingScreen
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.BikeDashboardContent
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.WaitingForGpsScreen
import com.ylabz.basepro.feature.heatlh.ui.HealthViewModel
import com.ylabz.basepro.feature.nfc.ui.NfcViewModel

@OptIn(ExperimentalPermissionsApi::class)
@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun BikeUiRouteNew(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    viewModel: BikeViewModelNew // <<< MODIFIED LINE: Accept BikeViewModel as a parameter
) {
    // DO NOT call hiltViewModel() for BikeViewModel here. Use the passed-in 'viewModel'.
    // val bikeViewModelFromHilt = hiltViewModel<BikeViewModel>() // THIS LINE IS REMOVED/AVOIDED

    // Other ViewModels can still be obtained via hiltViewModel if they are scoped appropriately
    // and not meant to be the same instance as one from a parent composable.
    val healthViewModel = hiltViewModel<HealthViewModel>()
    val nfcViewModel = hiltViewModel<NfcViewModel>()

    val bikeUiState by viewModel.uiState.collectAsState() // <<< MODIFIED LINE: Use the passed-in viewModel
    val healthUiState by healthViewModel.uiState.collectAsState() // Assuming HealthUiState is the correct type
    val nfcUiState by nfcViewModel.uiState.collectAsState()       // Assuming NfcUiState is the correct type

    // Log the instance hash code for confirmation (can be removed after verifying)
    Log.d("BikeUiRoute_InstanceTest", "BikeUiRoute using BikeViewModel instance: ${viewModel.hashCode()}")
    // General state log for BikeUiRoute recomposition
    Log.d("BikeUiRoute_StateLog", "BikeUiRoute recomposing with bikeUiState: ${bikeUiState::class.java.simpleName}")


    val context = LocalContext.current
    // rememberPermissionState for location, used by WaitingForGpsScreen's onRequestPermission callback
    val permissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    when (val currentBikeUiState = bikeUiState) {
        is BikeUiState.WaitingForGps -> {
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
        is BikeUiState.Success -> {
            val bikeData = currentBikeUiState.bikeData
            if (bikeData.location != null && (bikeData?.location?.latitude != 0.0 || bikeData?.location?.longitude != 0.0)) {
                BikeDashboardContent(
                    modifier = modifier.fillMaxSize(),
                    bikeRideInfo = bikeData,
                    onBikeEvent = viewModel::onEvent, // <<< MODIFIED LINE: Use the passed-in viewModel
                    navTo = navTo
                )
            } else {
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
        }
        is BikeUiState.Error -> {
            ErrorScreen(
                errorMessage = currentBikeUiState.message,
                onRetry = { viewModel.onEvent(BikeEvent.StartRide) } // <<< MODIFIED LINE: Use the passed-in viewModel
            )
        }
        BikeUiState.Loading -> {
            LoadingScreen()
        }
        BikeUiState.Idle -> {
            // Decided to show LoadingScreen for Idle as well, or define a specific IdleScreen
            LoadingScreen()
        }
    }
}
