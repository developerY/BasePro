package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

// 1. IMPORT THE SHARED SERVICE
import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ylabz.basepro.applications.bike.features.main.service.BikeForegroundService
import com.ylabz.basepro.ashbike.wear.presentation.PermissionRationaleContent
import com.ylabz.basepro.core.model.bike.BikeRideInfo

// 2. Stateful Screen (Handles Logic & Permissions)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WearBikeScreen(
    viewModel: WearBikeViewModel = hiltViewModel(), // Uses your Service VM
    onNavigateToDetail: (String) -> Unit // <--- Received from App
) {
    // Define required permissions for the underlying BikeForegroundService
    val permissionsToRequest = buildList {
        add(Manifest.permission.BODY_SENSORS)
        add(Manifest.permission.ACTIVITY_RECOGNITION)
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        add(Manifest.permission.POST_NOTIFICATIONS)
    }

    val permissionState = rememberMultiplePermissionsState(permissions = permissionsToRequest)


    ScreenScaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            if (permissionState.allPermissionsGranted) {
                // Connect to Service
                val context = LocalContext.current

                // 2. UPDATE VARIABLE TYPE to BikeForegroundService
                var service by remember { mutableStateOf<BikeForegroundService?>(null) }

                // 3. COLLECT STATE (Uses 'rideState' from shared service)
                // 1. Collect Data (Binding is still needed for Reading Data)
                val rideInfoState = service?.rideInfo?.collectAsState()
                val rideInfo = rideInfoState?.value ?: BikeRideInfo.initial()
                // 1. Collect the single UI State
                val uiState by viewModel.uiState.collectAsState() // <--- Easy!


                DisposableEffect(context) {
                    val connection = object : ServiceConnection {
                        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
                            service = (binder as BikeForegroundService.LocalBinder).getService()
                        }
                        override fun onServiceDisconnected(arg0: ComponentName) { service = null }
                    }
                    // Bind to the shared service to read data
                    val intent = Intent(context, BikeForegroundService::class.java)
                    context.bindService(intent, connection, Context.BIND_AUTO_CREATE)

                    onDispose { context.unbindService(connection) }
                }

                // 2. Helper function to send Commands
                fun sendServiceCommand(action: String) {
                    val intent = Intent(context, BikeForegroundService::class.java).apply {
                        this.action = action
                    }
                    context.startForegroundService(intent)
                }

                // Pass data to Stateless UI (Which now contains the Pager)
                BikeControlContent(
                    rideInfo = rideInfo,
                    isRecording = uiState.isRecording,
                    // 3. TRIGGER COMMANDS VIA INTENTS
                    onStart = viewModel::startRide,
                    onStop = viewModel::stopRide,
                    onHistoryClick = onNavigateToDetail // <--- Connect the dots!
                )

            } else {
                PermissionRationaleContent(
                    onRequestPermission = { permissionState.launchMultiplePermissionRequest() }
                )
            }
        }
    }
}