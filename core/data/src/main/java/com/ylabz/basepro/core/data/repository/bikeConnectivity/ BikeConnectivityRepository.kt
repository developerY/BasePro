package com.ylabz.basepro.core.data.repository.bikeConnectivity

import com.ylabz.basepro.core.model.bike.BikeMotorData
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for bike connectivity.
 * This interface exposes functions to read the bike’s BLE address using NFC
 * and to connect to the bike over BLE, returning updates as a flow of BikeMotorData.
 */
interface BikeConnectivityRepository {

    /**
     * Reads the bike's BLE address using NFC.
     *
     * @return a String representing the BLE address.
     * @throws Exception if there is an error reading via NFC.
     */
    suspend fun getBleAddressFromNfc(): String

    /**
     * Connects to the bike over BLE using the provided BLE address.
     *
     * @param bleAddress the BLE address obtained from NFC.
     * @return a Flow of BikeMotorData updates that include battery level and motor power.
     */
    fun connectBike(bleAddress: String): Flow<BikeMotorData>
}