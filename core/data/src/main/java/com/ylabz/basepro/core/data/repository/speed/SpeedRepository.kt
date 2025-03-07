package com.ylabz.basepro.core.data.repository.speed

import kotlinx.coroutines.flow.Flow

interface SpeedRepository {
    /**
     * Emits the current speed (in km/h) as a Float.
     * The repository is responsible for retrieving
     * the device's GPS location, checking permissions, etc.
     */
    val speedFlow: Flow<Float>
}

