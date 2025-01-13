package com.ylabz.basepro.feature.wearos.sleepwatch

sealed interface SleepWatchEvent {
    object RequestPermissions : SleepWatchEvent
    object LoadSleepWatchData : SleepWatchEvent
    object DeleteAll : SleepWatchEvent
    object Retry : SleepWatchEvent
    object Insert : SleepWatchEvent
}
