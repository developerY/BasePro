package com.ylabz.basepro.core.data.repository.health

import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.Record
import java.time.Instant

/**
 * A port for the Health Connect functionality.
 * Implementations handle inserting, querying and deleting Health Connect data.
 */
interface HealthConnectRepository {

    /**
     * Inserts one or more Health Connect records in a single batch.
     * @param records the list of Records (e.g. ExerciseSessionRecord, DistanceRecord, etc.)
     */
    suspend fun insert(records: List<Record>)

    /**
     * Reads all [ExerciseSessionRecord]s whose start time is on or after [startTime],
     * and whose end time is on or before [endTime].
     *
     * @param startTime the inclusive lower bound for session start
     * @param endTime the inclusive upper bound for session end
     * @return the list of matching ExerciseSessionRecord
     */
    suspend fun readExerciseSessions(startTime: Instant, endTime: Instant): List<ExerciseSessionRecord>

    /**
     * Deletes all ExerciseSessionRecord (and their associated data) ending before [before].
     *
     * @param before deletes sessions whose endTime < before
     */
    suspend fun deleteAllSessions(before: Instant)
}
