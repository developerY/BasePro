package com.ylabz.basepro.feature.heatlh.ui

import androidx.health.connect.client.records.ExerciseSessionRecord

sealed interface HealthEvent {
    object RequestPermissions : HealthEvent
    object LoadHealthData     : HealthEvent
    object DeleteAll          : HealthEvent
    object Retry              : HealthEvent
    /** Insert (sync) exactly this session into Health Connect */
    data class Insert(val session: ExerciseSessionRecord) : HealthEvent
}

