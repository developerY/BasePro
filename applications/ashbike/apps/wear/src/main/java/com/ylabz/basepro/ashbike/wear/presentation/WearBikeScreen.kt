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
// --- MATERIAL 3 IMPORTS ---
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
// --- NAVIGATION IMPORTS ---
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
// --- PERMISSIONS IMPORT ---
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
// --- APP IMPORTS ---
import com.ylabz.basepro.ashbike.wear.service.ExerciseMetrics
import com.ylabz.basepro.ashbike.wear.service.ExerciseService

// 1. The Root App Component with Navigation & Scaffold
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

// 2. The Main Screen (Handles Permissions & Service)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WearBikeScreen() {
    // Define required permissions
    val permissionsToRequest = buildList {
        add(Manifest.permission.BODY_SENSORS)
        add(Manifest.permission.ACTIVITY_RECOGNITION)
        add(Manifest.permission.ACCESS_FINE_LOCATION)
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
                .background(MaterialTheme.colorScheme.background), // M3 Background
            contentAlignment = Alignment.Center
        ) {
            if (permissionState.allPermissionsGranted) {
                BikeControlContent()
            } else {
                PermissionRationaleContent(
                    onRequestPermission = { permissionState.launchMultiplePermissionRequest() }
                )
            }
        }
    }
}

// 3. The Active Ride UI (Material 3)
@Composable
fun BikeControlContent() {
    val context = LocalContext.current
    var service by remember { mutableStateOf<ExerciseService?>(null) }

    // Collect metrics safely
    val metrics by service?.exerciseMetrics?.collectAsState(initial = ExerciseMetrics())
        ?: remember { mutableStateOf(ExerciseMetrics()) }

    // Bind to Service (Only runs once permissions are granted)
    DisposableEffect(context) {
        val connection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, binder: IBinder) {
                service = (binder as ExerciseService.LocalBinder).getService()
            }
            override fun onServiceDisconnected(arg0: ComponentName) {
                service = null
            }
        }
        val intent = Intent(context, ExerciseService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        onDispose { context.unbindService(connection) }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AshBike Live",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Data Stats with M3 Typography
        Text(
            text = "HR: ${metrics.heartRate.toInt()} bpm",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Speed: ${String.format("%.1f", metrics.speed * 3.6)} km/h",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Dist: ${String.format("%.2f", metrics.distance / 1000)} km",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Controls Row
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = { service?.startExercise() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Go")
            }
            Button(
                onClick = { service?.stopExercise() },
                colors = ButtonDefaults.filledTonalButtonColors() // M3 style secondary button
            ) {
                Text("Stop")
            }
        }
    }
}

// 4. Permission Rationale UI (Material 3)
@Composable
fun PermissionRationaleContent(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tracking requires sensor access.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onRequestPermission) {
            Text("Grant")
        }
    }
}