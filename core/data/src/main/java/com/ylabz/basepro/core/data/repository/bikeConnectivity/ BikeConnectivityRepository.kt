package com.ylabz.basepro.core.data.repository.bikeConnectivity

import kotlinx.coroutines.flow.Flow

sealed class BikeConnectionStatus {
    object Connected : BikeConnectionStatus()
    object Disconnected : BikeConnectionStatus()
    data class Error(val message: String) : BikeConnectionStatus()
}

interface BikeConnectivityRepository {
    // Reads a BLE address via NFC.
    suspend fun getBleAddressFromNfc(): String

    // Attempts to connect to the bike over BLE and returns status updates.
    fun connectBike(bleAddress: String): Flow<BikeConnectionStatus>
}