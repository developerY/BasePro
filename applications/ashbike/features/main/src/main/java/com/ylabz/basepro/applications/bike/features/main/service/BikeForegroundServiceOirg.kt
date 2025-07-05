package com.ylabz.basepro.applications.bike.features.main.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest as GmsLocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
// DO NOT import com.ylabz.basepro.applications.bike.MainActivity here
// import com.ylabz.basepro.applications.bike.R // Correct R class for drawables
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.core.model.bike.BikeRide
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@AndroidEntryPoint
class BikeForegroundServiceOrig : LifecycleService() {

    @Inject
    lateinit var repo: BikeRideRepo

    // Service state properties
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentFormalRideId: String? = null
    private val ridePoints = mutableListOf<Location>()
    private var rideDistance = 0f
    private var actualRideStartTimeEpochMillis: Long = 0L

    // --- ARCHITECTURE FIX: Binder Implementation ---
    private val binder = LocalBinder()

    // rideInfo is now an INSTANCE variable, not static.
    val rideInfo = MutableStateFlow<BikeRideInfo?>(null)

    inner class LocalBinder : Binder() {
        fun getService(): BikeForegroundServiceOrig = this@BikeForegroundServiceOrig
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        Log.d("BikeAppDebug", "Service: onBind - Client bound to service.")
        return binder
    }
    // --- END ARCHITECTURE FIX ---


