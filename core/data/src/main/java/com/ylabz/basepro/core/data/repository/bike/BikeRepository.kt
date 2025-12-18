package com.ylabz.basepro.core.data.repository.bike

import com.ylabz.basepro.core.model.bike.SuspensionState
import kotlinx.coroutines.flow.StateFlow

interface BikeRepository {
    // Data Streams
    val currentGear: StateFlow<Int>
    val suspensionState: StateFlow<SuspensionState>
    val isConnected: StateFlow<Boolean>

    // Actions
    suspend fun gearUp()
    suspend fun gearDown()
    suspend fun toggleSuspension()
    suspend fun setGear(gear: Int)
}