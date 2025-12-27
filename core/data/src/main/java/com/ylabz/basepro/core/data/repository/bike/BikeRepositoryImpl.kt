package com.ylabz.basepro.core.data.repository.bike

import android.util.Log
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.SuspensionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // CRITICAL: Ensures the state survives across Screens and Services
class BikeRepositoryImpl @Inject constructor() : BikeRepository {

    // --- 1. RIDE INFO (Speed, Distance, GPS) ---
    private val _rideInfo = MutableStateFlow(BikeRideInfo.initial())
    override val rideInfo = _rideInfo.asStateFlow()

    override suspend fun updateRideInfo(info: BikeRideInfo) {
        // This is called ~1 time per second by BikeForegroundService
        Log.d("DEBUG_PATH", "2. REPO: Received speed ${info.currentSpeed}. Emitting...") // <--- ADD THIS
        _rideInfo.emit(info)
    }

    // --- 2. GEARS ---
    private val _currentGear = MutableStateFlow(1)
    override val currentGear = _currentGear.asStateFlow()

    override suspend fun gearUp() {
        if (_currentGear.value < 12) {
            _currentGear.emit(_currentGear.value + 1)
            // TODO: Send bluetooth command: bikeBluetoothService.writeCharacteristic(...)
        }
    }

    override suspend fun gearDown() {
        if (_currentGear.value > 1) {
            _currentGear.emit(_currentGear.value - 1)
        }
    }

    override suspend fun setGear(gear: Int) {
        if (gear in 1..12) {
            _currentGear.emit(gear)
        }
    }

    // --- 3. SUSPENSION ---
    private val _suspensionState = MutableStateFlow(SuspensionState.OPEN)
    override val suspensionState = _suspensionState.asStateFlow()

    override suspend fun toggleSuspension() {
        val nextState = when (_suspensionState.value) {
            SuspensionState.OPEN -> SuspensionState.TRAIL
            SuspensionState.TRAIL -> SuspensionState.LOCK
            SuspensionState.LOCK -> SuspensionState.OPEN
        }
        _suspensionState.emit(nextState)
    }

    // --- 4. CONNECTIONS ---
    private val _isConnected = MutableStateFlow(false)
    override val isConnected = _isConnected.asStateFlow()

    override suspend fun updateConnectionState(isConnected: Boolean) {
        _isConnected.emit(isConnected)
    }


    // --- 5. GLASS STATE (Updated) ---
    // Hardware State (Plugged in?)
    private val _isGlassConnected = MutableStateFlow(false)
    override val isGlassConnected = _isGlassConnected.asStateFlow()

    // Software State (App Running?)
    private val _isGlassSessionActive = MutableStateFlow(false)
    override val isGlassSessionActive = _isGlassSessionActive.asStateFlow()

    override suspend fun updateGlassConnectionState(isConnected: Boolean) {
        Log.d("DEBUG_GLASS", "1. Repo: Hardware Connection Changed -> $isConnected") // <--- LOG
        _isGlassConnected.emit(isConnected)

        // Logic: If hardware is unplugged, the session must be dead.
        if (!isConnected) {
            Log.d("DEBUG_GLASS", "1. Repo: Hardware Disconnect -> Forcing Session FALSE") // <--- LOG
            _isGlassSessionActive.emit(false)
        }
    }

    override suspend fun updateGlassSessionState(isActive: Boolean) {
        Log.d("DEBUG_GLASS", "1. Repo: Session State Request -> $isActive. (Hardware Connected: ${_isGlassConnected.value})") // <--- LOG

        // We only allow setting active to true if hardware is actually connected
        if (isActive && !_isGlassConnected.value) {
            Log.w("DEBUG_GLASS", "1. Repo: REJECTED Session Active. Hardware is disconnected.") // <--- LOG            _isGlassSessionActive.emit(false)
            return
        }
        _isGlassSessionActive.emit(isActive)
    }
}