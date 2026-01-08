package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

// 1. IMPORT THE SHARED SERVICE
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
import androidx.navigation.NavHostController
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
    navController: NavHostController
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
                val rideInfo by service?.rideInfo?.collectAsState(initial = BikeRideInfo.initial())
                    ?: remember { mutableStateOf(BikeRideInfo.initial()) }

                DisposableEffect(context) {
                    val connection = object : ServiceConnection {
                        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
                            // 4. CAST TO SHARED BINDER
                            service = (binder as BikeForegroundService.LocalBinder).getService()
                        }
                        override fun onServiceDisconnected(arg0: ComponentName) {
                            service = null
                        }
                    }

                    // 5. UPDATE INTENT TARGET
                    val intent = Intent(context, BikeForegroundService::class.java)

                    // Start Foreground Service ensures it keeps running even if UI is closed
                    if (Build.VERSION.SDK_INT >= 26) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                    context.bindService(intent, connection, Context.BIND_AUTO_CREATE)

                    onDispose { context.unbindService(connection) }
                }

                // Pass data to Stateless UI (Which now contains the Pager)
                BikeControlContent(
                    rideInfo = rideInfo,
                    onStart = { service?.startFormalRide() }, // Shared service usually uses toggleRide
                    onStop = { service?.stopAndFinalizeFormalRide() }   // Shared service usually uses finishRide
                )

            } else {
                PermissionRationaleContent(
                    onRequestPermission = { permissionState.launchMultiplePermissionRequest() }
                )
            }
        }
    }
}