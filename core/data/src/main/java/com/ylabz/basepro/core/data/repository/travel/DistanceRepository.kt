package com.ylabz.basepro.core.data.repository.travel

import kotlinx.coroutines.flow.Flow

interface DistanceRepository {
    /**
     * Emits the remaining distance (in km) as the user rides.
     */
    val remainingDistanceFlow: Flow<Float>
}
