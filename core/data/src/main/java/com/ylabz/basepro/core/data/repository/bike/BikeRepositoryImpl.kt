package com.ylabz.basepro.core.data.repository.bike

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

    private val _isGlassConnected = MutableStateFlow(false)
    override val isGlassConnected = _isGlassConnected.asStateFlow()

    override suspend fun updateConnectionState(isConnected: Boolean) {
        _isConnected.emit(isConnected)
    }

    override suspend fun updateGlassConnectionState(isConnected: Boolean) {
        _isGlassConnected.emit(isConnected)
    }
}