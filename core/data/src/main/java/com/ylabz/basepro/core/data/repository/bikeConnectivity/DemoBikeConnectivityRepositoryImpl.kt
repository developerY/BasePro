package com.ylabz.basepro.core.data.repository.bikeConnectivity

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoBikeConnectivityRepositoryImpl @Inject constructor() : BikeConnectivityRepository {

    override suspend fun getBleAddressFromNfc(): String {
        // Simulate a delay for NFC reading.
        delay(500L)
        return "DEMO_BLE_ADDRESS"
    }

    override fun connectBike(bleAddress: String): Flow<BikeConnectionStatus> = flow {
        // Simulate connection delay and then emit a connected status.
        delay(500L)
        emit(BikeConnectionStatus.Connected)
    }
}
