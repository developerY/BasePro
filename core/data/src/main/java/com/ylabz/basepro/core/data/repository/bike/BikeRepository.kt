package com.ylabz.basepro.core.data.repository.bike

import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.SuspensionState
import kotlinx.coroutines.flow.StateFlow

interface BikeRepository {
    // --- READ-ONLY STREAMS (The "What") ---
    // Observed by Phone UI and Glass UI
    val rideInfo: StateFlow<BikeRideInfo>
    val currentGear: StateFlow<Int>
    val suspensionState: StateFlow<SuspensionState>

    // Connection Status
    val isConnected: StateFlow<Boolean>      // Bike Bluetooth Connection
    val isGlassConnected: StateFlow<Boolean> // Smart Glasses Connection

    // --- ACTIONS (The "How") ---
    // Called by ViewModels (Phone/Glass) to change physical state
    suspend fun gearUp()
    suspend fun gearDown()
    suspend fun setGear(gear: Int)
    suspend fun toggleSuspension()

    // --- SYSTEM UPDATES ---
    // Called by Services (BikeForegroundService / GlassService) to update raw data
    suspend fun updateRideInfo(info: BikeRideInfo)
    suspend fun updateConnectionState(isConnected: Boolean)
    suspend fun updateGlassConnectionState(isConnected: Boolean)

    //only for debug
    suspend fun toggleSimulatedConnection() // <--- NEW
}