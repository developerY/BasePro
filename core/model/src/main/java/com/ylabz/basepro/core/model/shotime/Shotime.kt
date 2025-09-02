package com.ylabz.basepro.core.model.shotime

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