package com.ylabz.basepro.ashbike.wear.presentation

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ylabz.basepro.ashbike.wear.presentation.components.WearSpeedometer
import com.ylabz.basepro.ashbike.wear.service.BikeWearService
import com.ylabz.basepro.core.model.bike.BikeRideInfo

// 1. Root App Component
@Composable
fun AshBikeApp() {
    val navController = rememberSwipeDismissableNavController()

    // AppScaffold is the root container for M3 Wear apps
    AppScaffold {
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "ride_screen"
        ) {
            composable("ride_screen") {
                // This screen handles its own permissions and service binding
                WearBikeScreen()
            }
        }
    }
}

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

// 3. Stateless UI
@Composable
fun BikeControlContent(
    rideInfo: BikeRideInfo,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Background Speedometer
        WearSpeedometer(
            // BikeRideInfo provides speed in km/h automatically from BikeForegroundService
            currentSpeed = rideInfo.currentSpeed.toFloat(),
            modifier = Modifier.fillMaxSize()
        )

        // Overlay Stats
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top: Heart Rate
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 30.dp)) {
                Text(
                    text = "HR",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    // Handle nullable heartbeat
                    text = "${rideInfo.heartbeat ?: "--"}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Bottom: Distance & Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Text(
                    // BikeRideInfo.currentTripDistance is already in Kilometers
                    text = String.format("%.2f km", rideInfo.currentTripDistance),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Go")
                    }
                    Button(
                        onClick = onStop,
                        colors = ButtonDefaults.filledTonalButtonColors(),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Stop")
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionRationaleContent(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Need Permissions",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRequestPermission) {
            Text("Grant")
        }
    }
}