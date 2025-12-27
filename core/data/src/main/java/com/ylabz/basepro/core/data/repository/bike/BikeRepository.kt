package com.ylabz.basepro.core.data.repository.bike

import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.SuspensionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BikeRepository {
    // --- READ-ONLY STREAMS (The "What") ---
    // Observed by Phone UI and Glass UI
    val rideInfo: StateFlow<BikeRideInfo>
    val currentGear: StateFlow<Int>
    val suspensionState: StateFlow<SuspensionState>

    // Connection Status
    val isConnected: Flow<Boolean>      // Bike Bluetooth Connection
    // --- GLASS STATES (Updated for 3-State Logic) ---
    val isGlassConnected: StateFlow<Boolean>     // 1. Hardware plugged in?
    val isGlassSessionActive: StateFlow<Boolean> // 2. Projection App running?

    // --- ACTIONS (The "How") ---
    // Called by ViewModels (Phone/Glass) to change physical state
    suspend fun gearUp()
    suspend fun gearDown()
    suspend fun setGear(gear: Int)
    suspend fun toggleSuspension()

    // --- SYSTEM UPDATES ---
    // Called by Services (BikeForegroundService / GlassService) to update raw data
    suspend fun updateRideInfo(info: BikeRideInfo)
    // NFC -2- BLE
    suspend fun updateConnectionState(isConnected: Boolean)
    // Glass Updates
    suspend fun updateGlassConnectionState(isConnected: Boolean)
    suspend fun updateGlassSessionState(isActive: Boolean) // <--- NEW
}