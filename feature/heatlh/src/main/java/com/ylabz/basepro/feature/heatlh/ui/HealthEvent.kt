package com.ylabz.basepro.feature.heatlh.ui

import androidx.health.connect.client.records.Record

sealed interface HealthEvent {
    object RequestPermissions : HealthEvent
    object LoadHealthData     : HealthEvent
    object DeleteAll          : HealthEvent
    object Retry              : HealthEvent
    object ReadAll             : HealthEvent
    
    /** Insert (sync) exactly this session into Health Connect */
    object TestInsert: HealthEvent
    /** Insert a prepared list of Health Connect Record objects */
    data class Insert(val records: List<Record>) : HealthEvent
}


