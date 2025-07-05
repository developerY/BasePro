package com.ylabz.basepro.applications.bike.features.main.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SharedLocationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) {
    private val _currentLocationData = MutableStateFlow<Location?>(null)
    val currentLocationData: StateFlow<Location?> = _currentLocationData.asStateFlow()

    private val _currentSpeedMps = MutableStateFlow(0.0f) // Speed in meters per second
    val currentSpeedMps: StateFlow<Float> = _currentSpeedMps.asStateFlow()

    // Using a simple counter for active requesters
    private var activeRequestersCount = 0
    // Track the highest priority requested
    private var currentHighestPriority = LocationPriority.PASSIVE_UI // Default or a "NONE" state
    private var isCurrentlyUpdating = false

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let {
                Log.d("SharedLocationManager", "New location: ${it.latitude}, ${it.longitude}, Speed: ${it.speed}")
                _currentLocationData.value = it
                _currentSpeedMps.value = it.speed // it.speed is in m/s
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    @Synchronized
    fun requestLocationUpdates(requesterTag: String, priority: LocationPriority) {
        Log.d("SharedLocationManager", "Request from [$requesterTag] with priority [$priority]. Current active: $activeRequestersCount, Current priority: $currentHighestPriority, IsUpdating: $isCurrentlyUpdating")
        activeRequestersCount++

        if (priority > currentHighestPriority || !isCurrentlyUpdating) {
            if (isCurrentlyUpdating) {
                // Stop existing updates to potentially change parameters
                fusedLocationClient.removeLocationUpdates(locationCallback)
                Log.d("SharedLocationManager", "Stopped existing updates to change priority.")
            }
            currentHighestPriority = priority
            startLocationUpdatesInternal(currentHighestPriority)
        } else {
            Log.d("SharedLocationManager", "Already updating with sufficient or higher priority.")
        }
    }

    @Synchronized
    fun releaseLocationUpdates(requesterTag: String) {
        activeRequestersCount--
        Log.d("SharedLocationManager", "Release from [$requesterTag]. Current active: $activeRequestersCount")
        if (activeRequestersCount <= 0) {
            activeRequestersCount = 0 // Ensure it doesn't go negative
            stopLocationUpdatesInternal()
            currentHighestPriority = LocationPriority.PASSIVE_UI // Reset to a base state or a NONE state
        }
        // Optional: If activeRequesters > 0, you might want to re-evaluate and potentially
        // downgrade the location request priority if the highest priority requester was removed.
        // This part needs more complex logic to track priorities of all requesters.
        // For simplicity, current implementation only stops when all requesters are gone,
        // or upgrades if a new higher priority comes in.
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startLocationUpdatesInternal(priority: LocationPriority) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("SharedLocationManager", "Location permissions not granted.")
            // Optionally, emit an error state or a specific event that UI can observe
            // to trigger permission request flow.
            _currentLocationData.value = null // Clear any stale data
            _currentSpeedMps.value = 0.0f
            return
        }

        val intervalMillis = when (priority) {
            LocationPriority.ACTIVE_TRACKING -> 2000L // More frequent for tracking
            LocationPriority.PASSIVE_UI -> 5000L      // Less frequent for UI speedometer
        }
        val locationRequestPriority = when (priority) {
            LocationPriority.ACTIVE_TRACKING -> Priority.PRIORITY_HIGH_ACCURACY
            LocationPriority.PASSIVE_UI -> Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }

        val locationRequest = LocationRequest.Builder(intervalMillis)
            .setPriority(locationRequestPriority)
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            isCurrentlyUpdating = true
            Log.d("SharedLocationManager", "Location updates STARTED with priority [$priority], interval [${intervalMillis}ms]")
        } catch (e: SecurityException) {
            Log.e("SharedLocationManager", "SecurityException while starting location updates.", e)
            // Handle missing permissions again, perhaps more explicitly
        }
    }

    private fun stopLocationUpdatesInternal() {
        if (isCurrentlyUpdating) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            isCurrentlyUpdating = false
            _currentLocationData.value = null // Clear last known location
            _currentSpeedMps.value = 0.0f
            Log.d("SharedLocationManager", "Location updates STOPPED.")
        }
    }
}
