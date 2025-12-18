package com.ylabz.basepro.core.data.repository.bike

import com.ylabz.basepro.core.model.bike.SuspensionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Keeps the state alive across the whole app session
class BikeRepositoryImpl @Inject constructor(
    // If you need to talk to Bluetooth, inject a DataSource here:
    // private val bikeBluetoothDataSource: BikeBluetoothDataSource
) : BikeRepository {

    private val _currentGear = MutableStateFlow(1)
    override val currentGear = _currentGear.asStateFlow()

    private val _suspensionState = MutableStateFlow(SuspensionState.OPEN)
    override val suspensionState = _suspensionState.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    override val isConnected = _isConnected.asStateFlow()

    override suspend fun gearUp() {
        if (_currentGear.value < 12) {
            _currentGear.value += 1
            // TODO: Send command to bikeBluetoothDataSource
        }
    }

    override suspend fun gearDown() {
        if (_currentGear.value > 1) {
            _currentGear.value -= 1
        }
    }

    override suspend fun toggleSuspension() {
        val nextState = when (_suspensionState.value) {
            SuspensionState.OPEN -> SuspensionState.TRAIL
            SuspensionState.TRAIL -> SuspensionState.LOCK
            SuspensionState.LOCK -> SuspensionState.OPEN
        }
        _suspensionState.value = nextState
    }

    override suspend fun setGear(gear: Int) {
        if (gear in 1..12) _currentGear.value = gear
    }
}