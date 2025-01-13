package com.ylabz.basepro.feature.wearos.drunkwatch

sealed interface DrukWatchEvent {
    object LoadDrukWatchData : DrukWatchEvent
    object DeleteAll : DrukWatchEvent
    object Insert : DrukWatchEvent
}
