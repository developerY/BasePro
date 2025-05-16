package com.ylabz.basepro.applications.bike.features.trips.data

import android.annotation.SuppressLint
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
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
import androidx.health.connect.client.units.Energy.Companion.calories
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Mass
import java.time.Instant
import java.time.ZoneOffset

// 1) A pure sync use-case in your domain layer:
class SyncRideUseCase {
    @SuppressLint("RestrictedApi")
    operator fun invoke(ride: BikeRideEntity): List<Record> {
        val start = Instant.ofEpochMilli(ride.startTime)
        val end   = Instant.ofEpochMilli(ride.endTime)
        val offset= ZoneOffset.systemDefault().rules.getOffset(start)

        val session = androidx.health.connect.client.records.ExerciseSessionRecord(
            metadata = Metadata.autoRecorded(
                device = Device(type = Device.TYPE_PHONE)
            ),
            startTime = start,
            startZoneOffset = offset,
            endTime = end,
            endZoneOffset = offset,
            exerciseType = when (ride.rideType) {
                "Bike" -> ExerciseSessionRecord.EXERCISE_TYPE_BIKING
                else   -> ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT
            },
            title = ride.notes ?: "${ride.rideType} Session",
            notes = ride.notes
        )

        val distanceRecord = androidx.health.connect.client.records.DistanceRecord(
            metadata = Metadata.manualEntry(),
            startTime = start,
            startZoneOffset = offset,
            endTime = end,
            endZoneOffset = offset,
            distance = Length.meters(ride.totalDistance.toDouble())
        )

        val caloriesRecord = androidx.health.connect.client.records.TotalCaloriesBurnedRecord(
            metadata = Metadata.manualEntry(),
            startTime = start,
            startZoneOffset = offset,
            endTime = end,
            endZoneOffset = offset,
            energy = Energy.calories(ride.caloriesBurned.toDouble())
        )

        // heart rate
        val heartRateRecord: HeartRateRecord? = ride.avgHeartRate?.let { avgHr ->
            val samples = mutableListOf(
                HeartRateRecord.Sample(time = start, beatsPerMinute = avgHr.toLong()),
                HeartRateRecord.Sample(time = end,   beatsPerMinute = (ride.maxHeartRate ?: avgHr).toLong())
            )
            HeartRateRecord(
                metadata        = Metadata.manualEntry(),
                startTime       = start,
                startZoneOffset = offset,
                endTime         = end,
                endZoneOffset   = offset,
                samples         = samples
            )
        }

        val records = listOf(session, distanceRecord, caloriesRecord)
            .let { base -> if (heartRateRecord != null) base + heartRateRecord else base }

        return records
    }
}
