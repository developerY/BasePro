package com.ylabz.basepro.core.model.shotime

import androidx.health.connect.client.records.SleepSessionRecord
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

data class ShotimeSessionData(
    val shot: String,
    val id: Int,
    val isEnabled: Boolean
    /*val startTime: Instant,
    val startZoneOffset: ZoneOffset?,
    val endTime: Instant,
    val endZoneOffset: ZoneOffset?,
    val duration: Duration?,
    val stages: List<SleepSessionRecord.Stage> = listOf()*/
)