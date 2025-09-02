package com.ylabz.basepro.feature.heatlh.ui

import androidx.health.connect.client.records.Record

sealed interface HealthEvent {
    object RequestPermissions : HealthEvent
    object LoadHealthData : HealthEvent
    object DeleteAll : HealthEvent
    object Retry : HealthEvent
    object ReadAll : HealthEvent

    /** Insert a prepared list of Health Connect Record objects */
    data class Insert(
        val rideId: String, // Added rideId
        val records: List<Record>
    ) : HealthEvent
}