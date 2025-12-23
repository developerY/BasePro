package com.ylabz.basepro.core.data.repository.bike

import android.util.Log
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.SuspensionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // CRITICAL: Ensures the state survives across Screens and Services
class BikeRepositoryImpl @Inject constructor() : BikeRepository {

    // --- 1. RIDE INFO (Speed, Distance, GPS) ---
    // --- 1. THE SINGLE SOURCE OF TRUTH ---
    // This holds ALL state: speed, gears, AND connection status
    private val _rideInfo = MutableStateFlow(BikeRideInfo.initial())
    override val rideInfo = _rideInfo.asStateFlow()

    // --- 2. DERIVED PROPERTIES (No manual syncing needed!) ---

    // We "map" the rideInfo. whenever rideInfo changes, this updates automatically.
    // distinctUntilChanged() ensures we don't emit 'true' multiple times in a row.
    override val isConnected : Flow<Boolean> = _rideInfo
        .map { it.isBikeConnected }
        .distinctUntilChanged()

    override suspend fun updateConnectionState(isConnected: Boolean) {
        // Instead of updating a separate flag, we update the MASTER object
        val current = _rideInfo.value
        if (current.isBikeConnected != isConnected) {
            _rideInfo.emit(current.copy(isBikeConnected = isConnected))
        }
    }

    // 3. SIMULATION FLAG (New!)
    // This tracks if we are forcing the connection manually
    private var isSimulatingConnection = false


    // --- 3. UPDATED FUNCTIONS ---
    override suspend fun updateRideInfo(info: BikeRideInfo) {
        // This is called ~1 time per second by BikeForegroundService
        Log.d("DEBUG_PATH", "2. REPO: Received speed ${info.currentSpeed}. Emitting...") // <--- ADD THIS
        // INTERCEPTOR LOGIC:
        // If the Service sends us data, we check if we are simulating.
        // If we are simulating, we overwrite the service's "disconnected" status
        // with our "connected" status.

        // SIMULATION GUARD:
        // If we are simulating, we IGNORE the "disconnected" signal from the real service
        // and force our fake "connected" data instead.

        val finalInfo = if (isSimulatingConnection) {
            info.copy(
                isBikeConnected = true, // Force True
                batteryLevel = 100,     // Force Battery
                motorPower = 250.0f     // Force Power
            )
        } else {
            info // Pass real data through unchanged
        }

        _rideInfo.emit(finalInfo)
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

    private val _isGlassConnected = MutableStateFlow(false)
    override val isGlassConnected = _isGlassConnected.asStateFlow()


    override suspend fun updateGlassConnectionState(isConnected: Boolean) {
        _isGlassConnected.emit(isConnected)
    }

    // Just for video
    override suspend fun toggleSimulatedConnection() {
        // 1. Toggle our internal flag
        isSimulatingConnection = !isSimulatingConnection

        // 2. Use the flag as the source of truth
        val newStatus = isSimulatingConnection

        val current = _rideInfo.value

        // We only emit ONCE to _rideInfo.
        // The 'isConnected' flow above will update automatically because it watches _rideInfo.
        _rideInfo.emit(
            current.copy(
                isBikeConnected = newStatus,
                batteryLevel = if (newStatus) 100 else null,
                motorPower = if (newStatus) 250.0f else 0.0f
            )
        )
    }

}