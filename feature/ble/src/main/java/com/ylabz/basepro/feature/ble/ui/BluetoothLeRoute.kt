package com.ylabz.basepro.feature.ble.ui

import android.Manifest.permission.BLUETOOTH_ADMIN
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ylabz.basepro.core.util.Logging
import com.ylabz.basepro.feature.ble.ui.components.BluetoothLeSuccessScreen
import com.ylabz.basepro.feature.ble.ui.components.ErrorScreen
import com.ylabz.basepro.feature.ble.ui.components.LoadingScreen
import com.ylabz.basepro.feature.ble.ui.components.PermissionsDenied
import com.ylabz.basepro.feature.ble.ui.components.PermissionsRationale
import com.ylabz.basepro.feature.ble.ui.components.StatusBar

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothLeRoute(
    paddingValues: PaddingValues,
    navTo: (String) -> Unit,
    viewModel: BluetoothLeViewModel = hiltViewModel()
) {
    val TAG = "BluetoothLeRoute"
    //val healthUiState by remember { mutableStateOf(viewModel.uiState) }
    val uiState = viewModel.uiState.collectAsState().value
    val scanState by viewModel.scanState.collectAsState()
    val isStartButtonEnabled by viewModel.isStartButtonEnabled.collectAsState()
    val context = LocalContext.current


    // Define BLE permissions
    val blePermissions = listOf(
        android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.BLUETOOTH_CONNECT,
        android.Manifest.permission.BLUETOOTH_ADVERTISE,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
    )

    // Remember permission state
    val permissionState = rememberMultiplePermissionsState(permissions = blePermissions)
    // Observe the state of BLE permissions and notify the ViewModel of changes.
    // LaunchedEffect ensures this block of code is executed whenever the
    // 'permissionState.allPermissionsGranted' value changes. This effect runs
    // only while the Composable is in the Composition and cancels itself
    // if the Composable is recomposed with a different key or leaves the Composition.
    // If all required permissions are granted, it triggers the PermissionsGranted
    // event to proceed with BLE scanning. If permissions are denied and the rationale
    // should not be shown (e.g., the user permanently denied permissions), it triggers
    // the PermissionsDenied event to update the UI accordingly.
    // Observe permission state and send events to the ViewModel
    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            viewModel.onEvent(BluetoothLeEvent.PermissionsGranted)
        } else if (!permissionState.shouldShowRationale) {
            viewModel.onEvent(BluetoothLeEvent.PermissionsDenied)
        }
    }

    // Render the UI based on the current state
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        //PermissionStatusUI(permissionState) // Show BLE permission status visually
        // Status Bar at the top of the screen
        StatusBar(
            permissionState = permissionState,
            onManagePermissionsClick = {
                // Navigate to BLE permissions settings or trigger permission request
                permissionState.launchMultiplePermissionRequest()
            },
            scanState = scanState
        )

        when (uiState) {
            BluetoothLeUiState.ShowBluetoothDialog -> {
                LaunchedEffect(Unit) {
                    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    val activity = context as Activity
                    activity.startActivityForResult(enableBluetoothIntent, 1)
                }
            } // Show Bluetooth dialog and trigger permission request

            is BluetoothLeUiState.PermissionsRequired -> PermissionsRationale {
                permissionState.launchMultiplePermissionRequest() // Trigger permission request
            }

            is BluetoothLeUiState.PermissionsDenied -> PermissionsDenied {
                permissionState.launchMultiplePermissionRequest() // Trigger permission request
                // viewModel.onEvent(BluetoothLeEvent.PermissionsDenied)
            }

            is BluetoothLeUiState.Loading -> LoadingScreen()

            is BluetoothLeUiState.ScanDevices -> BluetoothLeSuccessScreen(
                scanState  = scanState,
                device = uiState.devices,
                isStartScanningEnabled = isStartButtonEnabled,
                startScan = { viewModel.onEvent(BluetoothLeEvent.StartScan) },
                stopScan = { viewModel.onEvent(BluetoothLeEvent.StopScan) } // Trigger rescan
            )

            is BluetoothLeUiState.Error -> ErrorScreen(uiState.message)

            is BluetoothLeUiState.Scanning -> {
                LaunchedEffect(Unit) {
                    viewModel.onEvent(BluetoothLeEvent.StopScan)
                }
            } // Handle stopped state if needed

            is BluetoothLeUiState.Stopped -> {
                LaunchedEffect(Unit) {
                    viewModel.onEvent(BluetoothLeEvent.StopScan)
                }
            } // Handle stopped state if needed

            //is BluetoothLeUiState.TiTagSensorFound -> {}

        }
    }
}

// Ex.
// https://github.com/santansarah/ble-scanner/blob/main/app/src/main/java/com/santansarah/scan/presentation/scan/Home.kt
