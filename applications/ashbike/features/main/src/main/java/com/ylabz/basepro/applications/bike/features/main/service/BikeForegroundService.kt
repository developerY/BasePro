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
import com.ylabz.basepro.applications.bike.database.repository.UserProfileRepository
import com.ylabz.basepro.applications.bike.features.main.ui.WeatherUseCase
import com.ylabz.basepro.applications.bike.features.main.usecase.CalculateCaloriesUseCase
import com.ylabz.basepro.applications.bike.features.main.usecase.UserStats
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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

    @Inject
    lateinit var calculateCaloriesUseCase: CalculateCaloriesUseCase

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Service state properties
    private var currentFormalRideId: String? = null
    private val ridePoints = mutableListOf<Location>()
    private var rideDistance = 0f // Internal accumulator for distance in meters
    private var actualRideStartTimeEpochMillis: Long = 0L
    private var weatherFetchJob: Job? = null

    // Calorie calculation state
    private lateinit var userStatsFlow: Flow<UserStats>
    private var caloriesCalculationJob: Job? = null
    private var currentTotalCaloriesBurned: Float = 0f // Internal accumulator for total calories burned

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

        // Initialize userStatsFlow
        userStatsFlow = userProfileRepository.weightFlow.map { weightString ->
            val weightKg = weightString.toFloatOrNull() ?: 70f // Default to 70kg if null or invalid
            UserStats(heightCm = 0f, weightKg = weightKg) // heightCm can be default as it'''s not used by current use case
        }
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
            // Consider stopping the service or notifying the user if permissions are critical
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
        val speedKph = location.speed * 3.6 // m/s to km/h
        val isFormalRideActive = currentUiInfo.rideState == RideState.Riding

        var currentDurationMillis = 0L
        var newDistanceForUiKm = currentUiInfo.currentTripDistance // Preserve UI distance if not riding

        if (isFormalRideActive) { // Only update accumulators if a formal ride is active
            if (actualRideStartTimeEpochMillis > 0) {
                currentDurationMillis = System.currentTimeMillis() - actualRideStartTimeEpochMillis
            }
            if (ridePoints.isNotEmpty()) {
                // rideDistance is the internal accumulator in meters
                rideDistance += location.distanceTo(ridePoints.last())
            }
            ridePoints.add(location) // Collect points in memory
            newDistanceForUiKm = rideDistance / 1000f // Convert internal meters to km for UI
        }

        _rideInfo.value = currentUiInfo.copy(
            location = LatLng(location.latitude, location.longitude),
            currentSpeed = if (isFormalRideActive) speedKph else 0.0,
            maxSpeed = if (isFormalRideActive) max(currentUiInfo.maxSpeed, speedKph) else 0.0,
            currentTripDistance = newDistanceForUiKm, // This is what the UI shows (km)
            rideDuration = if (isFormalRideActive) formatDuration(currentDurationMillis) else "00:00",
            elevation = location.altitude,
            heading = if (location.hasBearing()) location.bearing else currentUiInfo.heading,
            // caloriesBurned is updated by the startOrRestartCalorieCalculation flow
            rideState = if (isFormalRideActive) RideState.Riding else currentUiInfo.rideState
        )

        if (isFormalRideActive) {
            startForegroundService() // Update notification with new speed, distance, time, calories
        }
    }

    private fun startOrRestartCalorieCalculation() {
        caloriesCalculationJob?.cancel()
        Log.d("BikeForegroundService", "Attempting to start calorie calculation.")

        if (!::userStatsFlow.isInitialized) {
            Log.e("BikeForegroundService", "userStatsFlow not initialized! Re-initializing.")
            // This is a fallback, should ideally be initialized in onCreate
            userStatsFlow = userProfileRepository.weightFlow.map { weightString ->
                val weightKg = weightString.toFloatOrNull() ?: 70f
                UserStats(heightCm = 0f, weightKg = weightKg)
            }
        }

        // distanceKmFlow uses the internal rideDistance (meters) converted to km
        val distanceKmFlow = _rideInfo.map { rideDistance / 1000f }
        // speedKmhFlow uses currentSpeed from _rideInfo (already in km/h)
        val speedKmhFlow = _rideInfo.map { it.currentSpeed.toFloat() }

        caloriesCalculationJob = lifecycleScope.launch {
            Log.d("BikeForegroundService", "Starting calorie calculation collection.")
            calculateCaloriesUseCase(distanceKmFlow, speedKmhFlow, userStatsFlow)
                .collect { calculatedCalories ->
                    currentTotalCaloriesBurned = calculatedCalories
                    _rideInfo.value = _rideInfo.value.copy(caloriesBurned = currentTotalCaloriesBurned.toInt())
                    Log.d("BikeForegroundService", "Collected calories: $currentTotalCaloriesBurned, UI calories: ${currentTotalCaloriesBurned.toInt()}")
                }
        }
    }

    private fun startFormalRide() {
        if (_rideInfo.value.rideState == RideState.Riding) {
            Log.d("BikeForegroundService", "Ride already in progress. Ignoring startFormalRide command.")
            return
        }
        Log.d("BikeForegroundService", "Starting formal ride. Resetting all ride data.")

        currentFormalRideId = UUID.randomUUID().toString()
        actualRideStartTimeEpochMillis = System.currentTimeMillis()
        rideDistance = 0f // Reset internal distance accumulator (meters)
        ridePoints.clear()
        currentTotalCaloriesBurned = 0f // Reset internal calorie accumulator

        fetchWeatherIfNeeded()

        // Reset UI state to initial, then set to Riding
        // getInitialRideInfo() ensures caloriesBurned in UI state starts at 0
        _rideInfo.value = getInitialRideInfo().copy(
            rideState = RideState.Riding,
            bikeWeatherInfo = _rideInfo.value.bikeWeatherInfo // Preserve last known weather
        )

        startOrRestartCalorieCalculation()
        startForegroundService()
    }

    private fun stopAndFinalizeFormalRide() {
        val rideIdToFinalize = currentFormalRideId
        if (_rideInfo.value.rideState != RideState.Riding || rideIdToFinalize == null) {
            Log.d("BikeForegroundService", "No active ride to stop or already stopped.")
            return
        }
        Log.d("BikeForegroundService", "Stopping and finalizing ride: $rideIdToFinalize")

        caloriesCalculationJob?.cancel() // Stop calorie calculation before finalizing

        val finalRideUiInfo = _rideInfo.value
        val endTime = System.currentTimeMillis()
        val durationMillis = if (actualRideStartTimeEpochMillis > 0) endTime - actualRideStartTimeEpochMillis else 0L
        val durationSeconds = durationMillis / 1000f

        val calculatedAverageSpeedKph = if (durationSeconds > 0 && rideDistance > 0) {
            (rideDistance / durationSeconds) * 3.6f // rideDistance is in meters
        } else {
            0f
        }

        val rideSummaryEntity = BikeRideEntity(
            rideId = rideIdToFinalize,
            startTime = actualRideStartTimeEpochMillis,
            endTime = endTime,
            totalDistance = rideDistance, // Total accumulated distance in meters
            averageSpeed = calculatedAverageSpeedKph,
            maxSpeed = finalRideUiInfo.maxSpeed.toFloat(),
            startLat = ridePoints.firstOrNull()?.latitude ?: 0.0,
            startLng = ridePoints.firstOrNull()?.longitude ?: 0.0,
            endLat = ridePoints.lastOrNull()?.latitude ?: 0.0,
            endLng = ridePoints.lastOrNull()?.longitude ?: 0.0,
            elevationGain = 0f, // Placeholder
            elevationLoss = 0f, // Placeholder
            caloriesBurned = currentTotalCaloriesBurned.toInt(), // Use final calculated calories
            isHealthDataSynced = false,
            weatherCondition = finalRideUiInfo.bikeWeatherInfo?.conditionDescription
        )

        val locationEntities = ridePoints.map { location ->
            RideLocationEntity(
                rideId = rideIdToFinalize,
                timestamp = location.time,
                lat = location.latitude,
                lng = location.longitude,
                elevation = location.altitude.toFloat()
            )
        }

        lifecycleScope.launch {
            repo.insertRideWithLocations(rideSummaryEntity, locationEntities)
            Log.d("BikeForegroundService", "Ride summary AND all locations saved for $rideIdToFinalize")
            resetServiceStateAndStopForeground()
        }
    }

    private fun resetServiceStateAndStopForeground() {
        Log.d("BikeForegroundService", "Resetting service state and stopping foreground.")
        _rideInfo.value = getInitialRideInfo().copy(
            location = _rideInfo.value.location, // Preserve last location
            bikeWeatherInfo = _rideInfo.value.bikeWeatherInfo, // Preserve last weather
            rideState = RideState.Ended
        )

        currentFormalRideId = null
        actualRideStartTimeEpochMillis = 0L
        rideDistance = 0f
        ridePoints.clear()
        currentTotalCaloriesBurned = 0f

        weatherFetchJob?.cancel()
        caloriesCalculationJob?.cancel()

        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun fetchWeatherIfNeeded() {
        if (weatherFetchJob?.isActive == true || _rideInfo.value.bikeWeatherInfo != null) return

        weatherFetchJob = lifecycleScope.launch {
            val location = _rideInfo.value.location
            if (location != null && location.latitude != 0.0 && location.longitude != 0.0) {
                val weather = weatherUseCase.getWeather(location.latitude, location.longitude)
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

        val rideState = rideInfo.value // Capture current state for notification
        val currentSpeedFormatted = String.format("%.1f", rideState.currentSpeed)
        val currentDistanceFormatted = String.format("%.1f", rideState.currentTripDistance)
        val currentDurationFormatted = rideState.rideDuration
        val currentCaloriesFormatted = rideState.caloriesBurned // Calories for notification

        val notificationText = "Speed: $currentSpeedFormatted km/h, Dist: $currentDistanceFormatted km, Time: $currentDurationFormatted, Cal: $currentCaloriesFormatted"

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Bike Ride Active")
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.ic_bike)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true) // Subsequent updates won'''t make sound/vibrate
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
        const val NOTIFICATION_CHANNEL_ID = "bike_ride_channel_v5" // Ensure this channel is created in Application class
        const val NOTIFICATION_ID = 1

        private const val PKG_PREFIX = "com.ylabz.basepro.applications.bike.features.main.service."
        const val ACTION_START_RIDE = PKG_PREFIX + "action.START_RIDE"
        const val ACTION_STOP_RIDE = PKG_PREFIX + "action.STOP_RIDE"

        fun getInitialRideInfo(): BikeRideInfo = BikeRideInfo(
            location = LatLng(0.0, 0.0),
            currentSpeed = 0.0,
            averageSpeed = 0.0,
            maxSpeed = 0.0,
            currentTripDistance = 0f, // in km for UI
            totalTripDistance = null,
            remainingDistance = null,
            elevationGain = 0.0,
            elevationLoss = 0.0,
            caloriesBurned = 0, // Initial UI calories
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