    override fun onCreate() {
        super.onCreate()
        Log.d("BikeAppDebug", "Service: onCreate - Service created.")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        rideInfo.value = getInitialRideInfo()
        startLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("BikeAppDebug", "Service: onStartCommand - Action: ${intent?.action}")
        intent?.let {
            when (it.action) {
                ACTION_BEGIN_FORMAL_RIDE -> {
                    val formalRideUuidFromViewModel = it.getStringExtra(EXTRA_FORMAL_RIDE_UUID)
                    if (formalRideUuidFromViewModel != null) {
                        currentFormalRideId = formalRideUuidFromViewModel
                        actualRideStartTimeEpochMillis = System.currentTimeMillis()
                        rideDistance = 0f
                        ridePoints.clear()
                        val ridingInfo = rideInfo.value?.copy(
                            rideState = RideState.Riding,
                            currentTripDistance = 0f,
                            rideDuration = formatDuration(0)
                        ) ?: getInitialRideInfo().copy(rideState = RideState.Riding)
                        rideInfo.value = ridingInfo
                        Log.d("BikeAppDebug", "Service: ACTION_BEGIN_FORMAL_RIDE - UUID: $currentFormalRideId. Ride formally started.")
                        startForegroundService()
                    }
                }
                // REMOVED: Pause functionality
                ACTION_STOP_FORMAL_RIDE -> stopAndFinalizeFormalRide()
            }
        }
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = GmsLocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(1000)
            .setMaxUpdateDelayMillis(2000)
            .build()
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            Log.d("BikeAppDebug", "Service: Location updates started.")
        } catch (e: SecurityException) {
            Log.e("BikeAppDebug", "Service: Missing location permissions.", e)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.locations.forEach { location ->
                addPointToRideAndUpdateUi(location)
            }
        }
    }

    private fun addPointToRideAndUpdateUi(location: Location) {
        val speedMps = location.speed
        val speedKph = (speedMps * 3.6)
        val isFormalRideActive = currentFormalRideId != null
        val currentRideState = rideInfo.value?.rideState ?: RideState.NotStarted
        var currentDurationMillis: Long = 0L

        if (isFormalRideActive && currentRideState == RideState.Riding) {
            currentDurationMillis = if (actualRideStartTimeEpochMillis > 0) System.currentTimeMillis() - actualRideStartTimeEpochMillis else 0L
            if (ridePoints.isNotEmpty()) {
                rideDistance += location.distanceTo(ridePoints.last())
            }
            ridePoints.add(location)
        }

        val currentUiInfo = rideInfo.value ?: getInitialRideInfo()
        val updatedInfo = currentUiInfo.copy(
            location = LatLng(location.latitude, location.longitude),
            currentSpeed = speedKph,
            maxSpeed = if (isFormalRideActive && currentRideState == RideState.Riding) max(currentUiInfo.maxSpeed, speedKph) else currentUiInfo.maxSpeed,
            currentTripDistance = if (isFormalRideActive) rideDistance / 1000f else 0f,
            rideDuration = formatDuration(currentDurationMillis),
            elevation = location.altitude,
            heading = if(location.hasBearing()) location.bearing else currentUiInfo.heading,
            rideState = currentRideState
        )
        rideInfo.value = updatedInfo

        if(isFormalRideActive) {
            startForegroundService()
        }

        currentFormalRideId?.let { formalRideIdStr ->
            if (currentRideState == RideState.Riding) {
                lifecycleScope.launch {
                    // --- CORRECTED: Entity creation now matches the data class ---
                    val pointEntity = RideLocationEntity(
                        rideId = formalRideIdStr,
                        timestamp = location.time,
                        lat = location.latitude,
                        lng = location.longitude,
                        elevation = location.altitude.toFloat()
                        // Removed speed and accuracy as they are not in the entity
                    )
                    // repo.insertLocation(pointEntity)
                }
            }
        }
    }

    private fun stopAndFinalizeFormalRide() {
        val rideIdToFinalize = currentFormalRideId ?: return
        val finalRideUiInfo = rideInfo.value ?: getInitialRideInfo()
        val endTime = System.currentTimeMillis()
        val durationMillis = if (actualRideStartTimeEpochMillis > 0) endTime - actualRideStartTimeEpochMillis else 0L
        val durationSeconds = durationMillis / 1000f

        val calculatedAverageSpeedKph = if (durationSeconds > 0 && rideDistance > 0) {
            (rideDistance / durationSeconds) * 3.6f
        } else {
            0f
        }

        val rideSummary = BikeRide(
            rideId = rideIdToFinalize,
            startTime = actualRideStartTimeEpochMillis,
            endTime = endTime,
            totalDistance = rideDistance,
            averageSpeed = calculatedAverageSpeedKph,
            maxSpeed = finalRideUiInfo.maxSpeed.toFloat(),
            startLat = ridePoints.firstOrNull()?.latitude ?: 0.0,
            startLng = ridePoints.firstOrNull()?.longitude ?: 0.0,
            endLat = ridePoints.lastOrNull()?.latitude ?: 0.0,
            endLng = ridePoints.lastOrNull()?.longitude ?: 0.0,
            elevationGain = finalRideUiInfo.elevationGain.toFloat(),
            elevationLoss = finalRideUiInfo.elevationLoss.toFloat(),
            caloriesBurned = finalRideUiInfo.caloriesBurned,
            isHealthDataSynced = false,
            avgHeartRate = null,
            maxHeartRate = null,
            healthConnectRecordId = null,
            weatherCondition = finalRideUiInfo.bikeWeatherInfo?.conditionDescription,
            rideType = null,
            notes = null,
            rating = null,
            bikeId = null,
            batteryStart = finalRideUiInfo.batteryLevel,
            batteryEnd = null,
            locations = emptyList()
        )

        lifecycleScope.launch {
            repo.insert(rideSummary)
            Log.d("BikeAppDebug", "Service: Ride summary UPDATED for $rideIdToFinalize")
            resetServiceStateAndStop()
        }
    }

    private fun resetServiceStateAndStop() {
        rideInfo.value = getInitialRideInfo().copy(rideState = RideState.Ended)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startForegroundService() {
        val notificationIntent = Intent(ACTION_OPEN_MAIN_ACTIVITY_FROM_SERVICE)
        notificationIntent.setPackage(applicationContext.packageName)

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, pendingIntentFlags)

        val currentSpeedFormatted = String.format("%.1f", rideInfo.value?.currentSpeed ?: 0.0)
        val currentDistanceFormatted = String.format("%.1f", rideInfo.value?.currentTripDistance ?: 0f)
        val currentDurationFormatted = rideInfo.value?.rideDuration ?: "00:00"

        val notificationText = "Speed: $currentSpeedFormatted km/h, Dist: $currentDistanceFormatted km, Time: $currentDurationFormatted"

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Bike Ride Active")
            .setContentText(notificationText)
            //.setSmallIcon(R.drawable.ic_bike)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun formatDuration(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60)) % 24
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "bike_ride_channel_v5"
        const val NOTIFICATION_ID = 1

        private const val PKG_PREFIX = "com.ylabz.basepro.applications.bike.features.main.service."
        const val ACTION_OPEN_MAIN_ACTIVITY_FROM_SERVICE = PKG_PREFIX + "action.OPEN_MAIN_ACTIVITY"
        const val ACTION_BEGIN_FORMAL_RIDE = PKG_PREFIX + "action.BEGIN_FORMAL_RIDE"
        const val ACTION_STOP_FORMAL_RIDE = PKG_PREFIX + "action.STOP_FORMAL_RIDE"
        const val EXTRA_FORMAL_RIDE_UUID = PKG_PREFIX + "extra.FORMAL_RIDE_UUID"

        fun getInitialRideInfo(): BikeRideInfo = BikeRideInfo(
            location = LatLng(0.0, 0.0),
            currentSpeed = 0.0,
            averageSpeed = 0.0,
            maxSpeed = 0.0,
            currentTripDistance = 0f,
            totalTripDistance = null,
            remainingDistance = null,
            elevationGain = 0.0,
            elevationLoss = 0.0,
            caloriesBurned = 0,
            rideDuration = "00:00",
            settings = emptyMap(),
            heading = 0f,
            elevation = 0.0,
            isBikeConnected = false,
            batteryLevel = null,
            motorPower = null,
            rideState = RideState.NotStarted,
            bikeWeatherInfo = null
        )
    }
}