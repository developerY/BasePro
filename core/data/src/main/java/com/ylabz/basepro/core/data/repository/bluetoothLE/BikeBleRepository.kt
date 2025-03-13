package com.ylabz.basepro.core.data.repository.bluetoothLE

import kotlinx.coroutines.flow.Flow

/**
 * Simplified interface for BLE interactions with a bike sensor.
 */
interface BikeBleRepository {
    /**
     * Scans for the target bike device and attempts to connect.
     * Returns a flow of BikeBleData (e.g., speed, battery, etc.).
     */
    fun connectToBike(deviceName: String): Flow<BikeBleData>

    /**
     * Data class representing the sensor data from the bike BLE device.
     * Extend with whatever metrics your BLE device provides.
     */
    data class BikeBleData(
        val speedKmh: Float?,
        val batteryPercent: Int?,
        val rawData: ByteArray? = null
    )
}
