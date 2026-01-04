package com.ylabz.basepro.core.data.repository.sensor

import kotlinx.coroutines.flow.Flow

/**
 * Abstract source of Heart Rate data.
 * Implementations will differ for Phone (BLE) vs Watch (Health Services).
 */
interface HeartRateRepository {
    /**
     * Emits the current heart rate in BPM.
     * Returns 0 or throws exception if disconnected/unavailable.
     */
    val heartRate: Flow<Int>
}