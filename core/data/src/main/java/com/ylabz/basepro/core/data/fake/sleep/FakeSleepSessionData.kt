package com.ylabz.basepro.core.data.fake.sleep

import androidx.health.connect.client.records.SleepSessionRecord
import com.ylabz.basepro.core.model.health.SleepSessionData
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

class FakeSleepSessionData {

    fun getFakeSleepData(): List<SleepSessionData> {
        return listOf(
            SleepSessionData(
                uid = UUID.randomUUID().toString(),
                title = "Night Sleep",
                notes = "Slept well after workout",
                startTime = Instant.now().minusSeconds(8 * 60 * 60), // 8 hours ago
                startZoneOffset = ZoneOffset.ofHours(-7), // Example timezone offset (PDT)
                endTime = Instant.now(),
                endZoneOffset = ZoneOffset.ofHours(-7),
                duration = Duration.ofHours(8),
                stages = listOf(
                    SleepSessionRecord.Stage(
                        startTime = Instant.now().minusSeconds(7 * 60 * 60 + 30 * 60),
                        endTime = Instant.now().minusSeconds(6 * 60 * 60),
                        stage = SleepSessionRecord.STAGE_TYPE_DEEP
                    ),
                    SleepSessionRecord.Stage(
                        startTime = Instant.now().minusSeconds(6 * 60 * 60),
                        endTime = Instant.now().minusSeconds(5 * 60 * 60),
                        stage = SleepSessionRecord.STAGE_TYPE_REM
                    ),
                    SleepSessionRecord.Stage(
                        startTime = Instant.now().minusSeconds(5 * 60 * 60),
                        endTime = Instant.now().minusSeconds(4 * 60 * 60),
                        stage = SleepSessionRecord.STAGE_TYPE_LIGHT
                    )
                )
            ),
            SleepSessionData(
                uid = UUID.randomUUID().toString(),
                title = "Afternoon Nap",
                notes = "Quick power nap",
                startTime = Instant.now().minusSeconds(2 * 60 * 60),
                startZoneOffset = ZoneOffset.UTC,
                endTime = Instant.now().minusSeconds(1 * 60 * 60 + 30 * 60),
                endZoneOffset = ZoneOffset.UTC,
                duration = Duration.ofMinutes(90),
                stages = listOf(
                    SleepSessionRecord.Stage(
                        startTime = Instant.now().minusSeconds(2 * 60 * 60),
                        endTime = Instant.now().minusSeconds(1 * 60 * 60 + 30 * 60),
                        stage = SleepSessionRecord.STAGE_TYPE_REM
                    )
                )
            )
        )
    }
}