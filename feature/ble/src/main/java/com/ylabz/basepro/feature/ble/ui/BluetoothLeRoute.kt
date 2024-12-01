package com.ylabz.basepro.feature.ble.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ylabz.basepro.core.data.repository.BluetoothDeviceInfo
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

    // Observe permission state and send events to the ViewModel
    LaunchedEffect(permissionState.allPermissionsGranted) {
        when {
            permissionState.allPermissionsGranted -> viewModel.onEvent(BluetoothLeEvent.PermissionsGranted)
            !permissionState.shouldShowRationale -> viewModel.onEvent(BluetoothLeEvent.PermissionsDenied)
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
            is BluetoothLeUiState.Success -> BluetoothLeSuccessScreen(uiState.devices)
            is BluetoothLeUiState.Error -> ErrorScreen(uiState.message)
        }
    }
}
