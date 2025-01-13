package com.ylabz.basepro.feature.wearos.sleepwatch

sealed interface SleepWatchEvent {
    object LoadSleepWatchData : SleepWatchEvent
    object DeleteAll : SleepWatchEvent
    object Insert : SleepWatchEvent
}
