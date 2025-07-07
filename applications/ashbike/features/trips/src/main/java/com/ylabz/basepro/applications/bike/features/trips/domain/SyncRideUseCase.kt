package com.ylabz.basepro.applications.bike.features.trips.domain

import android.annotation.SuppressLint
import android.health.connect.datatypes.ExerciseSegmentType
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSegment
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.metadata.Device
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.core.model.bike.BikeRide
import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

// 1) A pure sync use-case in your domain layer:
@Singleton
class SyncRideUseCase @Inject constructor(
) {
    @SuppressLint("RestrictedApi")
    operator fun invoke(ride: BikeRide): List<Record> {
        val start  = Instant.ofEpochMilli(ride.startTime)
        val end    = Instant.ofEpochMilli(ride.endTime)
        val offset = ZoneOffset.systemDefault().rules.getOffset(start)

        // 1) Pick a recording method. If it’s an “active” session, use activelyRecorded:
        /**
         * • Use the manual-entry metadata factory instead of activelyRecorded, because only that
         * overload guarantees clientRecordId is persisted & returned:
         */
        val rideMetaData = Metadata.manualEntry(
            device             = Device(type = Device.TYPE_PHONE),
            clientRecordId     = ride.rideId,    // your domain UUID
            /* clientRecordVersion defaults to 0, no need to supply */
            // clientRecordVersion= 0               // bump this on each update
        )

        // 1) Session with a single segment
        //Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        val session = ExerciseSessionRecord(
            metadata        = rideMetaData,
            startTime       = start,
            startZoneOffset = offset,
            endTime         = end,
            endZoneOffset   = offset,
            exerciseType    = when (ride.rideType) {
                "Bike" -> ExerciseSessionRecord.EXERCISE_TYPE_BIKING
                else   -> ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT
            },
            title           = ride.notes ?: "${ride.rideType} Session",
            notes           = ride.notes,
            segments        = listOf(
                ExerciseSegment(
                    startTime = start,
                    endTime = end,
                    segmentType = ExerciseSegmentType.EXERCISE_SEGMENT_TYPE_BIKING
                )
            )
        )

        // 2) Distance
        val distanceRecord = DistanceRecord(
            metadata        = rideMetaData,
            startTime       = start,
            startZoneOffset = offset,
            endTime         = end,
            endZoneOffset   = offset,
            distance        = Length.meters(ride.totalDistance.toDouble())
        )

        // 3) Calories
        val caloriesRecord = TotalCaloriesBurnedRecord(
            metadata        = rideMetaData,
            startTime       = start,
            startZoneOffset = offset,
            endTime         = end,
            endZoneOffset   = offset,
            energy          = Energy.calories(ride.caloriesBurned.toDouble())
        )

        // 4) Optional heart‐rate series
        val heartRateRecord: HeartRateRecord? = ride.avgHeartRate?.let { avgHr ->
            val samples = listOfNotNull(
                HeartRateRecord.Sample(time = start, beatsPerMinute = avgHr.toLong()),
                ride.maxHeartRate?.let { maxHr ->
                    HeartRateRecord.Sample(time = end, beatsPerMinute = maxHr.toLong())
                }
            )
            HeartRateRecord(
                metadata        = rideMetaData,
                startTime       = start,
                startZoneOffset = offset,
                endTime         = end,
                endZoneOffset   = offset,
                samples         = samples
            )
        }

        // 5) Aggregate into one list
        return buildList<Record> {
            add(session)
            add(distanceRecord)
            add(caloriesRecord)
            heartRateRecord?.let { add(it) }
        }
    }
}