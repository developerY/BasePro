package com.ylabz.basepro.feature.ble.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.Copy
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ylabz.basepro.feature.ble.ui.components.BluetoothLeSuccessScreen
import com.ylabz.basepro.feature.ble.ui.components.ErrorScreen
import com.ylabz.basepro.feature.ble.ui.components.LoadingScreen
import com.ylabz.basepro.feature.ble.ui.components.PermissionStatusUI
import com.ylabz.basepro.feature.ble.ui.components.PermissionsDenied
import com.ylabz.basepro.feature.ble.ui.components.PermissionsRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothLeRoute(
    paddingValues: PaddingValues,
    navTo: (String) -> Unit,
    viewModel: BluetoothLeViewModel = hiltViewModel()
) {
    //val healthUiState by remember { mutableStateOf(viewModel.uiState) }
    val uiState = viewModel.uiState.collectAsState().value
    Text("BLE")


    // Define BLE permissions
    val blePermissions = listOf(
        android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.BLUETOOTH_CONNECT,
        android.Manifest.permission.BLUETOOTH_ADVERTISE
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
            .padding(16.dp)
    ) {
        PermissionStatusUI(permissionState) // Show BLE permission status visually


        when (uiState) {
            is BluetoothLeUiState.PermissionsRequired -> PermissionsRationale {
                permissionState.launchMultiplePermissionRequest() // Trigger permission request
            }

            is BluetoothLeUiState.PermissionsDenied -> PermissionsDenied {
                permissionState.launchMultiplePermissionRequest() // Trigger permission request
                // viewModel.onEvent(BluetoothLeEvent.PermissionsDenied)
            }

            is BluetoothLeUiState.Loading -> LoadingScreen()

            is BluetoothLeUiState.Success -> BluetoothLeSuccessScreen(
                devices = uiState.devices,
                onRescan = { viewModel.onEvent(BluetoothLeEvent.FetchDevices) } // Trigger rescan
            )

            is BluetoothLeUiState.ClassicSuccess -> BluetoothLeSuccessScreen(
                devices = uiState.devices,
                onRescan = { viewModel.onEvent(BluetoothLeEvent.FetchDevices) } // Trigger rescan
            )


            is BluetoothLeUiState.Error -> ErrorScreen(uiState.message)

            is BluetoothLeUiState.Stopped -> {} // Handle stopped state if needed
        }
    }
}

// Ex.
// https://github.com/santansarah/ble-scanner/blob/main/app/src/main/java/com/santansarah/scan/presentation/scan/Home.kt
