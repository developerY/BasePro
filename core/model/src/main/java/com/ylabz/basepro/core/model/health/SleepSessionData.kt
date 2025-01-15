package com.ylabz.basepro.core.model.health

import androidx.compose.ui.graphics.Color
import androidx.health.connect.client.records.SleepSessionRecord
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset


/**
 * Represents sleep data, raw, aggregated and sleep stages, for a given [SleepSessionRecord].
 */
data class SleepSessionData(
    val uid: String,
    val title: String?,
    val notes: String?,
    val startTime: Instant,
    val startZoneOffset: ZoneOffset?,
    val endTime: Instant,
    val endZoneOffset: ZoneOffset?,
    val duration: Duration?,
    val stages: List<SleepSessionRecord.Stage> = listOf()
)


data class SleepSegment(
    val startHour: Float,        // e.g., 22.5 for 10:30 PM
    val endHour: Float,          // e.g., 7.0 for 7:00 AM
    val percentage: Float,       // e.g., 16% for REM
    val color: Color,
    val label: String            // e.g., "N2 Sleep: 2"
)
