package com.ylabz.basepro.applications.bike.features.main.ui.components.home

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ylabz.basepro.applications.bike.features.main.location.LocationPriority
import com.ylabz.basepro.applications.bike.features.main.location.SharedLocationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A Composable that displays live speed updates using SharedLocationManager.
 *
 * @param sharedLocationManager The instance of SharedLocationManager to get location updates from.
 */
@androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun SpeedometerLiveDisplay(sharedLocationManager: SharedLocationManager) {
    val TAG = "SpeedometerLiveDisplay"
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val currentSpeedMps by sharedLocationManager.currentSpeedMps.collectAsState()

    DisposableEffect(lifecycleOwner, sharedLocationManager) {
        val observer = LifecycleEventObserver  { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    Log.d(TAG, "ON_RESUME: Requesting PASSIVE location updates.")
                    sharedLocationManager.requestLocationUpdates(TAG, LocationPriority.PASSIVE_UI)
                }
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d(TAG, "ON_PAUSE: Releasing PASSIVE location updates.")
                    sharedLocationManager.releaseLocationUpdates(TAG)
                }
                else -> { /* Do nothing for other events */ }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            Log.d(TAG, "onDispose: Releasing PASSIVE location updates (safety net).")
            sharedLocationManager.releaseLocationUpdates(TAG) // Ensure release
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Convert m/s to km/h for display
    val speedKmh = currentSpeedMps * 3.6f
    Text(text = "Live Speed: ${String.format("%.1f", speedKmh)} km/h")
}

// Preview requires a mock or fake SharedLocationManager