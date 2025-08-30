package com.ylabz.basepro.applications.bike.features.main.service

import android.R
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.os.HandlerThread
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
// TODO: Uncomment and verify R import
// import com.ylabz.basepro.applications.bike.features.main.R
// TODO: Uncomment and verify MainActivity import (or your UI target for notification)
// import com.ylabz.basepro.applications.bike.features.main.ui.MainActivity
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
// TODO: Define RideLocationEntity and its mapping
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.applications.bike.database.repository.AppSettingsRepository
import com.ylabz.basepro.applications.bike.database.repository.UserProfileRepository
import com.ylabz.basepro.applications.bike.features.main.usecase.CalculateCaloriesUseCase
import com.ylabz.basepro.applications.bike.features.main.usecase.UserStats
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.LocationEnergyLevel
import com.ylabz.basepro.core.model.bike.RideState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest as GmsLocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.max

@AndroidEntryPoint
class BikeForegroundService_Update : LifecycleService() {

    @Inject
    lateinit var repo: BikeRideRepo

    @Inject
    lateinit var calculateCaloriesUseCase: CalculateCaloriesUseCase

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    @Inject
    lateinit var appSettingsRepository: AppSettingsRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationProcessingThread: HandlerThread
    private lateinit var backgroundLooper: Looper

    private lateinit var currentEnergyLevelState: StateFlow<LocationEnergyLevel>
    private lateinit var isLongRideActiveState: StateFlow<Boolean>
    private lateinit var userStatsFlow: Flow<UserStats>

    private var caloriesCalculationJob: Job? = null
    private val binder = LocalBinder()

    private val _rideInfo = MutableStateFlow(getInitialRideInfo())
    val rideInfo: StateFlow<BikeRideInfo> = _rideInfo.asStateFlow()

    private var currentActualGpsIntervalMillis: Long = 0L

    // --- Continuous Tracking State (Session-long) ---
    private var continuousSessionStartTimeMillis: Long = 0L
    private var continuousDistanceMeters: Float = 0f
    private var continuousCaloriesBurned: Float = 0f // Tracks total calories for the session
    private var continuousMaxSpeedKph: Double = 0.0
    private var continuousElevationGainMeters: Double = 0.0
    private var continuousElevationLossMeters: Double = 0.0
    private var lastLocationForContinuousElevation: Location? = null


    // --- Formal Ride Segment State ---
    private var currentFormalRideId: String? = null
    private val formalRideTrackPoints = mutableListOf<Location>()
    private var formalRideSegmentStartTimeMillis: Long = 0L
    private var formalRideSegmentUiResetTimeMillis: Long = 0L // For UI duration display reset
    private var formalRideSegmentStartOffsetDistanceMeters: Float = 0f
    private var formalRideSegmentMaxSpeedKph: Double = 0.0
    private var currentFormalRideHighestCalories: Float = 0f // Tracks highest calories for the *current formal ride*
    private var formalRideElevationGainMeters: Double = 0.0
    private var formalRideElevationLossMeters: Double = 0.0
    private var lastLocationForFormalElevation: Location? = null


