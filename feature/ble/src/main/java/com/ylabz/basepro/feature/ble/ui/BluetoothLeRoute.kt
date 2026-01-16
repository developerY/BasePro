package com.ylabz.basepro.feature.ble.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
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
    viewModel: BluetoothLeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scanState by viewModel.scanState.collectAsState()
    val gattConnectionState by viewModel.gattConnectionState.collectAsState()
    val isStartButtonEnabled by viewModel.isStartButtonEnabled.collectAsState()
    val context = LocalContext.current
    val gattServicesList by viewModel.gattServicesList.collectAsState()

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
        StatusBar(
            permissionState = permissionState,
            onManagePermissionsClick = {
                permissionState.launchMultiplePermissionRequest()
            },
            scanState = scanState
        )

        when (val currentUiState = uiState) {
            BluetoothLeUiState.ShowBluetoothDialog -> {
                LaunchedEffect(Unit) {
                    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    val activity = context as Activity
                    activity.startActivityForResult(enableBluetoothIntent, 1)
                }
            }

            is BluetoothLeUiState.PermissionsRequired -> PermissionsRationale {
                permissionState.launchMultiplePermissionRequest()
            }

            is BluetoothLeUiState.PermissionsDenied -> PermissionsDenied {
                permissionState.launchMultiplePermissionRequest()
            }

            is BluetoothLeUiState.Loading -> LoadingScreen()

            is BluetoothLeUiState.DataLoaded -> {
                // V V V V V ADD THIS LOGGING LINE V V V V V
                android.util.Log.d(
                    "BluetoothLeRoute",
                    "In DataLoaded. Discovered Devices count: ${currentUiState.discoveredDevices.size}"
                )
                currentUiState.discoveredDevices.forEachIndexed { index, device ->
                    android.util.Log.d(
                        "BluetoothLeRoute",
                        "Device $index: Name='${device.name}', Address='${device.address}', RSSI=${device.rssi}"
                    )
                }

                // ^ ^ ^ ^ ^ END OF LOGGING ^ ^ ^ ^ ^


                BluetoothLeSuccessScreen(
                    scanState = scanState,
                    gattConnectionState = gattConnectionState,
                    activeDevice = currentUiState.activeDevice,
                    discoveredDevices = currentUiState.discoveredDevices,
                    isStartScanningEnabled = isStartButtonEnabled,
                    startScan = { viewModel.onEvent(BluetoothLeEvent.StartScan) },
                    stopScan = { viewModel.onEvent(BluetoothLeEvent.StopScan) },
                    connectToActiveDevice = { viewModel.onEvent(BluetoothLeEvent.ConnectToSensorTag) }, // Corrected: event name
                    readCharacteristics = { viewModel.onEvent(BluetoothLeEvent.ReadCharacteristics) },
                    gattServicesList = gattServicesList,
                    onDeviceSelected = { device ->
                        viewModel.onEvent(BluetoothLeEvent.SetActiveDevice(device)) // Corrected: event is now defined
                    }
                )
            }

            is BluetoothLeUiState.Error -> ErrorScreen(currentUiState.message)
        }
    }
}
