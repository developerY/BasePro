package com.ylabz.basepro.core.data.repository.bikeConnectivity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BikeConnectivityRepositoryImpl @Inject constructor(
    //private val nfcReader: NfcReader,
    //private val bleAdapter: BleAdapter
) : BikeConnectivityRepository {

    override suspend fun getBleAddressFromNfc(): String {
        // Read the BLE address using NFC.
        //return nfcReader.readBleAddress()
        return ""
    }

    override fun connectBike(bleAddress: String): Flow<BikeConnectionStatus> = flow {
        try {
            // Connect to the bike via BLE.
            // bleAdapter.connect(bleAddress)
            emit(BikeConnectionStatus.Connected)
        } catch (e: Exception) {
            emit(BikeConnectionStatus.Error(e.message ?: "Unknown error"))
        }
    }
}