    inner class LocalBinder : Binder() {
        fun getService(): BikeForegroundService_Update = this@BikeForegroundService_Update
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

        isLongRideActiveState = appSettingsRepository.longRideEnabledFlow
            .stateIn(
                scope = lifecycleScope,
                started = SharingStarted.Eagerly,
                initialValue = false
            )

        _rideInfo.value = _rideInfo.value.copy(
            gpsUpdateIntervalMillis = currentEnergyLevelState.value.passiveTrackingIntervalMillis
        )

        userStatsFlow = combine(
            userProfileRepository.heightFlow,
            userProfileRepository.weightFlow
        ) { heightString, weightString ->
            val heightCm = heightString.toFloatOrNull() ?: DEFAULT_USER_HEIGHT_CM
            val weightKg = weightString.toFloatOrNull() ?: DEFAULT_USER_WEIGHT_KG
            UserStats(heightCm = heightCm, weightKg = weightKg)
        }.shareIn(lifecycleScope, SharingStarted.Eagerly, replay = 1)


        lifecycleScope.launch {
            combine(currentEnergyLevelState, isLongRideActiveState) { energyLevel, isLongRide ->
                Pair(energyLevel, isLongRide)
            }.collect { (newLevel, currentIsLongRide) ->
                Log.d(TAG, ">>> SETTING CHANGE RECEIVED: Level: ${newLevel.name}, LongRide: $currentIsLongRide")
                updateLocationTrackingBasedOnState()
            }
        }
        updateLocationTrackingBasedOnState()
        startOrRestartCalorieCalculation(isFormalRideActive = false)
        Log.d(TAG, "Service created.")
    }

