package com.ylabz.basepro.core.data.repository.bike

import com.ylabz.basepro.core.model.bike.SuspensionState
import kotlinx.coroutines.flow.StateFlow

interface BikeRepository {
    // --- READ ONLY STREAMS ---
    val isConnected: StateFlow<Boolean>      // Bike Connection
    val isGlassConnected: StateFlow<Boolean> // <--- NEW: Glass Connection

    val currentGear: StateFlow<Int>
    val suspensionState: StateFlow<SuspensionState>

    // --- ACTIONS ---
    suspend fun gearUp()
    suspend fun gearDown()
    suspend fun toggleSuspension()
    suspend fun setGear(gear: Int)

    // Connection Management
    suspend fun connectToBike(address: String)
    suspend fun disconnectBike()

    // Glass Management
    // Called by your GlassService when the session starts/ends
    suspend fun updateGlassConnectionState(isConnected: Boolean)
}