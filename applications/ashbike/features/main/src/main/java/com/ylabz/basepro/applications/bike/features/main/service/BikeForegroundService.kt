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

    private var weatherFetchJob: Job? = null

    @Inject
    lateinit var calculateCaloriesUseCase: CalculateCaloriesUseCase

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // --- Continuous Tracking State (Session-long) ---
    private var continuousSessionStartTimeMillis: Long = 0L
    private var continuousDistanceMeters: Float = 0f
    private var continuousCaloriesBurned: Float = 0f // Updated by calorie use case based on continuous distance/speed
    private var continuousMaxSpeedKph: Double = 0.0

    // --- Formal Ride Segment State (for UI "reset" and saving the specific segment) ---
    private var currentFormalRideId: String? = null
    private val formalRideTrackPoints = mutableListOf<Location>()
    private var formalRideSegmentStartTimeMillis: Long = 0L // Actual start time of the formal segment for saving
    private var formalRideSegmentUiResetTimeMillis: Long = 0L // Time used for UI duration "reset"
    private var formalRideSegmentStartOffsetDistanceMeters: Float = 0f
    private var formalRideSegmentStartOffsetCalories: Float = 0f
    private var formalRideSegmentMaxSpeedKph: Double = 0.0


    // Calorie calculation state
    private lateinit var userStatsFlow: Flow<UserStats>
    private var caloriesCalculationJob: Job? = null
    // currentTotalCaloriesBurned will now specifically refer to the formal ride segment's calories when active

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
        continuousSessionStartTimeMillis = System.currentTimeMillis()

        userStatsFlow = userProfileRepository.weightFlow.map { weightString ->
            val weightKg = weightString.toFloatOrNull() ?: 70f
            UserStats(heightCm = 0f, weightKg = weightKg)
        }
        startLocationUpdates()
        startOrRestartCalorieCalculation(isFormalRideActive = false) // Start for continuous mode
        Log.d("BikeForegroundService", "Service created. Continuous tracking and calorie calculation initiated.")
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
        val speedKph = location.speed * 3.6
        val isFormalRideActive = _rideInfo.value.rideState == RideState.Riding

        // --- Update Continuous Accumulators ---
        continuousMaxSpeedKph = max(continuousMaxSpeedKph, speedKph)
        val prevLocation = _rideInfo.value.location
        if (prevLocation?.latitude != 0.0 || prevLocation?.longitude != 0.0) {
            val lastLoc = Location("").apply { latitude = prevLocation?.latitude?: 0.0; longitude = prevLocation?.longitude?: 0.0 }
            continuousDistanceMeters += location.distanceTo(lastLoc)
        }
        // continuousCaloriesBurned is updated by its dedicated flow

        // --- Determine Displayed Values ---
        var displayDistanceKm: Float
        var displayCalories: Int // This will come from the calorie flow based on its mode
        var displayDuration: String
        var displayMaxSpeed: Double

        if (isFormalRideActive) {
            displayDistanceKm = (continuousDistanceMeters - formalRideSegmentStartOffsetDistanceMeters) / 1000f
            // Calories for formal ride segment are handled by currentTotalCaloriesBurned in startOrRestartCalorieCalculation
            displayCalories = _rideInfo.value.caloriesBurned // Relies on calorie collector updating this for the segment
            displayDuration = formatDuration(System.currentTimeMillis() - formalRideSegmentUiResetTimeMillis)
            formalRideSegmentMaxSpeedKph = max(formalRideSegmentMaxSpeedKph, speedKph)
            displayMaxSpeed = formalRideSegmentMaxSpeedKph
            formalRideTrackPoints.add(location)
        } else {
            displayDistanceKm = continuousDistanceMeters / 1000f
            displayCalories = continuousCaloriesBurned.toInt()
            displayDuration = formatDuration(System.currentTimeMillis() - continuousSessionStartTimeMillis)
            displayMaxSpeed = continuousMaxSpeedKph
        }

        _rideInfo.value = _rideInfo.value.copy(
            location = LatLng(location.latitude, location.longitude),
            currentSpeed = speedKph,
            currentTripDistance = displayDistanceKm,
            caloriesBurned = displayCalories, // This needs to be correctly sourced from segment/continuous calorie flow
            rideDuration = displayDuration,
            maxSpeed = displayMaxSpeed,
            elevation = location.altitude,
            heading = if (location.hasBearing()) location.bearing else _rideInfo.value.heading,
            rideState = _rideInfo.value.rideState // Preserve current state, changed by start/stop actions
        )

        if (isFormalRideActive) {
            startForegroundService()
        }
    }

    private fun startOrRestartCalorieCalculation(isFormalRideActive: Boolean) {
        caloriesCalculationJob?.cancel()
        Log.d("BikeForegroundService", "Starting/Restarting calorie calculation. FormalRideActive: $isFormalRideActive")

        if (!::userStatsFlow.isInitialized) {
            Log.e("BikeForegroundService", "userStatsFlow not initialized! Re-initializing.")
            userStatsFlow = userProfileRepository.weightFlow.map { weightString ->
                val weightKg = weightString.toFloatOrNull() ?: 70f
                UserStats(heightCm = 0f, weightKg = weightKg)
            }
        }

        val distanceKmFlow: Flow<Float>
        if (isFormalRideActive) {
            // Flow for formal ride segment distance
            distanceKmFlow = _rideInfo.map { (continuousDistanceMeters - formalRideSegmentStartOffsetDistanceMeters) / 1000f }
        } else {
            // Flow for continuous session distance
            distanceKmFlow = _rideInfo.map { continuousDistanceMeters / 1000f }
        }

        // Speed is always the current speed from _rideInfo
        val speedKmhFlow = _rideInfo.map { it.currentSpeed.toFloat() }

        caloriesCalculationJob = lifecycleScope.launch {
            calculateCaloriesUseCase(distanceKmFlow, speedKmhFlow, userStatsFlow)
                .collect { calculatedCalories ->
                    if (isFormalRideActive) {
                        // Update UI with segment's calories
                        _rideInfo.value = _rideInfo.value.copy(caloriesBurned = calculatedCalories.toInt())
                         Log.d("BikeForegroundService", "Formal Ride Calories: ${calculatedCalories.toInt()}")
                    } else {
                        continuousCaloriesBurned = calculatedCalories // Update continuous accumulator
                        // Update UI only if not in a formal ride (to avoid overriding segment display)
                        if (_rideInfo.value.rideState != RideState.Riding) {
                             _rideInfo.value = _rideInfo.value.copy(caloriesBurned = continuousCaloriesBurned.toInt())
                        }
                        Log.d("BikeForegroundService", "Continuous Calories: ${continuousCaloriesBurned.toInt()}")
                    }
                }
        }
    }

    private fun startFormalRide() {
        if (_rideInfo.value.rideState == RideState.Riding) {
            Log.d("BikeForegroundService", "Formal ride already in progress.")
            return
        }
        Log.d("BikeForegroundService", "Starting formal ride. UI will reset for this segment.")

        currentFormalRideId = UUID.randomUUID().toString()
        formalRideSegmentStartTimeMillis = System.currentTimeMillis() // For saving
        formalRideSegmentUiResetTimeMillis = System.currentTimeMillis() // For UI duration display reset
        formalRideTrackPoints.clear()

        // Capture offsets from continuous data
        formalRideSegmentStartOffsetDistanceMeters = continuousDistanceMeters
        formalRideSegmentStartOffsetCalories = continuousCaloriesBurned // Though calorie calc will restart for segment
        formalRideSegmentMaxSpeedKph = 0.0 // Reset max speed for the formal segment view

        fetchWeatherIfNeeded()

        // Update UI to show "0" for the formal ride segment
        _rideInfo.value = _rideInfo.value.copy(
            rideState = RideState.Riding,
            currentTripDistance = 0f,
            caloriesBurned = 0, // UI reset
            rideDuration = "00:00",
            maxSpeed = 0.0,
            bikeWeatherInfo = _rideInfo.value.bikeWeatherInfo // Preserve weather
        )

        startOrRestartCalorieCalculation(isFormalRideActive = true) // Restart for formal ride segment
        startForegroundService()
    }

    private fun stopAndFinalizeFormalRide() {
        val rideIdToFinalize = currentFormalRideId
        if (_rideInfo.value.rideState != RideState.Riding || rideIdToFinalize == null) {
            Log.d("BikeForegroundService", "No active formal ride to stop.")
            return
        }
        Log.d("BikeForegroundService", "Stopping and finalizing formal ride: $rideIdToFinalize")

        caloriesCalculationJob?.cancel() // Stop segment-specific calorie calculation

        val segmentDistanceMeters = continuousDistanceMeters - formalRideSegmentStartOffsetDistanceMeters
        // The calories for the segment are what the _rideInfo.value.caloriesBurned holds at this point
        // because startOrRestartCalorieCalculation (in formal mode) was updating it.
        val segmentCalories = _rideInfo.value.caloriesBurned 
        val segmentDurationMillis = System.currentTimeMillis() - formalRideSegmentStartTimeMillis
        val segmentDurationSeconds = segmentDurationMillis / 1000f
        val segmentMaxSpeed = formalRideSegmentMaxSpeedKph

        val averageSpeedKph = if (segmentDurationSeconds > 0 && segmentDistanceMeters > 0) {
            (segmentDistanceMeters / segmentDurationSeconds) * 3.6f
        } else 0f

        val rideSummaryEntity = BikeRideEntity(
            rideId = rideIdToFinalize,
            startTime = formalRideSegmentStartTimeMillis,
            endTime = System.currentTimeMillis(),
            totalDistance = segmentDistanceMeters,
            averageSpeed = averageSpeedKph.toFloat(),
            maxSpeed = segmentMaxSpeed.toFloat(),
            startLat = formalRideTrackPoints.firstOrNull()?.latitude ?: 0.0,
            startLng = formalRideTrackPoints.firstOrNull()?.longitude ?: 0.0,
            endLat = formalRideTrackPoints.lastOrNull()?.latitude ?: 0.0,
            endLng = formalRideTrackPoints.lastOrNull()?.longitude ?: 0.0,
            elevationGain = 0f, 
            elevationLoss = 0f, 
            caloriesBurned = segmentCalories,
            isHealthDataSynced = false,
            weatherCondition = _rideInfo.value.bikeWeatherInfo?.conditionDescription
        )

        val locationEntities = formalRideTrackPoints.map { location ->
            RideLocationEntity(
                rideId = rideIdToFinalize, timestamp = location.time,
                lat = location.latitude, lng = location.longitude, elevation = location.altitude.toFloat()
            )
        }

        lifecycleScope.launch {
            repo.insertRideWithLocations(rideSummaryEntity, locationEntities)
            Log.d("BikeForegroundService", "Formal ride $rideIdToFinalize saved.")
            
            // Revert UI to show continuous data
            _rideInfo.value = _rideInfo.value.copy(
                rideState = RideState.Ended,
                // Display will now pick up continuous values in updateRideInfo
                currentTripDistance = continuousDistanceMeters / 1000f,
                caloriesBurned = continuousCaloriesBurned.toInt(),
                rideDuration = formatDuration(System.currentTimeMillis() - continuousSessionStartTimeMillis),
                maxSpeed = continuousMaxSpeedKph
            )
            currentFormalRideId = null
            formalRideTrackPoints.clear()
            startOrRestartCalorieCalculation(isFormalRideActive = false) // Restart for continuous mode
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }
    
    // Resets both continuous session and any formal ride view
    private fun resetServiceStateAndStopForeground() {
        Log.d("BikeForegroundService", "Full service reset: continuous and formal states.")
        
        continuousSessionStartTimeMillis = System.currentTimeMillis()
        continuousDistanceMeters = 0f
        continuousCaloriesBurned = 0f
        continuousMaxSpeedKph = 0.0

        currentFormalRideId = null
        formalRideTrackPoints.clear()
        formalRideSegmentStartOffsetDistanceMeters = 0f
        formalRideSegmentStartOffsetCalories = 0f
        formalRideSegmentUiResetTimeMillis = 0L
        formalRideSegmentMaxSpeedKph = 0.0
        
        _rideInfo.value = getInitialRideInfo().copy(
            location = _rideInfo.value.location, // Keep last known location for immediate use
            bikeWeatherInfo = _rideInfo.value.bikeWeatherInfo, // Keep last known weather
            rideState = RideState.NotStarted // Or Ended, depending on desired state
        )

        weatherFetchJob?.cancel()
        caloriesCalculationJob?.cancel()
        // Restart calorie calculation for the new continuous session if service is to remain active
        // startOrRestartCalorieCalculation(isFormalRideActive = false) 

        stopForeground(STOP_FOREGROUND_REMOVE)
        // If the service is truly stopping and not just resetting for a new session,
        // consider calling selfStop().
    }

    private fun fetchWeatherIfNeeded() {
        // ... (same as before)
    }

    private fun startForegroundService() {
        // ... (same as before, ensure notificationText uses _rideInfo.value for display)
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
        val currentCaloriesFormatted = rideState.caloriesBurned

        val notificationText = "Speed: $currentSpeedFormatted km/h, Dist: $currentDistanceFormatted km, Time: $currentDurationFormatted, Cal: $currentCaloriesFormatted"

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Bike Ride Active")
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.ic_bike)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun formatDuration(millis: Long): String {
        // ... (same as before)
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
