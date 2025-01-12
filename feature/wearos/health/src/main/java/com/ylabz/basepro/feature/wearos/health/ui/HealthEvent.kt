package com.ylabz.basepro.feature.wearos.health.ui

sealed interface HealthEvent {
    object RequestPermissions : HealthEvent
    object LoadHealthData : HealthEvent
    object DeleteAll : HealthEvent
    object Retry : HealthEvent
    object Insert : HealthEvent
}
