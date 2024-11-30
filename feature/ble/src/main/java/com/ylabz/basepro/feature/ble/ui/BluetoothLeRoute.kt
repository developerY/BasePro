package com.ylabz.basepro.feature.ble.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.core.data.repository.BluetoothDeviceInfo

@Composable
fun BluetoothLeRoute(
    paddingValues: PaddingValues,
    navTo: (String) -> Unit,
    viewModel: BluetoothLeViewModel = hiltViewModel()
) {
    //val healthUiState by remember { mutableStateOf(viewModel.uiState) }
    val uiState = viewModel.uiState.collectAsState().value
    Text("BLE")

    when (uiState) {
        is BluetoothLeUiState.Loading -> LoadingScreen()
        is BluetoothLeUiState.Success -> BluetoothLeSuccessScreen(devices = uiState.devices)
        is BluetoothLeUiState.Error -> ErrorScreen(message = uiState.message)
    }
}

// Loading screen
@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

// Success screen showing BLE devices
@Composable
fun BluetoothLeSuccessScreen(devices: List<com.ylabz.basepro.core.data.repository.BluetoothDeviceInfo>) {
    LazyColumn {
        items(devices) { device ->
            Text(text = "${device.name} (${device.address})", modifier = Modifier.padding(16.dp))
        }
    }
}

// Error screen
@Composable
fun ErrorScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}