    private fun updateLocationTrackingBasedOnState() {
        val rideState = _rideInfo.value.rideState
        val energyLevel = currentEnergyLevelState.value
        val isLongRide = isLongRideActiveState.value

        val interval: Long
        val minInterval: Long

        when (rideState) {
            RideState.Riding -> {
                Log.d(TAG, "Energy/LongRide changed to ${energyLevel.name}/$isLongRide MID-RIDE. Updating to ACTIVE interval.")
                interval = energyLevel.activeRideIntervalMillis
                minInterval = energyLevel.activeRideMinUpdateIntervalMillis
            }
            else -> { // NotStarted, Paused, Stopped
                Log.d(TAG, "Energy/LongRide changed to ${energyLevel.name}/$isLongRide while PASSIVE. Updating to passive interval.")
                interval = energyLevel.passiveTrackingIntervalMillis
                minInterval = energyLevel.passiveTrackingMinUpdateIntervalMillis
            }
        }
        startLocationUpdates(interval, minInterval, isLongRide)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.action?.let { action ->
            Log.d(TAG, "Received action: $action")
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
        caloriesCalculationJob?.cancel()
        Log.d(TAG, "Service destroyed.")
    }

    private fun getInitialRideInfo(): BikeRideInfo {
        val emptySettings: ImmutableMap<String, ImmutableList<String>> = persistentMapOf()
        return BikeRideInfo(
            location = null,
            currentSpeed = 0.0,
            caloriesBurned = 0,
            rideDuration = formatDuration(0L),
            maxSpeed = 0.0,
            averageSpeed = 0.0,
            elevation = 0.0,
            elevationGain = 0.0,
            elevationLoss = 0.0,
            heading = 0f,
            rideState = RideState.NotStarted,
            lastGpsUpdateTime = 0L,
            gpsUpdateIntervalMillis = LocationEnergyLevel.BALANCED.passiveTrackingIntervalMillis, // Static default
            totalTripDistance = null,
            remainingDistance = null,
            settings = emptySettings,
            isBikeConnected = false,
            heartbeat = null,
            batteryLevel = null,
            motorPower = null,
            bikeWeatherInfo = null,
            currentTripDistance = 0F
        )
    }

    private fun formatDuration(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60))
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(intervalMillis: Long, minUpdateIntervalMillis: Long, isLongRide: Boolean) {
        var actualIntervalMillis = intervalMillis
        var actualMinUpdateIntervalMillis = minUpdateIntervalMillis

        if (isLongRide) {
            actualIntervalMillis = (actualIntervalMillis * LONG_RIDE_INTERVAL_MULTIPLIER).toLong()
            actualMinUpdateIntervalMillis = (actualMinUpdateIntervalMillis * LONG_RIDE_INTERVAL_MULTIPLIER).toLong()
        }
        actualIntervalMillis = max(actualIntervalMillis, MIN_ALLOWED_GPS_INTERVAL_MS)
        actualMinUpdateIntervalMillis = max(actualMinUpdateIntervalMillis, MIN_ALLOWED_GPS_INTERVAL_MS)

        if (actualIntervalMillis == currentActualGpsIntervalMillis && fusedLocationClient.lastLocation.isSuccessful) {
            Log.d(GPS_TIMING_DEBUG_TAG, "GPS interval unchanged: $actualIntervalMillis ms. Skipping re-request.")
            // return // Be cautious with this
        }

        currentActualGpsIntervalMillis = actualIntervalMillis
        _rideInfo.value = _rideInfo.value.copy(gpsUpdateIntervalMillis = currentActualGpsIntervalMillis)

        Log.d(GPS_TIMING_DEBUG_TAG, "Applying new GPS interval: $actualIntervalMillis ms (LongRide: $isLongRide)")
        Log.d(TAG, "Attempting to start location updates with interval: $actualIntervalMillis ms, minInterval: $actualMinUpdateIntervalMillis ms on looper: ${backgroundLooper.thread.name}")

        fusedLocationClient.removeLocationUpdates(locationCallback)

        val locationRequest = GmsLocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, actualIntervalMillis)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(actualMinUpdateIntervalMillis)
            .setMaxUpdateDelayMillis(actualIntervalMillis * 2)
            .build()
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, backgroundLooper)
            Log.d(TAG, "Successfully requested location updates.")
        } catch (e: SecurityException) {
            Log.e(TAG, "Missing location permissions. Cannot start location updates.", e)
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
        val currentTime = System.currentTimeMillis()
        val speedKph = location.speed * MPS_TO_KPH_CONVERSION_FACTOR
        val isFormalRideActive = _rideInfo.value.rideState == RideState.Riding

        continuousMaxSpeedKph = max(continuousMaxSpeedKph, speedKph)
        val prevContinuousLocationLatLng = _rideInfo.value.location
        var distanceIncrementMeters = 0f

        if (prevContinuousLocationLatLng != null && (prevContinuousLocationLatLng.latitude != 0.0 || prevContinuousLocationLatLng.longitude != 0.0)) {
            val lastKnownGlobalLoc = Location("previousGlobalLocation").apply {
                latitude = prevContinuousLocationLatLng.latitude
                longitude = prevContinuousLocationLatLng.longitude
            }

            if (location.accuracy <= MAX_ACCURACY_THRESHOLD_METERS) {
                val distInc = location.distanceTo(lastKnownGlobalLoc)
                if (distInc >= MIN_DISTANCE_THRESHOLD_METERS) {
                    continuousDistanceMeters += distInc
                    distanceIncrementMeters = distInc
                }

                if (location.hasAltitude()) {
                    lastLocationForContinuousElevation?.let { prevAltLoc ->
                         if (prevAltLoc.hasAltitude()) {
                            val altitudeChange = location.altitude - prevAltLoc.altitude
                            if (altitudeChange > 0) continuousElevationGainMeters += altitudeChange
                            else if (altitudeChange < 0) continuousElevationLossMeters += -altitudeChange
                        }
                    }
                    lastLocationForContinuousElevation = Location(location)
                }
            }
        } else {
            if (location.hasAltitude()) {
                lastLocationForContinuousElevation = Location(location)
            }
        }

        if (isFormalRideActive) {
            formalRideTrackPoints.add(location)
            formalRideSegmentMaxSpeedKph = max(formalRideSegmentMaxSpeedKph, speedKph)
            if (location.hasAltitude()) {
                lastLocationForFormalElevation?.let { prevFormalLoc ->
                     if (prevFormalLoc.hasAltitude()) {
                        val altitudeChange = location.altitude - prevFormalLoc.altitude
                        if (altitudeChange > 0) formalRideElevationGainMeters += altitudeChange
                        else if (altitudeChange < 0) formalRideElevationLossMeters += -altitudeChange
                    }
                }
                lastLocationForFormalElevation = Location(location)
            }
        }

        val displayDistanceKm: Float
        val displayDuration: String
        val displayMaxSpeed: Double
        val displayAverageSpeed: Double
        val displayElevationGain: Double
        val displayElevationLoss: Double

        if (isFormalRideActive) {
            val segmentDistanceMeters = continuousDistanceMeters - formalRideSegmentStartOffsetDistanceMeters
            val segmentDurationMillis = currentTime - formalRideSegmentStartTimeMillis
            val segmentDurationUiMillis = currentTime - formalRideSegmentUiResetTimeMillis
            val segmentDurationSeconds = segmentDurationMillis / MILLIS_TO_SECONDS_FACTOR

            displayDistanceKm = segmentDistanceMeters / METERS_TO_KILOMETERS_FACTOR
            displayDuration = formatDuration(segmentDurationUiMillis)
            displayMaxSpeed = formalRideSegmentMaxSpeedKph
            displayAverageSpeed = if (segmentDurationSeconds > 0 && segmentDistanceMeters > 0) {
                (segmentDistanceMeters / segmentDurationSeconds) * MPS_TO_KPH_CONVERSION_FACTOR
            } else { 0.0 }
            displayElevationGain = formalRideElevationGainMeters
            displayElevationLoss = formalRideElevationLossMeters
            startForegroundServiceNotification()
        } else {
            val continuousDurationMillis = currentTime - continuousSessionStartTimeMillis
            val continuousDurationSeconds = continuousDurationMillis / MILLIS_TO_SECONDS_FACTOR

            displayDistanceKm = continuousDistanceMeters / METERS_TO_KILOMETERS_FACTOR
            displayDuration = formatDuration(continuousDurationMillis)
            displayMaxSpeed = continuousMaxSpeedKph
            displayAverageSpeed = if (continuousDurationSeconds > 0 && continuousDistanceMeters > 0) {
                (continuousDistanceMeters / continuousDurationSeconds) * MPS_TO_KPH_CONVERSION_FACTOR
            } else { 0.0 }
            displayElevationGain = continuousElevationGainMeters
            displayElevationLoss = continuousElevationLossMeters
        }

        // Calories are updated by the calorie job, this update is mainly for other fields
        _rideInfo.value = _rideInfo.value.copy(
            location = LatLng(location.latitude, location.longitude),
            currentSpeed = speedKph,
            totalTripDistance = displayDistanceKm, // Reflects formal ride or continuous based on state
            rideDuration = displayDuration,
            maxSpeed = displayMaxSpeed,
            averageSpeed = displayAverageSpeed,
            elevation = if (location.hasAltitude()) location.altitude else _rideInfo.value.elevation,
            elevationGain = displayElevationGain,
            elevationLoss = displayElevationLoss,
            heading = if (location.hasBearing()) location.bearing else _rideInfo.value.heading,
            lastGpsUpdateTime = currentTime
        )
    }

    private fun startOrRestartCalorieCalculation(isFormalRideActive: Boolean) {
        caloriesCalculationJob?.cancel()
        Log.d(TAG, "Setting up calorie calculation. FormalRideActive: $isFormalRideActive")

        val distanceKmFlow = _rideInfo.map { info ->
            info.totalTripDistance ?: 0f // Use totalTripDistance from BikeRideInfo
        }.distinctUntilChanged()

        val speedKmhFlow = _rideInfo.map { (it.currentSpeed).toFloat() }.distinctUntilChanged()

        caloriesCalculationJob = lifecycleScope.launch {
            calculateCaloriesUseCase(distanceKmFlow, speedKmhFlow, userStatsFlow)
                .collect { calculatedTotalCalories -> // This should be the total for the current context
                    if (isFormalRideActive) {
                        if (calculatedTotalCalories >= currentFormalRideHighestCalories.toInt()) {
                            _rideInfo.value = _rideInfo.value.copy(caloriesBurned = calculatedTotalCalories.toInt())
                            currentFormalRideHighestCalories = calculatedTotalCalories
                        } else {
                            _rideInfo.value = _rideInfo.value.copy(caloriesBurned = currentFormalRideHighestCalories.toInt())
                        }
                        Log.d(TAG, "Formal ride calories updated: ${_rideInfo.value.caloriesBurned}")
                    } else {
                        continuousCaloriesBurned = calculatedTotalCalories
                        _rideInfo.value = _rideInfo.value.copy(caloriesBurned = continuousCaloriesBurned.toInt())
                        Log.d(TAG, "Continuous calories updated: $continuousCaloriesBurned")
                    }
                }
        }
        Log.d(TAG, "Calories calculation job started. FormalRideActive: $isFormalRideActive")
    }

    private fun startFormalRide() {
        if (_rideInfo.value.rideState == RideState.Riding) {
            Log.w(TAG, "Attempted to start a new formal ride while one is already active.")
            return
        }
        Log.d(TAG, "Starting formal ride...")

        currentFormalRideId = UUID.randomUUID().toString()
        formalRideTrackPoints.clear()
        formalRideSegmentStartTimeMillis = System.currentTimeMillis()
        formalRideSegmentUiResetTimeMillis = formalRideSegmentStartTimeMillis
        formalRideSegmentStartOffsetDistanceMeters = continuousDistanceMeters
        formalRideSegmentMaxSpeedKph = 0.0
        currentFormalRideHighestCalories = 0f
        formalRideElevationGainMeters = 0.0
        formalRideElevationLossMeters = 0.0
        lastLocationForFormalElevation = null

        _rideInfo.value = _rideInfo.value.copy(
            rideState = RideState.Riding,
            totalTripDistance = 0f,
            caloriesBurned = 0,
            rideDuration = formatDuration(0L),
            averageSpeed = 0.0,
            maxSpeed = 0.0,
            elevationGain = 0.0,
            elevationLoss = 0.0
        )

        updateLocationTrackingBasedOnState()
        startOrRestartCalorieCalculation(isFormalRideActive = true)
        startForegroundServiceNotification()
        Log.i(TAG, "Formal ride $currentFormalRideId started.")
    }

    private fun stopAndFinalizeFormalRide() {
        if (_rideInfo.value.rideState != RideState.Riding || currentFormalRideId == null) {
            Log.w(TAG, "Attempted to stop formal ride when none is active or ID is null.")
            return
        }
        val rideIdToSave = currentFormalRideId!!
        Log.d(TAG, "Stopping formal ride: $rideIdToSave")

        val endTimeMillis = System.currentTimeMillis()
        val durationMillis = endTimeMillis - formalRideSegmentStartTimeMillis
        val segmentDistanceMeters = continuousDistanceMeters - formalRideSegmentStartOffsetDistanceMeters

        val rideEntity = BikeRideEntity(
            rideId = rideIdToSave,
            startTime = formalRideSegmentStartTimeMillis,
            endTime = endTimeMillis,
            totalDistance = (segmentDistanceMeters / METERS_TO_KILOMETERS_FACTOR),
            averageSpeed = if (durationMillis > 0 && segmentDistanceMeters > 0) {
                ((segmentDistanceMeters / (durationMillis / MILLIS_TO_SECONDS_FACTOR)) * MPS_TO_KPH_CONVERSION_FACTOR).toFloat()
            } else 0f,
            maxSpeed = formalRideSegmentMaxSpeedKph.toFloat(),
            elevationGain = formalRideElevationGainMeters.toFloat(),
            elevationLoss = formalRideElevationLossMeters.toFloat(),
            caloriesBurned = _rideInfo.value.caloriesBurned.toInt(),
            startLat = formalRideTrackPoints.firstOrNull()?.latitude ?: 0.0,
            startLng = formalRideTrackPoints.firstOrNull()?.longitude ?: 0.0,
            endLat = formalRideTrackPoints.lastOrNull()?.latitude ?: 0.0,
            endLng = formalRideTrackPoints.lastOrNull()?.longitude ?: 0.0,
            healthConnectRecordId = rideIdToSave
        )

        // TODO: Define RideLocationEntity and map formalRideTrackPoints to List<RideLocationEntity>
        val rideLocationEntities = emptyList<RideLocationEntity>()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                repo.insertRideWithLocations(rideEntity, rideLocationEntities)
                Log.d(TAG, "Formal ride $rideIdToSave saved to database.")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving ride $rideIdToSave to database", e)
            }
        }

        _rideInfo.value = _rideInfo.value.copy(
            rideState = RideState.NotStarted,
            totalTripDistance = continuousDistanceMeters / METERS_TO_KILOMETERS_FACTOR,
            caloriesBurned = continuousCaloriesBurned.toInt(),
            rideDuration = formatDuration(System.currentTimeMillis() - continuousSessionStartTimeMillis)
        )

        currentFormalRideId = null
        formalRideTrackPoints.clear()
        lastLocationForFormalElevation = null

        updateLocationTrackingBasedOnState()
        startOrRestartCalorieCalculation(isFormalRideActive = false)
        stopForeground(STOP_FOREGROUND_REMOVE)
        Log.i(TAG, "Formal ride $rideIdToSave stopped and finalized.")
    }

    private fun startForegroundServiceNotification() {
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(NOTIFICATION_CHANNEL_ID, "Bike Ride Tracking")
        } else { "" }

        // TODO: Replace 'this::class.java' with your actual UI target e.g., MainActivity::class.java
        // TODO: Ensure MainActivity (or your target) import is uncommented and correct
        val notificationIntent = Intent(this, this::class.java) // Placeholder
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, pendingIntentFlags)

        val rideData = _rideInfo.value
        val contentText = if (rideData.rideState == RideState.Riding) {
            "Time: ${rideData.rideDuration}, Dist: ${String.format("%.2f", rideData.totalTripDistance ?: 0f)} km, Speed: ${String.format("%.1f", rideData.currentSpeed)} kph"
        } else {
            "Tracking active. Tap to open."
        }
        // TODO: Replace with your actual notification icon resource
        // TODO: Ensure R file import is uncommented and correct
        val notificationIcon = R.drawable.ic_dialog_info // Placeholder icon

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Bike Ride Active")
            .setContentText(contentText)
            .setSmallIcon(notificationIcon)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()

        try {
            startForeground(NOTIFICATION_ID, notification)
            Log.d(TAG, "Foreground service notification started/updated.")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting foreground service", e)
        }
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            chan.lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
            val service = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)
        }
        return channelId
    }

    companion object {
        private const val TAG = "BikeFgService"
        private const val GPS_TIMING_DEBUG_TAG = "BikeFgService_GPS"

        const val ACTION_START_RIDE = "com.ylabz.basepro.applications.bike.ACTION_START_RIDE"
        const val ACTION_STOP_RIDE = "com.ylabz.basepro.applications.bike.ACTION_STOP_RIDE"

        const val NOTIFICATION_CHANNEL_ID = "bike_ride_channel"
        private const val NOTIFICATION_ID = 12345

        private const val MPS_TO_KPH_CONVERSION_FACTOR = 3.6
        private const val METERS_TO_KILOMETERS_FACTOR = 1000f
        private const val MILLIS_TO_SECONDS_FACTOR = 1000.0
        private const val LONG_RIDE_INTERVAL_MULTIPLIER = 1.5

        private const val MIN_ALLOWED_GPS_INTERVAL_MS = 1000L
        private const val MAX_ACCURACY_THRESHOLD_METERS = 50f
        private const val MIN_DISTANCE_THRESHOLD_METERS = 2f
        private const val DEFAULT_USER_HEIGHT_CM = 170f // Default user height if not available
        private const val DEFAULT_USER_WEIGHT_KG = 70f  // Default user weight if not available
    }
}
