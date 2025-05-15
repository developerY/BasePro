package com.ylabz.basepro.applications.bike.features.main.usecase.health

import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import com.ylabz.basepro.core.data.service.health.HealthSessionManager
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class HealthUseCase @Inject constructor(
    private val healthManager: HealthSessionManager
) {
    /** One-shot read of all sessions today */
    suspend fun readTodaySessions(): List<ExerciseSessionRecord> {
        val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        return healthManager.readExerciseSessions(
            startOfDay.toInstant(),
            Instant.now()
        )
    }

    /** Stream latest heart-rate, every [intervalMs] */
    fun heartRateFlow(intervalMs: Long = 5_000L): Flow<Int?> = flow {
        while (true) {
            val sample = healthManager.readLatestHeartRateSample()
            emit(sample?.beatsPerMinute?.toInt())
            delay(intervalMs)
        }
    }

    /** Write a completed bike session */
    suspend fun writeBikeSession(
        start: ZonedDateTime,
        end: ZonedDateTime,
        distanceMeters: Double,
        heartRateSamples: List<HeartRateRecord.Sample>
    ) {
        healthManager.writeExerciseSessionMark()
           // start    = start, end      = end,)
    }

    class HealthUseCase @Inject constructor(
        private val healthManager: HealthSessionManager
    ) {
        fun heartRateFlow(pollMs: Long = 5_000L): Flow<Int?> = flow {
            while (currentCoroutineContext().isActive) {
                val sample = healthManager.readLatestHeartRateSample()
                emit(sample?.beatsPerMinute?.toInt())
                delay(pollMs)
            }
        }
    }

}