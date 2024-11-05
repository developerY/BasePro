package com.ylabz.basepro.core.data.service

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregationResult
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Mass
import com.ylabz.basepro.core.data.dto.health.SleepSessionData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Manager for accessing and aggregating health data from Health Connect.
 */
class HealthConnectManager @Inject constructor(
    private val context: Context
) {
    private val _isAvailable = MutableStateFlow(false)
    val isAvailable: StateFlow<Boolean> get() = _isAvailable

    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    init {
        //_isAvailable.value = HealthConnectClient.isAvailable(context)
    }

    /**
     * Checks if all the requested Health Connect permissions are granted.
     */
    /*suspend fun hasAllPermissions(permissions: Set<HealthPermission>): Boolean {
        return permissions == healthConnectClient.permissionController.getGrantedPermissions(
            permissions
        )
    }*/

    /**
     * Reads step count records within a specified time range.
     */
    suspend fun readStepsByTimeRange(
        startTime: Instant,
        endTime: Instant
    ): List<StepsRecord> {
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response.records
    }

    /**
     * Aggregates step records over a specified time range.
     */
    suspend fun aggregateSteps(
        startTime: Instant,
        endTime: Instant
    ): Long {
        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response[StepsRecord.COUNT_TOTAL] ?: 0
    }

    /**
     * Aggregates heart rate data (min and max) over a specified time range.
     */
    suspend fun aggregateHeartRate(
        startTime: Instant,
        endTime: Instant
    ): AggregationResult {
        return healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(HeartRateRecord.BPM_MAX, HeartRateRecord.BPM_MIN),
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
    }

    /**
     * Reads exercise sessions in a specified time range.
     */
    suspend fun readExerciseSessions(
        startTime: Instant,
        endTime: Instant
    ): List<ExerciseSessionRecord> {
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = ExerciseSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response.records
    }

    /**
     * Reads blood glucose records within a specified time range.
     */
    suspend fun readBloodSugarByTimeRange(
        startTime: Instant,
        endTime: Instant
    ): List<BloodGlucoseRecord> {
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                BloodGlucoseRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response.records
    }

    /**
     * Aggregates blood pressure data (average systolic and diastolic) over a specified time range.
     */
    suspend fun aggregateBloodPressure(
        startTime: Instant,
        endTime: Instant
    ): AggregationResult {
        return healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(BloodPressureRecord.DIASTOLIC_AVG, BloodPressureRecord.SYSTOLIC_AVG),
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
    }

    /**
     * Computes the average weight over a specified time range.
     */
    suspend fun getAverageWeight(
        startTime: Instant,
        endTime: Instant
    ): Mass? {
        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(WeightRecord.WEIGHT_AVG),
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response[WeightRecord.WEIGHT_AVG]
    }

    /**
     * Retrieves all weight records within a specified time range.
     */
    suspend fun getWeightRecords(
        startTime: Instant,
        endTime: Instant
    ): List<WeightRecord> {
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = WeightRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response.records
    }

    /**
     * Reads sleep sessions for the previous seven days (from yesterday) to show a week's worth of sleep data.
     */
    suspend fun readSleepSessions(): List<SleepSessionData> {
        val lastDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            .minusDays(1)
            .withHour(12)
        val firstDay = lastDay.minusDays(7)

        val sessions = mutableListOf<SleepSessionData>()
        val sleepSessionRequest = ReadRecordsRequest(
            recordType = SleepSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(firstDay.toInstant(), lastDay.toInstant()),
            ascendingOrder = false
        )
        val sleepSessions = healthConnectClient.readRecords(sleepSessionRequest)

        for (session in sleepSessions.records) {
            val sessionTimeFilter = TimeRangeFilter.between(session.startTime, session.endTime)
            val durationAggregateRequest = AggregateRequest(
                metrics = setOf(SleepSessionRecord.SLEEP_DURATION_TOTAL),
                timeRangeFilter = sessionTimeFilter
            )
            val aggregateResponse = healthConnectClient.aggregate(durationAggregateRequest)
            val stagesRequest = ReadRecordsRequest(
                recordType = SleepStageRecord::class,
                timeRangeFilter = sessionTimeFilter
            )
            val stagesResponse = healthConnectClient.readRecords(stagesRequest)
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
                    stages = stagesResponse.records
                )
            )
        }
        return sessions
    }
}
