package com.ylabz.basepro.applications.bike.features.main.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.applications.bike.database.repository.AppSettingsRepository
import com.ylabz.basepro.applications.bike.database.repository.UserProfileRepository
import com.ylabz.basepro.applications.bike.features.main.R
import com.ylabz.basepro.applications.bike.features.main.usecase.CalculateCaloriesUseCase
import com.ylabz.basepro.applications.bike.features.main.usecase.UserStats
import com.ylabz.basepro.core.data.repository.bike.BikeRepository
import com.ylabz.basepro.core.data.repository.bike.DemoModeSimulator
import com.ylabz.basepro.core.data.repository.sensor.heart.HeartRateRepository
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.LocationEnergyLevel
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.model.location.GpsFix // Added import for GpsFix
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import kotlin.math.max
import com.google.android.gms.location.LocationRequest as GmsLocationRequest

@AndroidEntryPoint
class BikeForegroundService : LifecycleService() {

    @Inject
    lateinit var repo: BikeRideRepo

    @Inject
    lateinit var calculateCaloriesUseCase: CalculateCaloriesUseCase

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    @Inject
    lateinit var appSettingsRepository: AppSettingsRepository

    @Inject lateinit var bikeRepository: BikeRepository // Inject the Shared Repo

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationProcessingThread: HandlerThread
    private lateinit var backgroundLooper: Looper


    // --- StateFlow for current energy level ---
    private lateinit var currentEnergyLevelState: StateFlow<LocationEnergyLevel>

    // 2. INJECT THE HEART RATE REPOSITORY
    @Inject
    lateinit var heartRateRepository: HeartRateRepository


    // --- Continuous Tracking State (Session-long) ---
    private var continuousSessionStartTimeMillis: Long = 0L
    private var continuousDistanceMeters: Float = 0f
    private var continuousCaloriesBurned: Float = 0f
    private var continuousMaxSpeedKph: Double = 0.0
    private var continuousElevationGainMeters: Double = 0.0
    private var continuousElevationLossMeters: Double = 0.0

    // --- Formal Ride Segment State ---
    private var currentFormalRideId: String? = null
    private val formalRideTrackPoints = mutableListOf<Location>()
    private var formalRideSegmentStartTimeMillis: Long = 0L
    private var formalRideSegmentUiResetTimeMillis: Long = 0L
    private var formalRideSegmentStartOffsetDistanceMeters: Float = 0f
    private var formalRideSegmentMaxSpeedKph: Double = 0.0
    private var currentFormalRideHighestCalories: Int = 0
    private var formalRideElevationGainMeters: Double = 0.0
    private var formalRideElevationLossMeters: Double = 0.0


    private lateinit var userStatsFlow: Flow<UserStats>
    private var caloriesCalculationJob: Job? = null

    private val binder = LocalBinder()

    private val _rideInfo = MutableStateFlow(getInitialRideInfo())
    val rideInfo = _rideInfo.asStateFlow()

    private var currentActualGpsIntervalMillis: Long = 0L // Stores the actual interval used

    // 1. Instantiate the Simulator
    // NOTE: Remove after video.
    private val demoSimulator = DemoModeSimulator()


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

        locationProcessingThread = HandlerThread("LocationProcessingThread")
        locationProcessingThread.start()
        backgroundLooper = locationProcessingThread.looper

        currentEnergyLevelState = appSettingsRepository.gpsAccuracyFlow
            .stateIn(
                scope = lifecycleScope,
                started = SharingStarted.Eagerly,
                initialValue = LocationEnergyLevel.BALANCED
            )

        userStatsFlow = userProfileRepository.weightFlow.map { weightString ->
            val weightKg = weightString.toFloatOrNull() ?: 70f
            UserStats(heightCm = 0f, weightKg = weightKg)
        }

        // 3. START COLLECTING HEART RATE HERE
        lifecycleScope.launch {
            // This collects from the repository (WearHealth or BLE)
            // and updates the main Ride Info state.
            heartRateRepository.heartRate.collect { bpm ->
                val current = _rideInfo.value

                // Optimization: Only update flow if value changed
                if (current.heartbeat != bpm) {
                    _rideInfo.value = current.copy(
                        heartbeat = bpm
                    )
                    Log.v("BikeForegroundService", "❤ Heart Rate: $bpm")
                }
            }
        }

