package com.ylabz.basepro.core.data.service.health

/**
 * Manager for accessing and aggregating health data from Health Connect.
 */
import android.R.attr.end
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_UNAVAILABLE
import androidx.health.connect.client.HealthConnectFeatures
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.changes.Change
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseRouteResult
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.metadata.Device
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ChangesTokenRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.response.InsertRecordsResponse
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Mass
import com.example.healthconnectsample.data.ExerciseSessionData
import com.example.healthconnectsample.data.HealthConnectAppInfo
import com.example.healthconnectsample.data.randomSleepStage
import com.ylabz.basepro.core.model.health.SleepSessionData
import com.ylabz.basepro.core.network.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.io.InvalidObjectException
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random
import kotlin.reflect.KClass

//finished/src/main/java/com/example/healthconnect/codelab/data/HealthConnectManager.kt
// The minimum android level that can use Health Connect
const val MIN_SUPPORTED_SDK = Build.VERSION_CODES.O_MR1

private const val TAG = "HealthSessionManager"


/** Demonstrates reading and writing from Health Connect. */
class HealthSessionManager(private val context: Context) {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }
    // private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }


    val healthConnectCompatibleApps by lazy {
        val intent = Intent("androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE")

        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
            )
        } else {
            context.packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_ALL
            )
        }

        packages.associate {
            val icon = try {
                context.packageManager.getApplicationIcon(it.activityInfo.packageName)
            } catch (e: NotFoundException) {
                null
            }
            val label = context.packageManager.getApplicationLabel(it.activityInfo.applicationInfo)
                .toString()
            it.activityInfo.packageName to
                    HealthConnectAppInfo(
                        packageName = it.activityInfo.packageName,
                        icon = icon,
                        appLabel = label
                    )
        }
    }

    /*var availability = mutableStateOf(SDK_UNAVAILABLE)
        private set*/
    // Replace mutableStateOf with MutableStateFlow
    private val _availability = MutableStateFlow(SDK_UNAVAILABLE) // Mutable internal state
    val availability: StateFlow<Int> get() = _availability // Expose as immutable


    fun checkAvailability() {
        val sdkStatus = HealthConnectClient.getSdkStatus(context)
        _availability.value = sdkStatus // Properly modify the mutable state flow
    }

    init {
        checkAvailability()
    }

    /**
     * Determines whether all the specified permissions are already granted. It is recommended to
     * call [PermissionController.getGrantedPermissions] first in the permissions flow, as if the
     * permissions are already granted then there is no need to request permissions via
     * [PermissionController.createRequestPermissionResultContract].
     */
    suspend fun hasAllPermissions(permissions: Set<String>): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions()
            .containsAll(permissions)
    }

    fun requestPermissionsActivityContract(): ActivityResultContract<Set<String>, Set<String>> {
        return PermissionController.createRequestPermissionResultContract()
    }

    suspend fun revokeAllPermissions() {
        healthConnectClient.permissionController.revokeAllPermissions()
    }

    /**
     * Obtains a list of [ExerciseSessionRecord]s in a specified time frame. An Exercise Session Record is a
     * period of time given to an activity, that would make sense to a user, e.g. "Afternoon run"
     * etc. It does not necessarily mean, however, that the user was *running* for that entire time,
     * more that conceptually, this was the activity being undertaken.
     */
    suspend fun readExerciseSessions(start: Instant, end: Instant): List<ExerciseSessionRecord> {
        val request = ReadRecordsRequest(
            recordType = ExerciseSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records
    }

    suspend fun insertRecords(records: List<Record>): InsertRecordsResponse {
        //val res = healthConnectClient.insertRecords(records)

        Log.d("DebugSync", "HealthSessionManager.insertRecords → ${records.map { it::class.simpleName }}")
        val res = healthConnectClient.insertRecords(records)
        Log.d("DebugSync", "HealthSessionManager.insertRecords ← ${res.recordIdsList}")
        // Build a detailed mapping of record type -> (clientId? -> serverId)
        val details = records
            .mapIndexed { index, record ->
                val typeName = record::class.simpleName
                // If you populated a clientRecordId in metadata, you can grab it:
                val clientId = record.metadata.clientRecordId
                val serverId = res.recordIdsList.getOrNull(index)
                "$typeName(clientId=$clientId)->serverId=$serverId"
            }
            .joinToString(separator = "\n    ", prefix = "\n    ")

        Log.d("HealthSessionManager", buildString {
            append("insertRecords: inserted ${res.recordIdsList.size} record(s):")
            append(details)
        })
        // showAllRecs() // This might be excessive for every insert, consider if needed
        return res
    }

    suspend fun insertBikeRideWithAssociatedData(rideId: String, records: List<Record>): String {
        Log.d(TAG, "Attempting to insert bike ride data for rideId: $rideId consisting of ${records.map { it::class.simpleName }}")
        try {
            val response = healthConnectClient.insertRecords(records)
            Log.d(TAG, "Successfully inserted ${response.recordIdsList.size} records for rideId $rideId. HC IDs: ${response.recordIdsList}")
            // We return the original rideId. It's assumed to be the clientRecordId for the main session,
            // or at least the identifier the ViewModel layer needs to update the local database.
            // If you need the specific Health Connect generated ID for the ExerciseSessionRecord,
            // you would find it in response.recordIdsList. This requires identifying the
            // ExerciseSessionRecord within the 'records' list and getting its corresponding ID
            // from the response.recordIdsList using its index or clientRecordId if set.
            return rideId
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting bike ride data for rideId $rideId into Health Connect", e)
            throw e // Propagate the error to be handled by the caller (HealthViewModel)
        }
    }

    /**
     * Logs every record currently stored in Health Connect for the common record types.
     */
    @SuppressLint("RestrictedApi")
    suspend fun showAllRecs() {
        val now = Instant.now()

        // 1) Exercise sessions
        val sessionRequest = ReadRecordsRequest(
            recordType      = ExerciseSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.before(now)
        )
        val sessionResponse = healthConnectClient.readRecords(sessionRequest)
        Log.d("HealthSessionManager", "ExerciseSessionRecord: ${sessionResponse.records.size} record(s)")
        sessionResponse.records.forEach { Log.d("HealthSessionManager", it.toString()) }

        // 2) Steps
        val stepsRequest = ReadRecordsRequest(
            recordType      = StepsRecord::class,
            timeRangeFilter = TimeRangeFilter.before(now)
        )
        val stepsResponse = healthConnectClient.readRecords(stepsRequest)
        Log.d("HealthSessionManager", "StepsRecord: ${stepsResponse.records.size} record(s)")
        stepsResponse.records.forEach { Log.d("HealthSessionManager", it.toString()) }

        // 3) Distance
        val distanceRequest = ReadRecordsRequest(
            recordType      = DistanceRecord::class,
            timeRangeFilter = TimeRangeFilter.before(now)
        )
        val distanceResponse = healthConnectClient.readRecords(distanceRequest)
        Log.d("HealthSessionManager", "DistanceRecord: ${distanceResponse.records.size} record(s)")
        distanceResponse.records.forEach { Log.d("HealthSessionManager", it.toString()) }

        // 4) Calories
        val caloriesRequest = ReadRecordsRequest(
            recordType      = TotalCaloriesBurnedRecord::class,
            timeRangeFilter = TimeRangeFilter.before(now)
        )
        val caloriesResponse = healthConnectClient.readRecords(caloriesRequest)
        Log.d("HealthSessionManager", "TotalCaloriesBurnedRecord: ${caloriesResponse.records.size} record(s)")
        caloriesResponse.records.forEach { Log.d("HealthSessionManager", it.toString()) }

        // 5) Heart rate
        val hrRequest = ReadRecordsRequest(
            recordType      = HeartRateRecord::class,
            timeRangeFilter = TimeRangeFilter.before(now)
        )
        val hrResponse = healthConnectClient.readRecords(hrRequest)
        Log.d("HealthSessionManager", "HeartRateRecord: ${hrResponse.records.size} record(s)")
        hrResponse.records.forEach { Log.d("HealthSessionManager", it.toString()) }
    }




    /**
     * Writes an [ExerciseSessionRecord] to Health Connect, and additionally writes underlying data for
     * the session too, such as [StepsRecord], [DistanceRecord] etc.
     */
    suspend fun writeExerciseSessionNotUse(
        start: ZonedDateTime,
        end: ZonedDateTime
    ): InsertRecordsResponse {
        Log.d("HealthSessionManager", "Writing exercise session")
        return healthConnectClient.insertRecords(
            listOf<Record>(
                   StepsRecord(
                       startTime = start.toInstant(),
                       startZoneOffset = start.offset,
                       endTime = end.toInstant(),
                       endZoneOffset = end.offset,
                       count = (1000 + 1000 * Random.nextInt(3)).toLong(),
                       metadata = Metadata.autoRecorded(
                           device = Device(type = Device.TYPE_WATCH)
                       )

                   ),
                   DistanceRecord(
                       startTime = start.toInstant(),
                       startZoneOffset = start.offset,
                       endTime = end.toInstant(),
                       endZoneOffset = end.offset,
                       distance = Length.meters((1000 + 100 * Random.nextInt(20)).toDouble()),
                       metadata = Metadata.autoRecorded(
                           device = Device(type = Device.TYPE_WATCH)
                       )
                   ),
                   TotalCaloriesBurnedRecord(
                       startTime = start.toInstant(),
                       startZoneOffset = start.offset,
                       endTime = end.toInstant(),
                       endZoneOffset = end.offset,
                       energy = Energy.calories(140 + (Random.nextInt(20)) * 0.01),
                       metadata = Metadata.autoRecorded(
                           device = Device(type = Device.TYPE_WATCH)
                       )
                   )
               ) + buildHeartRateSeries(start, end)
        )
    }




    /**
     * TODO: Writes an [ExerciseSessionRecord] to Health Connect.
     */
    @SuppressLint("RestrictedApi")
    suspend fun writeExerciseSessionTest(){//start: ZonedDateTime, end: ZonedDateTime) {
        val start = ZonedDateTime.now()
        val sessionStartTime = Instant.now()
        val sessionDuration = Duration.ofMinutes(20)
        val sessionEndTime = sessionStartTime.plus(sessionDuration)
        val end = start.plus(sessionDuration)

        Log.d("HealthSessionManager", "Writing exercise session START")

        healthConnectClient.insertRecords(
            listOf<Record>(
                ExerciseSessionRecord(
                    metadata = Metadata.manualEntry(),
                    startTime = start.toInstant(),
                    startZoneOffset = start.offset,
                    endTime = end.toInstant(),
                    endZoneOffset = end.offset,
                    exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
                    title = "My Run #${Random.nextInt(0, 60)}"
                ),
                StepsRecord(
                    metadata = Metadata.manualEntry(),
                    startTime = start.toInstant(),
                    startZoneOffset = start.offset,
                    endTime = end.toInstant(),
                    endZoneOffset = end.offset,
                    count = (1000 + 1000 * Random.nextInt(3)).toLong()
                ),
                TotalCaloriesBurnedRecord(
                    metadata = Metadata.manualEntry(),
                    startTime = start.toInstant(),
                    startZoneOffset = start.offset,
                    endTime = end.toInstant(),
                    endZoneOffset = end.offset,
                    energy = Energy.calories((140 + Random.nextInt(20)) * 0.01)
                )
            ) + buildHeartRateSeries(start, end)
        )

        Log.d("HealthSessionManager", "Writing exercise session END")
    }

    @SuppressLint("RestrictedApi")
    suspend fun insertBikeSessionRecord(
        start: ZonedDateTime,
        end: ZonedDateTime,
        title: String = "Bike Ride",
        notes: String? = null
    ): InsertRecordsResponse {
        // 1) Build the ExerciseSessionRecord for BIKING
        val session = ExerciseSessionRecord(
            metadata       = Metadata.manualEntry(),
            startTime      = start.toInstant(),
            startZoneOffset = start.offset,
            endTime        = end.toInstant(),
            endZoneOffset   = end.offset,
            exerciseType   = ExerciseSessionRecord.EXERCISE_TYPE_BIKING,
            title          = title,
            notes          = notes
        )

        // 2) Insert it into Health Connect
        return healthConnectClient.insertRecords(listOf(session))
    }


    /**
     * Writes a “bike ride” as an ExerciseSessionRecord + underlying metrics.
     */
    @SuppressLint("RestrictedApi")
    suspend fun insertBikeExerciseSession(
        start: ZonedDateTime,
        end: ZonedDateTime,
        title: String = "Bike Ride",
        notes: String? = null
    ): InsertRecordsResponse {

        // 1) Build the ExerciseSessionRecord itself
        val session = ExerciseSessionRecord(
            startTime        = start.toInstant(),
            startZoneOffset  = start.offset,
            endTime          = end.toInstant(),
            endZoneOffset    = end.offset,
            exerciseType     = ExerciseSessionRecord.EXERCISE_TYPE_BIKING,
            title            = title,
            notes            = notes,
            // metadata tags it as auto-recorded by this app
            metadata         = Metadata.autoRecorded(
                device = Device(type = Device.TYPE_PHONE)
            )
        )

        // 2) Underlying raw and aggregate records
        val steps = StepsRecord(
            startTime       = start.toInstant(),
            startZoneOffset = start.offset,
            endTime         = end.toInstant(),
            endZoneOffset   = end.offset,
            count           = /* your total step count, e.g. tracker.steps() */ 1L,
            metadata        = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        val distance = DistanceRecord(
            startTime       = start.toInstant(),
            startZoneOffset = start.offset,
            endTime         = end.toInstant(),
            endZoneOffset   = end.offset,
            distance        = Length.meters(/* your total meters */ 0.0),
            metadata        = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        val calories = TotalCaloriesBurnedRecord(
            startTime       = start.toInstant(),
            startZoneOffset = start.offset,
            endTime         = end.toInstant(),
            endZoneOffset   = end.offset,
            energy          = Energy.calories(/* your calories */ 0.0),
            metadata        = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        // 3) Heart-rate series—use your existing builder
        val heartRate = buildHeartRateSeries(start, end)

        // 4) Insert them all in one batch
        return healthConnectClient.insertRecords(
            listOf<Record>(session, steps, distance, calories, heartRate)
        )
    }


    /**
     * Deletes an [ExerciseSessionRecord] and underlying data.
     */
    suspend fun deleteExerciseSession(uid: String) {
        val exerciseSession = healthConnectClient.readRecord(ExerciseSessionRecord::class, uid)
        healthConnectClient.deleteRecords(
            ExerciseSessionRecord::class,
            recordIdsList = listOf(uid),
            clientRecordIdsList = emptyList()
        )
        val timeRangeFilter = TimeRangeFilter.between(
            exerciseSession.record.startTime,
            exerciseSession.record.endTime
        )
        val rawDataTypes: Set<KClass<out Record>> = setOf(
            HeartRateRecord::class,
            SpeedRecord::class,
            DistanceRecord::class,
            StepsRecord::class,
            TotalCaloriesBurnedRecord::class
        )
        rawDataTypes.forEach { rawType ->
            healthConnectClient.deleteRecords(rawType, timeRangeFilter)
        }
    }

    /**
     * Reads aggregated data and raw data for selected data types, for a given [ExerciseSessionRecord].
     */
    suspend fun readAssociatedSessionData(
        uid: String
    ): ExerciseSessionData {
        val exerciseSession = healthConnectClient.readRecord(ExerciseSessionRecord::class, uid)
        // Use the start time and end time from the session, for reading raw and aggregate data.
        val timeRangeFilter = TimeRangeFilter.between(
            startTime = exerciseSession.record.startTime,
            endTime = exerciseSession.record.endTime
        )
        val aggregateDataTypes = setOf(
            ExerciseSessionRecord.EXERCISE_DURATION_TOTAL,
            StepsRecord.COUNT_TOTAL,
            DistanceRecord.DISTANCE_TOTAL,
            TotalCaloriesBurnedRecord.ENERGY_TOTAL,
            HeartRateRecord.BPM_AVG,
            HeartRateRecord.BPM_MAX,
            HeartRateRecord.BPM_MIN,
        )
        // Limit the data read to just the application that wrote the session. This may or may not
        // be desirable depending on the use case: In some cases, it may be useful to combine with
        // data written by other apps.
        val dataOriginFilter = setOf(exerciseSession.record.metadata.dataOrigin)
        val aggregateRequest = AggregateRequest(
            metrics = aggregateDataTypes,
            timeRangeFilter = timeRangeFilter,
            dataOriginFilter = dataOriginFilter)
        val aggregateData = healthConnectClient.aggregate(aggregateRequest)


        Log.d("HealthSessionManager", "aggregateData: $aggregateData")
        Log.d("HealthSessionManager", "exerciseSession: ${exerciseSession.record.title}")
        return ExerciseSessionData(
            uid = uid,
            totalActiveTime = aggregateData[ExerciseSessionRecord.EXERCISE_DURATION_TOTAL],
            totalSteps = aggregateData[StepsRecord.COUNT_TOTAL],
            totalDistance = aggregateData[DistanceRecord.DISTANCE_TOTAL],
            totalEnergyBurned = aggregateData[TotalCaloriesBurnedRecord.ENERGY_TOTAL],
            minHeartRate = aggregateData[HeartRateRecord.BPM_MIN],
            maxHeartRate = aggregateData[HeartRateRecord.BPM_MAX],
            avgHeartRate = aggregateData[HeartRateRecord.BPM_AVG],
        )
    }

    /**
     * Deletes all existing sleep data.
     */
    suspend fun deleteAllSleepData() {
        val now = Instant.now()
        healthConnectClient.deleteRecords(SleepSessionRecord::class, TimeRangeFilter.before(now))
    }


    suspend fun <T : Record> deleteRecordsOfType(
        type: KClass<T>,
        sessionRange: TimeRangeFilter
    ) {
        // build a properly‐typed ReadRecordsRequest<T>
        val request = ReadRecordsRequest(type, sessionRange)
        // now `records` comes back as List<T> (e.g. List<StepsRecord>)
        val existing: List<T> = healthConnectClient.readRecords(request).records

        Log.d(TAG, "Found ${existing.size} ${type.simpleName} to delete")

        if (existing.isNotEmpty()) {
            val recordIds     = existing.map { it.metadata.id }
            val clientIds     = existing.mapNotNull { it.metadata.clientRecordId }

            Log.d(TAG, "Deleting ${type.simpleName}: recordIds=$recordIds, clientIds=$clientIds")
            healthConnectClient.deleteRecords(
                recordType          = type,
                recordIdsList       = recordIds,
                clientRecordIdsList = clientIds
            )
            Log.d(TAG, "✅ Deleted all ${type.simpleName}")
        }
    }


    suspend fun deleteAllSessionData() {
        val now          = Instant.now()
        val sessionRange = TimeRangeFilter.before(now)

        deleteRecordsOfType(ExerciseSessionRecord::class, sessionRange)
        deleteRecordsOfType(StepsRecord::class,          sessionRange)
        deleteRecordsOfType(DistanceRecord::class,       sessionRange)
        deleteRecordsOfType(TotalCaloriesBurnedRecord::class, sessionRange)
        deleteRecordsOfType(HeartRateRecord::class,      sessionRange)

        deleteAllHealthData()
    }


    /**
     * Deletes ALL existing session data.
     */
    suspend fun deleteAllSessionDataType() {
        val now = Instant.now()
        val sessionRange = TimeRangeFilter.before(now)

        // The record types you want to purge:
        val typesToDelete = listOf(
            ExerciseSessionRecord::class,
            StepsRecord::class,
            DistanceRecord::class,
            TotalCaloriesBurnedRecord::class,
            HeartRateRecord::class,
            //SpeedRecord::class,
            //ExerciseRoute::class,
        )

        typesToDelete.forEach { recordType ->
            try {
                // 1️⃣ Read existing records in the time range
                Log.d(TAG, "⏳ Reading ${recordType.simpleName} before $now…")
                val readRequest = ReadRecordsRequest(
                    recordType = recordType,
                    timeRangeFilter = sessionRange,
                    ascendingOrder = false
                )
                val existing = healthConnectClient
                    .readRecords(ReadRecordsRequest(recordType, sessionRange))
                    .records

                Log.d(TAG, "🔎 Found ${existing.size} ${recordType.simpleName} records to delete")

                if (existing.isNotEmpty()) {
                    // 2️⃣ Collect both system IDs and client IDs
                    // force‐cast each item into the public class
                    val recordIds = existing.map { record ->
                        val typed = recordType.java.cast(record)   // -> T
                        (recordType.java.cast(record) as Record).metadata.id
                    }

                    val clientIds = existing.mapNotNull { record ->
                        val typed = recordType.java.cast(record)   // -> T
                        (recordType.java.cast(record) as Record).metadata.clientRecordId
                    }


                    // 3️⃣ Delete by IDs
                    Log.d(TAG, "🗑 Deleting ${recordType.simpleName} (records=${recordIds.size}, clientIds=${clientIds.size})…")
                    healthConnectClient.deleteRecords(
                        recordType           = recordType,
                        recordIdsList        = recordIds,
                        clientRecordIdsList  = clientIds
                    )
                    Log.d(TAG, "✅ Deleted ${recordType.simpleName} records successfully")
                } else {
                    Log.d(TAG, "⚪ No ${recordType.simpleName} records to delete")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error deleting ${recordType.simpleName}", e)
            }
        }

        Log.d(TAG, "🏁 deleteAllSessionData() completed")
    }

    /**
     * Deletes *all* records of the common Health Connect types up until now.
     * Any records you have written (sessions, steps, distance, calories, heart-rate, etc.) will be removed.
     */
    @SuppressLint("RestrictedApi")
    suspend fun deleteAllHealthData() {
        val now = Instant.now()
        val filter = TimeRangeFilter.before(now)

        // List every Record type you want to purge
        val recordTypes: List<KClass<out Record>> = listOf(
            ExerciseSessionRecord::class,
            StepsRecord::class,
            DistanceRecord::class,
            TotalCaloriesBurnedRecord::class,
            HeartRateRecord::class,
            // SleepSessionRecord::class,
            WeightRecord::class
            // …add any other types your app uses…
        )

        recordTypes.forEach { type ->
            try {
                Log.d(TAG, "🗑 Deleting all ${type.simpleName} records before $now…")
                // This call deletes *all* records matching the filter for that type
                healthConnectClient.deleteRecords(type, filter)
                Log.d(TAG, "✅ Deleted all ${type.simpleName} records")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to delete ${type.simpleName}", e)
            }
        }

        Log.d(TAG, "🏁 deleteAllHealthData() complete")
    }


    /**
     * Reads and logs *all* records of the common Health Connect types up until now.
     */
    @SuppressLint("RestrictedApi")
    suspend fun logAllHealthData() {
        val now = Instant.now()
        val filter = TimeRangeFilter.before(now)

        // Add any other types your app uses:
        val recordTypes: List<KClass<out Record>> = listOf(
            ExerciseSessionRecord::class,
            StepsRecord::class,
            DistanceRecord::class,
            TotalCaloriesBurnedRecord::class,
            HeartRateRecord::class,
            //SleepSessionRecord::class,
            WeightRecord::class
        )

        recordTypes.forEach { type ->
            try {
                Log.d(TAG, "📖 Reading all ${type.simpleName} records before $now…")
                val response = healthConnectClient.readRecords(
                    ReadRecordsRequest(
                        recordType      = type,
                        timeRangeFilter = filter,
                        ascendingOrder  = true
                    )
                )
                Log.d(TAG, "🗂 ${type.simpleName}: found ${response.records.size} record(s)")
                response.records.forEach { record ->
                    Log.d(TAG, record.toString())
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error reading ${type.simpleName}", e)
            }
        }

        Log.d(TAG, "🏁 logAllHealthData() complete")
    }




    /**
     * Deletes all existing sleep data.
     */
    suspend fun deleteExerciseSessionData() {
        val now = Instant.now()
        healthConnectClient.deleteRecords(ExerciseSessionRecord::class, TimeRangeFilter.before(now))
    }

    /**
     * Generates a week's worth of sleep data using a [SleepSessionRecord] to describe the overall
     * period of sleep, with multiple [SleepSessionRecord.Stage] periods which cover the entire
     * [SleepSessionRecord]. For the purposes of this sample, the sleep stage data is generated randomly.
     */
    suspend fun generateSleepData() {
        val records = mutableListOf<Record>()
        // Make yesterday the last day of the sleep data
        val lastDay = ZonedDateTime.now().minusDays(1).truncatedTo(ChronoUnit.DAYS)
        val notes = arrayOf("good", "bad", "ok", "good", "bad", "ok", "good")//NOTE: context.resources.getStringArray(R.array.sleep_notes_array)
        // Create 7 days-worth of sleep data
        for (i in 0..7) {
            val wakeUp = lastDay.minusDays(i.toLong())
                .withHour(Random.nextInt(7, 10))
                .withMinute(Random.nextInt(0, 60))
            val bedtime = wakeUp.minusDays(1)
                .withHour(Random.nextInt(19, 22))
                .withMinute(Random.nextInt(0, 60))
            val sleepSession = SleepSessionRecord(
                notes = notes[Random.nextInt(0, notes.size)],
                startTime = bedtime.toInstant(),
                startZoneOffset = bedtime.offset,
                endTime = wakeUp.toInstant(),
                endZoneOffset = wakeUp.offset,
                stages = generateSleepStages(bedtime, wakeUp),
                metadata = Metadata.autoRecorded(
                        device = Device(type = Device.TYPE_WATCH)
                )
            )
            records.add(sleepSession)
        }
        healthConnectClient.insertRecords(records)
    }

    /**
     * Reads sleep sessions for the previous seven days (from yesterday) to show a week's worth of
     * sleep data.
     *
     * In addition to reading [SleepSessionRecord]s, for each session, the duration is calculated to
     * demonstrate aggregation, and the underlying [SleepSessionRecord.Stage] data is also read.
     */
    suspend fun readSleepSessions(): List<SleepSessionData> {
        val lastDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            .minusDays(1)
            .withHour(12)
        val firstDay = lastDay
            .minusDays(7)

        val sessions = mutableListOf<SleepSessionData>()
        val sleepSessionRequest = ReadRecordsRequest(
            recordType = SleepSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(firstDay.toInstant(), lastDay.toInstant()),
            ascendingOrder = false
        )
        val sleepSessions = healthConnectClient.readRecords(sleepSessionRequest)
        sleepSessions.records.forEach { session ->
            val sessionTimeFilter = TimeRangeFilter.between(session.startTime, session.endTime)
            val durationAggregateRequest = AggregateRequest(
                metrics = setOf(SleepSessionRecord.SLEEP_DURATION_TOTAL),
                timeRangeFilter = sessionTimeFilter
            )
            val aggregateResponse = healthConnectClient.aggregate(durationAggregateRequest)
            sessions.add(
                SleepSessionData(
                    uid = session.metadata.id,
                    title = session.title,
                    notes = session.notes,
                    startTime = session.startTime,
                    startZoneOffset = session.startZoneOffset,
                    endTime = session.endTime,
                    endZoneOffset = session.endZoneOffset,
                    duration = aggregateResponse[SleepSessionRecord.SLEEP_DURATION_TOTAL],
                    stages = session.stages
                )
            )
        }
        return sessions
    }

    /**
     * Writes [WeightRecord] to Health Connect.
     */
    suspend fun writeWeightInput(weight: WeightRecord) {
        val records = listOf(weight)
        healthConnectClient.insertRecords(records)
    }

    /**
     * Reads in existing [WeightRecord]s.
     */
    suspend fun readWeightInputs(start: Instant, end: Instant): List<WeightRecord> {
        val request = ReadRecordsRequest(
            recordType = WeightRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records
    }


    /**
     *
     */
    suspend fun readSessionInputs(): List<ExerciseSessionRecord> {
        // “All‐time” filter: everything up until now
        val allTime = TimeRangeFilter.before(Instant.now())

        Log.d("DebugSync", "Reading all sessions before ${Instant.now()}")

        val recs = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType      = ExerciseSessionRecord::class,
                timeRangeFilter = allTime
            )
        ).records

        Log.d("DebugSync", "Found ${recs.size} session(s): ${recs.map { it.metadata.id }}")
        return recs
    }

    /**
     * Returns the weekly average of [WeightRecord]s.
     */
    suspend fun computeWeeklyAverage(start: Instant, end: Instant): Mass? {
        val request = AggregateRequest(
            metrics = setOf(WeightRecord.WEIGHT_AVG),
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.aggregate(request)
        return response[WeightRecord.WEIGHT_AVG]
    }

    /**
     * Deletes a [WeightRecord]s.
     */
    suspend fun deleteWeightInput(uid: String) {
        healthConnectClient.deleteRecords(
            WeightRecord::class,
            recordIdsList = listOf(uid),
            clientRecordIdsList = emptyList()
        )
    }

    /**
     * Obtains a changes token for the specified record types.
     */
    suspend fun getChangesToken(dataTypes: Set<KClass<out Record>>): String {
        val request = ChangesTokenRequest(dataTypes)
        return healthConnectClient.getChangesToken(request)
    }

    /**
     * Creates a [Flow] of change messages, using a changes token as a start point. The flow will
     * terminate when no more changes are available, and the final message will contain the next
     * changes token to use.
     */
    suspend fun getChanges(token: String): Flow<ChangesMessage> = flow {
        var nextChangesToken = token
        do {
            val response = healthConnectClient.getChanges(nextChangesToken)
            if (response.changesTokenExpired) {
                // As described here: https://developer.android.com/guide/health-and-fitness/health-connect/data-and-data-types/differential-changes-api
                // tokens are only valid for 30 days. It is important to check whether the token has
                // expired. As well as ensuring there is a fallback to using the token (for example
                // importing data since a certain date), more importantly, the app should ensure
                // that the changes API is used sufficiently regularly that tokens do not expire.
                throw IOException("Changes token has expired")
            }
            emit(ChangesMessage.ChangeList(response.changes))
            nextChangesToken = response.nextChangesToken
        } while (response.hasMore)
        emit(ChangesMessage.NoMoreChanges(nextChangesToken))
    }

    /** Creates a random sleep stage that spans the specified [start] to [end] time. */
    private fun generateSleepStages(
        start: ZonedDateTime,
        end: ZonedDateTime
    ): List<SleepSessionRecord.Stage> {
        val sleepStages = mutableListOf<SleepSessionRecord.Stage>()
        var stageStart = start
        while (stageStart < end) {
            val stageEnd = stageStart.plusMinutes(Random.nextLong(30, 120))
            val checkedEnd = if (stageEnd > end) end else stageEnd
            sleepStages.add(
                SleepSessionRecord.Stage(
                    stage = randomSleepStage(),
                    startTime = stageStart.toInstant(),
                    endTime = checkedEnd.toInstant()))
            stageStart = checkedEnd
        }
        return sleepStages
    }

    /**
     * Convenience function to fetch a time-based record and return series data based on the record.
     * Record types compatible with this function must be declared in the
     * [com.example.healthconnectsample.presentation.screen.recordlist.RecordType] enum.
     */
    suspend fun fetchSeriesRecordsFromUid(recordType: KClass<out Record>, uid: String, seriesRecordsType: KClass<out Record>): List<Record> {
        val recordResponse = healthConnectClient.readRecord(recordType, uid)
        // Use the start time and end time from the session, for reading raw and aggregate data.
        val timeRangeFilter =
            when (recordResponse.record) {
                // Change to use series record instead
                is ExerciseSessionRecord -> {
                    val record = recordResponse.record as ExerciseSessionRecord
                    TimeRangeFilter.between(startTime = record.startTime, endTime = record.endTime)
                }
                is SleepSessionRecord -> {
                    val record = recordResponse.record as SleepSessionRecord
                    TimeRangeFilter.between(startTime = record.startTime, endTime = record.endTime)
                }
                else -> {
                    throw InvalidObjectException("Record with unregistered data type returned")
                }
            }

        // Limit the data read to just the application that wrote the session. This may or may not
        // be desirable depending on the use case: In some cases, it may be useful to combine with
        // data written by other apps.
        val dataOriginFilter = setOf(recordResponse.record.metadata.dataOrigin)
        val request =
            ReadRecordsRequest(
                recordType = seriesRecordsType,
                dataOriginFilter = dataOriginFilter,
                timeRangeFilter = timeRangeFilter)
        return healthConnectClient.readRecords(request).records
    }

    /**
     * Reads the most recent HeartRateRecord sample, if any.
     */
    suspend fun readLatestHeartRateSample(): HeartRateRecord.Sample? {
        // 1) Read the latest record (we only need the most recent record)
        val request = ReadRecordsRequest(
            recordType       = HeartRateRecord::class,
            timeRangeFilter  = TimeRangeFilter.before(Instant.now()),
            ascendingOrder   = false,     // get newest first
            pageSize         = 1          // only need one record
        )
        val response = healthConnectClient.readRecords(request)
        val record = response.records.firstOrNull() ?: return null

        // 2) Extract the last Sample from that record’s series
        return record.samples.maxByOrNull { it.time }
    }

    private fun buildHeartRateSeries(
        sessionStartTime: ZonedDateTime,
        sessionEndTime: ZonedDateTime
    ): HeartRateRecord {
        val samples = mutableListOf<HeartRateRecord.Sample>()
        var time = sessionStartTime
        while (time.isBefore(sessionEndTime)) {
            samples.add(
                HeartRateRecord.Sample(
                    time = time.toInstant(), beatsPerMinute = (80 + Random.nextInt(80)).toLong()))
            time = time.plusSeconds(30)
        }
        return HeartRateRecord(
            startTime = sessionStartTime.toInstant(),
            startZoneOffset = sessionStartTime.offset,
            endTime = sessionEndTime.toInstant(),
            endZoneOffset = sessionEndTime.offset,
            samples = samples,
            metadata = Metadata.autoRecorded(
                device = Device(type = Device.TYPE_WATCH)
            )
        )
    }

    fun isFeatureAvailable(feature: Int): Boolean{
        return healthConnectClient
            .features
            .getFeatureStatus(feature) == HealthConnectFeatures.FEATURE_STATUS_AVAILABLE
    }

    // Represents the two types of messages that can be sent in a Changes flow.
    sealed class ChangesMessage {
        data class NoMoreChanges(val nextChangesToken: String) : ChangesMessage()

        data class ChangeList(val changes: List<Change>) : ChangesMessage()
    }
}
