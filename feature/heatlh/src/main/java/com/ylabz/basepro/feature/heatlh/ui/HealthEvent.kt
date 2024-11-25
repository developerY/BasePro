package com.ylabz.basepro.feature.heatlh.ui

sealed interface HealthEvent {
    //object RequestPermissions : HealthEvent
    object LoadHealthData : HealthEvent
    object Retry : HealthEvent
}
