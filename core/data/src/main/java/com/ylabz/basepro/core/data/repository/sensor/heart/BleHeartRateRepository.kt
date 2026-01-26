package com.ylabz.basepro.core.data.repository.sensor.heart

import android.util.Log
import com.ylabz.basepro.core.data.repository.sensor.heart.HeartRateRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Reference implementation for Mobile (Phone) Heart Rate.
 *
 * PROD NOTE: In a production environment, this class would inject a BluetoothLeScanner
 * or GATT Manager to connect to physical devices (Polar, Wahoo, etc) and read
 * the standard Heart Rate Measurement characteristic (UUID 0x2A37).
 *
 * CURRENT: Simulates a realistic heart rate curve for development/emulator use.
 *
 * How to upgrade to Real BLE later
 * When you are ready to implement actual Bluetooth connectivity, you would replace the flow { ... } block with a callbackFlow.
 *
 * Inject: BluetoothAdapter / BluetoothManager.
 *
 * Scan: Scan for devices advertising service UUID 180D (Heart Rate).
 *
 * Connect: device.connectGatt(...).
 *
 * Subscribe: Enable notifications on characteristic 2A37.
 *
 * Emit: In onCharacteristicChanged, parse the byte array and call trySend(bpm).
 *
 */
@Singleton
class BleHeartRateRepository @Inject constructor() : HeartRateRepository {

    override val heartRate: Flow<Int> = flow {
        Log.d("BleHeartRateRepo", "Starting Heart Rate Simulation (Phone Mode)")

        // Start at resting heart rate
        var currentBpm = 70
        // Trend determines if HR is generally rising (exercise) or falling (rest)
        var trend = 1

        while (true) {
            emit(currentBpm)

            // Standard BLE sensors update roughly once per second
            delay(1000)

            // Calculate simulation physics
            // 1. Add small random noise (+/- 0 to 2 bpm)
            val noise = Random.nextInt(0, 3)

            // 2. Apply trend
            if (Random.nextBoolean()) {
                currentBpm += (trend * noise)
            }

            // 3. Keep within realistic bounds for a "Ride"
            // If we hit 150, start cooling down. If we hit 60, start warming up.
            if (currentBpm >= 150) {
                trend = -1
            } else if (currentBpm <= 60) {
                trend = 1
            }
        }
    }
}