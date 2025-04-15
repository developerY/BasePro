package com.ylabz.basepro.core.data.repository.bikeConnectivity

import android.util.Log
import com.ylabz.basepro.core.model.bike.BikeMotorData
import kotlinx.coroutines.delay
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
        // Use the NFC reader to retrieve the bike's BLE address.
        // This function is suspendable because reading from NFC might involve waiting for a tag.
        return "Not Set"// nfcReader.readBleAddress()
    }

    override fun connectBike(bleAddress: String): Flow<BikeMotorData> = flow {
        Log.d("BikeConnectivityRepository", "Connected to bike with BLE address: $bleAddress")
        delay(1000L)

        // Attempt to connect to the bike using the BLE address.
        // The bleAdapter.connect() returns a Flow<BikeMotorData> with updates (e.g., battery level, motor power).
        // Here we just re-emit those updates downstream.
        /*bleAdapter.connect(bleAddress).collect { bikeMotorData ->
            emit(bikeMotorData)
        }*/
        emit(BikeMotorData(batteryLevel = 95, motorPower = 250.0f))
    }
}
