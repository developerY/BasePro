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
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.applications.bike.features.main.ui.WeatherUseCase
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.math.max
import com.ylabz.basepro.applications.bike.features.main.R

@AndroidEntryPoint
class BikeForegroundService : LifecycleService() {

    @Inject
    lateinit var repo: BikeRideRepo

    @Inject
    lateinit var weatherUseCase: WeatherUseCase

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Service state properties
    private var currentFormalRideId: String? = null
    private val ridePoints = mutableListOf<Location>()
    private var rideDistance = 0f
    private var actualRideStartTimeEpochMillis: Long = 0L
    private var weatherFetchJob: Job? = null

    private val binder = LocalBinder()

    private val _rideInfo = MutableStateFlow(getInitialRideInfo())
    val rideInfo = _rideInfo.asStateFlow()

    inner class LocalBinder : Binder() {
        fun getService(): BikeForegroundService = this@BikeForegroundService
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.action?.let { action ->
            Log.d("BikeForegroundService", "Received action: $action")
            when (action) {
                ACTION_START_RIDE -> startFormalRide()
                ACTION_STOP_RIDE -> stopAndFinalizeFormalRide()
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
        } catch (e: SecurityException) {
            Log.e("BikeForegroundService", "Missing location permissions.", e)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { location ->
                updateRideInfo(location)
            }
        }
    }

    private fun updateRideInfo(location: Location) {
        val currentUiInfo = _rideInfo.value
        val speedKph = location.speed * 3.6
        val isFormalRideActive = currentUiInfo.rideState == RideState.Riding

        var currentDurationMillis = 0L
        if (isFormalRideActive && actualRideStartTimeEpochMillis > 0) {
            currentDurationMillis = System.currentTimeMillis() - actualRideStartTimeEpochMillis
            if (ridePoints.isNotEmpty()) {
                rideDistance += location.distanceTo(ridePoints.last())
            }
            // Collect points in memory
            ridePoints.add(location)
        }

        _rideInfo.value = currentUiInfo.copy(
            location = LatLng(location.latitude, location.longitude),
            currentSpeed = speedKph,
            maxSpeed = if (isFormalRideActive) max(currentUiInfo.maxSpeed, speedKph) else 0.0,
            currentTripDistance = if (isFormalRideActive) rideDistance / 1000f else 0f,
            rideDuration = formatDuration(currentDurationMillis),
            elevation = location.altitude,
            heading = if (location.hasBearing()) location.bearing else currentUiInfo.heading,
        )

        if (isFormalRideActive) {
            startForegroundService()
        }
    }

    private fun startFormalRide() {
        if (_rideInfo.value.rideState == RideState.Riding) return

        Log.d("BikeForegroundService", "Starting formal ride")
        currentFormalRideId = UUID.randomUUID().toString()
        actualRideStartTimeEpochMillis = System.currentTimeMillis()
        rideDistance = 0f
        ridePoints.clear()

        fetchWeatherIfNeeded()

        _rideInfo.value = getInitialRideInfo().copy(
            rideState = RideState.Riding,
            bikeWeatherInfo = _rideInfo.value.bikeWeatherInfo
        )
        startForegroundService()
    }

    private fun stopAndFinalizeFormalRide() {
        val rideIdToFinalize = currentFormalRideId
        if (_rideInfo.value.rideState != RideState.Riding || rideIdToFinalize == null) return

        Log.d("BikeForegroundService", "Stopping and finalizing ride: $rideIdToFinalize")
        val finalRideUiInfo = _rideInfo.value
        val endTime = System.currentTimeMillis()
        val durationMillis = if (actualRideStartTimeEpochMillis > 0) endTime - actualRideStartTimeEpochMillis else 0L
        val durationSeconds = durationMillis / 1000f

        val calculatedAverageSpeedKph = if (durationSeconds > 0 && rideDistance > 0) {
            (rideDistance / durationSeconds) * 3.6f
        } else { 0f }

        // Create the main ride entity for the database
        val rideSummaryEntity = BikeRideEntity(
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
            elevationGain = 0f, // You can add logic for these
            elevationLoss = 0f,
            caloriesBurned = 0,
            isHealthDataSynced = false,
            weatherCondition = finalRideUiInfo.bikeWeatherInfo?.conditionDescription
        )

        // Map the in-memory location points to database entities
        val locationEntities = ridePoints.map { location ->
            RideLocationEntity(
                rideId = rideIdToFinalize,
                timestamp = location.time,
                lat = location.latitude,
                lng = location.longitude,
                elevation = location.altitude.toFloat()
            )
        }

        // Save the ride and all its locations in a single transaction
        lifecycleScope.launch {
            repo.insertRideWithLocations(rideSummaryEntity, locationEntities)
            Log.d("BikeForegroundService", "Ride summary AND all locations saved for $rideIdToFinalize")
            resetServiceStateAndStopForeground()
        }
    }

    private fun resetServiceStateAndStopForeground() {
        _rideInfo.value = getInitialRideInfo().copy(
            location = _rideInfo.value.location,
            bikeWeatherInfo = _rideInfo.value.bikeWeatherInfo,
            rideState = RideState.Ended
        )
        currentFormalRideId = null
        actualRideStartTimeEpochMillis = 0L
        rideDistance = 0f
        ridePoints.clear()
        weatherFetchJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun fetchWeatherIfNeeded() {
        if (weatherFetchJob?.isActive == true || _rideInfo.value.bikeWeatherInfo != null) return

        weatherFetchJob = lifecycleScope.launch {
            val location = _rideInfo.value.location
            if (location?.latitude != 0.0 && location?.longitude != 0.0) {
                val weather = weatherUseCase.getWeather(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
                _rideInfo.value = _rideInfo.value.copy(bikeWeatherInfo = weather)
            }
        }
    }

    private fun startForegroundService() {
        val activityIntent = packageManager.getLaunchIntentForPackage(packageName)?.let {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            it
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, pendingIntentFlags)

        val currentSpeedFormatted = String.format("%.1f", rideInfo.value.currentSpeed)
        val currentDistanceFormatted = String.format("%.1f", rideInfo.value.currentTripDistance)
        val currentDurationFormatted = rideInfo.value.rideDuration

        val notificationText = "Speed: $currentSpeedFormatted km/h, Dist: $currentDistanceFormatted km, Time: $currentDurationFormatted"

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Bike Ride Active")
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.ic_bike) // Make sure you have this drawable
            .setContentIntent(pendingIntent)
            .setContentInfo("Bike Service")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun formatDuration(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60))
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
        const val ACTION_START_RIDE = PKG_PREFIX + "action.START_RIDE"
        const val ACTION_STOP_RIDE = PKG_PREFIX + "action.STOP_RIDE"

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