package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
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
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ylabz.basepro.ashbike.wear.presentation.PermissionRationaleContent
import com.ylabz.basepro.ashbike.wear.service.BikeWearService
import com.ylabz.basepro.core.model.bike.BikeRideInfo

// 2. Stateful Screen (Handles Logic & Permissions)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WearBikeScreen() {
    // Define required permissions for the underlying BikeForegroundService
    val permissionsToRequest = buildList {
        add(Manifest.permission.BODY_SENSORS)
        add(Manifest.permission.ACTIVITY_RECOGNITION)
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= 33) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val permissionState = rememberMultiplePermissionsState(permissions = permissionsToRequest)

    // ScreenScaffold handles the scroll state and time text (if needed) for this specific screen
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
                var service by remember { mutableStateOf<BikeWearService?>(null) }

                // Collect the REAL BikeRideInfo state
                val rideInfo by service?.exerciseState?.collectAsState(initial = BikeRideInfo.initial())
                    ?: remember { mutableStateOf(BikeRideInfo.initial()) }

                DisposableEffect(context) {
                    val connection = object : ServiceConnection {
                        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
                            service = (binder as BikeWearService.LocalBinder).getService()
                        }
                        override fun onServiceDisconnected(arg0: ComponentName) { service = null }
                    }
                    val intent = Intent(context, BikeWearService::class.java)
                    // BIND_AUTO_CREATE starts the service if it's not running
                    context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
                    onDispose { context.unbindService(connection) }
                }

                // Pass data to Stateless UI
                BikeControlContent(
                    rideInfo = rideInfo,
                    onStart = { service?.startRide() },
                    onStop = { service?.stopRide() }
                )

            } else {
                PermissionRationaleContent(
                    onRequestPermission = { permissionState.launchMultiplePermissionRequest() }
                )
            }
        }
    }
}