        lifecycleScope.launch {
            combine(
                currentEnergyLevelState,
                appSettingsRepository.longRideEnabledFlow
            ) { energyLevel, isLongRide ->
                Pair(energyLevel, isLongRide)
            }.collect { (newLevel, isLongRide) ->
                Log.d(
                    "BikeServiceDebugger",
                    ">>> SETTING CHANGE RECEIVED: Level: ${newLevel.name}, LongRide: $isLongRide"
                )
                val interval: Long
                val minInterval: Long
                when (_rideInfo.value.rideState) {
                    RideState.Riding -> {
                        Log.d(
                            "BikeForegroundService",
                            "Energy/LongRide changed to ${newLevel.name}/$isLongRide MID-RIDE. Updating to ACTIVE interval."
                        )
                        interval = newLevel.activeRideIntervalMillis
                        minInterval = newLevel.activeRideMinUpdateIntervalMillis
                    }
                    else -> {
                        Log.d(
                            "BikeForegroundService",
                            "Energy/LongRide changed to ${newLevel.name}/$isLongRide while PASSIVE. Updating to passive interval."
                        )
                        interval = newLevel.passiveTrackingIntervalMillis
                        minInterval = newLevel.passiveTrackingMinUpdateIntervalMillis
                    }
                }
                startLocationUpdates(interval, minInterval, isLongRide)
            }
        }
        startOrRestartCalorieCalculation(isFormalRideActive = false)
        Log.d("BikeForegroundService", "Service created.")
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

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        locationProcessingThread.quitSafely()
        Log.d("BikeForegroundService", "Service destroyed, location updates removed, thread quit.")
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(
        intervalMillis: Long,
        minUpdateIntervalMillis: Long,
        isLongRide: Boolean
    ) {
        var actualIntervalMillis = intervalMillis
        var actualMinUpdateIntervalMillis = minUpdateIntervalMillis

        if (isLongRide) {
            actualIntervalMillis *= 2
            actualMinUpdateIntervalMillis *= 2
        }
        actualIntervalMillis = max(actualIntervalMillis, MIN_ALLOWED_GPS_INTERVAL_MS)
        actualMinUpdateIntervalMillis = max(actualMinUpdateIntervalMillis, MIN_ALLOWED_GPS_INTERVAL_MS)
        currentActualGpsIntervalMillis = actualIntervalMillis

        Log.d("GPS_TIMING_DEBUG", "Applying new GPS interval: $actualIntervalMillis ms (LongRide: $isLongRide)")
        Log.d("BikeForegroundService","Attempting to start location updates with interval: $actualIntervalMillis ms, minInterval: $actualMinUpdateIntervalMillis ms on looper: ${backgroundLooper.thread.name}")
        fusedLocationClient.removeLocationUpdates(locationCallback)
        val locationRequest = GmsLocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, actualIntervalMillis)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(actualMinUpdateIntervalMillis)
            .setMaxUpdateDelayMillis(actualIntervalMillis)
            .build()
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, backgroundLooper)
            Log.d("BikeForegroundService", "Successfully requested location updates.")
        } catch (e: SecurityException) {
            Log.e("BikeForegroundService", "Missing location permissions. Cannot start location updates.", e)
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
        var currentRidePath: List<GpsFix> = emptyList()

        continuousMaxSpeedKph = max(continuousMaxSpeedKph, speedKph)
        val prevLocationLatLng = _rideInfo.value.location
        if (prevLocationLatLng != null && (prevLocationLatLng.latitude != 0.0 || prevLocationLatLng.longitude != 0.0)) {
            val lastLoc = Location("").apply {
                latitude = prevLocationLatLng.latitude; longitude = prevLocationLatLng.longitude
                if (_rideInfo.value.elevation != 0.0) altitude = _rideInfo.value.elevation
            }
            if (location.accuracy <= MAX_ACCURACY_THRESHOLD_METERS) {
                val distanceIncrement = location.distanceTo(lastLoc)
                if (distanceIncrement >= MIN_DISTANCE_THRESHOLD_METERS) {
                    continuousDistanceMeters += distanceIncrement
                }
                if (location.hasAltitude() && lastLoc.hasAltitude()) {
                    val altitudeChange = location.altitude - lastLoc.altitude
                    if (altitudeChange > 0) {
                        continuousElevationGainMeters += altitudeChange
                        if (isFormalRideActive) formalRideElevationGainMeters += altitudeChange
                    } else if (altitudeChange < 0) {
                        continuousElevationLossMeters += -altitudeChange
                        if (isFormalRideActive) formalRideElevationLossMeters += -altitudeChange
                    }
                }
            }
        }

        var displayDistanceKm: Float
        var displayCalories: Int
        var displayDuration: String
        var displayMaxSpeed: Double
        var displayAverageSpeed: Double
        var displayElevationGain: Double
        var displayElevationLoss: Double

        if (isFormalRideActive) {
            formalRideTrackPoints.add(location) // Add current location to the list
            currentRidePath = formalRideTrackPoints.map { loc -> // Map to GpsFix list
                GpsFix(
                    lat = loc.latitude,
                    lng = loc.longitude,
                    timeMs = loc.time,
                    altitude = if (loc.hasAltitude()) loc.altitude else null,
                    speed = if (loc.hasSpeed()) loc.speed else null,
                    accuracy = if (loc.hasAccuracy()) loc.accuracy else null
                )
            }

            val segmentDistanceMeters = continuousDistanceMeters - formalRideSegmentStartOffsetDistanceMeters
            val segmentDurationMillis = System.currentTimeMillis() - formalRideSegmentStartTimeMillis
            val segmentDurationSeconds = segmentDurationMillis / 1000f

            displayDistanceKm = segmentDistanceMeters / 1000f
            displayCalories = _rideInfo.value.caloriesBurned
            displayDuration = formatDuration(System.currentTimeMillis() - formalRideSegmentUiResetTimeMillis)
            formalRideSegmentMaxSpeedKph = max(formalRideSegmentMaxSpeedKph, speedKph)
            displayMaxSpeed = formalRideSegmentMaxSpeedKph
            displayAverageSpeed = if (segmentDurationSeconds > 0 && segmentDistanceMeters > 0) (segmentDistanceMeters / segmentDurationSeconds) * 3.6 else 0.0
            displayElevationGain = formalRideElevationGainMeters
            displayElevationLoss = formalRideElevationLossMeters
        } else {
            val continuousDurationMillis = System.currentTimeMillis() - continuousSessionStartTimeMillis
            val continuousDurationSeconds = continuousDurationMillis / 1000f
            displayDistanceKm = continuousDistanceMeters / 1000f
            displayCalories = continuousCaloriesBurned.toInt()
            displayDuration = formatDuration(continuousDurationMillis)
            displayMaxSpeed = continuousMaxSpeedKph
            displayAverageSpeed = if (continuousDurationSeconds > 0 && continuousDistanceMeters > 0) (continuousDistanceMeters / continuousDurationSeconds) * 3.6 else 0.0
            displayElevationGain = continuousElevationGainMeters
            displayElevationLoss = continuousElevationLossMeters
            // currentRidePath remains emptyList() if not a formal ride
        }

        val newInfo = _rideInfo.value.copy(
            location = LatLng(location.latitude, location.longitude),
            currentSpeed = speedKph,
            currentTripDistance = displayDistanceKm,
            caloriesBurned = displayCalories,
            rideDuration = displayDuration,
            maxSpeed = displayMaxSpeed,
            averageSpeed = displayAverageSpeed,
            elevation = location.altitude,
            elevationGain = displayElevationGain,
            elevationLoss = displayElevationLoss,
            heading = if (location.hasBearing()) location.bearing else _rideInfo.value.heading,
            rideState = _rideInfo.value.rideState,
            lastGpsUpdateTime = System.currentTimeMillis(),
            gpsUpdateIntervalMillis = currentActualGpsIntervalMillis,
            ridePath = currentRidePath // Assign the mapped GpsFix list or emptyList
        )

        // =================================================================
        // DEMO VIDEO LOGIC: INTERCEPT HERE
        // =================================================================
        // This takes the Real Emulator Speed + Toggles the Connection
        demoSimulator.process(newInfo)
        // =================================================================

        val newRideInfo = newInfo // videoReadyInfo

        // 1. Update Local State (for Notification)
        _rideInfo.value = newRideInfo

        // 2. PUSH TO REPOSITORY (So Glass can see it!)
        lifecycleScope.launch {
            Log.d("DEBUG_PATH", "1. SERVICE: Pushing speed ${newRideInfo.currentSpeed} to Repo") // <--- ADD THIS
            bikeRepository.updateRideInfo(newRideInfo)
        }

        if (isFormalRideActive) {
            startForegroundService()
        }
    }

    private fun startOrRestartCalorieCalculation(isFormalRideActive: Boolean) {
        caloriesCalculationJob?.cancel()
        Log.d("BikeForegroundService", "Starting/Restarting calorie calculation. FormalRideActive: $isFormalRideActive")
        if (!::userStatsFlow.isInitialized) {
            Log.e("BikeForegroundService", "userStatsFlow not initialized! Re-initializing.")
            userStatsFlow = userProfileRepository.weightFlow.map { weightString: String ->
                val weightKg: Float = weightString.toFloatOrNull() ?: 70f
                UserStats(heightCm = 0f, weightKg = weightKg)
            }
        }
        val distanceKmFlow: Flow<Float>
        if (isFormalRideActive) {
            distanceKmFlow = _rideInfo.map { (continuousDistanceMeters - formalRideSegmentStartOffsetDistanceMeters) / 1000f }
        } else {
            distanceKmFlow = _rideInfo.map { continuousDistanceMeters / 1000f }
        }
        val speedKmhFlow = _rideInfo.map { it.currentSpeed.toFloat() }
        caloriesCalculationJob = lifecycleScope.launch {
            calculateCaloriesUseCase(distanceKmFlow, speedKmhFlow, userStatsFlow)
                .collect { calculatedCalories ->
                    val newCalories = calculatedCalories.toInt()
                    if (isFormalRideActive) {
                        if (newCalories >= currentFormalRideHighestCalories) {
                            currentFormalRideHighestCalories = newCalories
                            _rideInfo.value = _rideInfo.value.copy(caloriesBurned = currentFormalRideHighestCalories)
                        } else {
                            _rideInfo.value = _rideInfo.value.copy(caloriesBurned = currentFormalRideHighestCalories)
                        }
                    } else {
                        continuousCaloriesBurned = newCalories.toFloat()
                        if (_rideInfo.value.rideState != RideState.Riding) {
                            _rideInfo.value = _rideInfo.value.copy(caloriesBurned = continuousCaloriesBurned.toInt())
                        }
                    }
                }
        }
    }

    private fun startFormalRide() {
        if (_rideInfo.value.rideState == RideState.Riding) {
            Log.d("BikeForegroundService", "Formal ride already in progress.")
            return
        }
        Log.d("BikeForegroundService", "Starting formal ride. Switching to ACTIVE interval.")
        lifecycleScope.launch {
            val currentLevel = currentEnergyLevelState.first()
            val isLongRide = appSettingsRepository.longRideEnabledFlow.first()
            startLocationUpdates(
                intervalMillis = currentLevel.activeRideIntervalMillis,
                minUpdateIntervalMillis = currentLevel.activeRideMinUpdateIntervalMillis,
                isLongRide = isLongRide
            )
        }
        currentFormalRideId = UUID.randomUUID().toString()
        formalRideSegmentStartTimeMillis = System.currentTimeMillis()
        formalRideSegmentUiResetTimeMillis = System.currentTimeMillis()
        formalRideTrackPoints.clear()
        currentFormalRideHighestCalories = 0
        formalRideElevationGainMeters = 0.0
        formalRideElevationLossMeters = 0.0
        formalRideSegmentStartOffsetDistanceMeters = continuousDistanceMeters
        formalRideSegmentMaxSpeedKph = 0.0
        _rideInfo.value = _rideInfo.value.copy(
            rideState = RideState.Riding,
            currentTripDistance = 0f,
            caloriesBurned = 0,
            rideDuration = "00:00",
            maxSpeed = 0.0,
            averageSpeed = 0.0,
            elevationGain = 0.0,
            elevationLoss = 0.0,
            ridePath = emptyList() // Clear path when new ride starts
        )
        startOrRestartCalorieCalculation(isFormalRideActive = true)
        startForegroundService()
    }

    private fun stopAndFinalizeFormalRide() {
        val rideIdToFinalize = currentFormalRideId
        if (_rideInfo.value.rideState != RideState.Riding || rideIdToFinalize == null) {
            Log.d("BikeForegroundService", "No active formal ride to stop.")
            return
        }
        Log.d("BikeForegroundService", "Stopping formal ride. Reverting to PASSIVE GPS interval.")

        // Capture the final path BEFORE clearing formalRideTrackPoints
        val finalRidePath = formalRideTrackPoints.map { loc -> 
            GpsFix(
                lat = loc.latitude,
                lng = loc.longitude,
                timeMs = loc.time,
                altitude = if (loc.hasAltitude()) loc.altitude else null,
                speed = if (loc.hasSpeed()) loc.speed else null,
                accuracy = if (loc.hasAccuracy()) loc.accuracy else null
            )
        }

        _rideInfo.value = _rideInfo.value.copy(
            rideState = RideState.NotStarted, 
            ridePath = finalRidePath // Persist final path for UI briefly, will be cleared by resetServiceStateAndStopForeground
        )

        lifecycleScope.launch {
            val currentLevel = currentEnergyLevelState.first()
            val isLongRide = appSettingsRepository.longRideEnabledFlow.first()
            startLocationUpdates(
                intervalMillis = currentLevel.passiveTrackingIntervalMillis,
                minUpdateIntervalMillis = currentLevel.passiveTrackingMinUpdateIntervalMillis,
                isLongRide = isLongRide
            )
        }
        val segmentDistanceMeters = continuousDistanceMeters - formalRideSegmentStartOffsetDistanceMeters
        val segmentCalories = currentFormalRideHighestCalories // Use the highest recorded for the segment
        val segmentDurationMillis = System.currentTimeMillis() - formalRideSegmentStartTimeMillis
        val segmentDurationSeconds = segmentDurationMillis / 1000f
        val segmentMaxSpeed = formalRideSegmentMaxSpeedKph
        val averageSpeedKph = if (segmentDurationSeconds > 0 && segmentDistanceMeters > 0) (segmentDistanceMeters.toDouble() / segmentDurationSeconds.toDouble()) * 3.6 else 0.0

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
            elevationGain = formalRideElevationGainMeters.toFloat(),
            elevationLoss = formalRideElevationLossMeters.toFloat(),
            caloriesBurned = segmentCalories,
            isHealthDataSynced = false,
            weatherCondition = _rideInfo.value.bikeWeatherInfo?.conditionDescription
        )
        val locationEntities = formalRideTrackPoints.map { location ->
            RideLocationEntity(
                rideId = rideIdToFinalize,
                timestamp = location.time,
                lat = location.latitude,
                lng = location.longitude,
                elevation = location.altitude.toFloat()
            )
        }
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                repo.insertRideWithLocations(rideSummaryEntity, locationEntities)
                Log.d("BikeForegroundService", "Formal ride $rideIdToFinalize saved.")
            }
            resetServiceStateAndStopForeground() 
        }
    }

    private fun resetServiceStateAndStopForeground() {
        Log.d("BikeForegroundService", "Full service reset: continuous and formal states.")
        continuousSessionStartTimeMillis = System.currentTimeMillis()
        continuousDistanceMeters = 0f
        continuousCaloriesBurned = 0f
        continuousMaxSpeedKph = 0.0
        continuousElevationGainMeters = 0.0
        continuousElevationLossMeters = 0.0

        currentFormalRideId = null
        formalRideTrackPoints.clear()
        formalRideSegmentStartOffsetDistanceMeters = 0f
        formalRideSegmentUiResetTimeMillis = 0L
        formalRideSegmentMaxSpeedKph = 0.0
        currentFormalRideHighestCalories = 0
        formalRideElevationGainMeters = 0.0
        formalRideElevationLossMeters = 0.0

        // 1. Create the Reset Info Object
        val resetInfo = getInitialRideInfo().copy(
            location = _rideInfo.value.location, 
            bikeWeatherInfo = _rideInfo.value.bikeWeatherInfo, 
            rideState = RideState.NotStarted,
            ridePath = emptyList() // Ensure path is cleared on full reset
        )

        // 2. Update Local Service State
        _rideInfo.value = resetInfo

        // 3. --- ADD THIS: PUSH RESET TO REPOSITORY ---
        // This clears the Glass UI back to "0.0" instantly
        lifecycleScope.launch {
            bikeRepository.updateRideInfo(resetInfo)
        }
        // ---------------------------------------------


        caloriesCalculationJob?.cancel()
        startOrRestartCalorieCalculation(isFormalRideActive = false)
        stopForeground(STOP_FOREGROUND_REMOVE)
        Log.d("BikeForegroundService", "Service reset complete.")
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
        val rideState = _rideInfo.value
        val currentSpeedFormatted = String.format("%.1f", rideState.currentSpeed)
        val currentDistanceFormatted = String.format("%.1f", rideState.currentTripDistance)
        val currentElevationGainFormatted = String.format("%.0f", rideState.elevationGain)
        val notificationText = "Speed: $currentSpeedFormatted km/h, Dist: $currentDistanceFormatted km, Elev Gain: $currentElevationGainFormatted m"
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Bike Ride Active")
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.ic_bike)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
        Log.d("BikeForegroundService", "Notification Updated. Text: $notificationText")
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
        private const val MAX_ACCURACY_THRESHOLD_METERS = 30f
        private const val MIN_DISTANCE_THRESHOLD_METERS = 5f
        private const val MIN_ALLOWED_GPS_INTERVAL_MS = 500L

        fun getInitialRideInfo(): BikeRideInfo = BikeRideInfo(
            location = null,
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
            settings = persistentMapOf(),
            heading = 0f,
            elevation = 0.0,
            isBikeConnected = false,
            batteryLevel = null,
            motorPower = null,
            rideState = RideState.NotStarted,
            bikeWeatherInfo = null,
            heartbeat = null,
            gpsUpdateIntervalMillis = 0L,
            ridePath = emptyList() // Ensure initial info has empty path
        )
    }
}
