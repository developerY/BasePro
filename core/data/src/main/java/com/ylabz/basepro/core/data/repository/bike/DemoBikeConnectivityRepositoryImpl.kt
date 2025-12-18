package com.ylabz.basepro.core.data.repository.bike

import android.util.Log
import com.ylabz.basepro.core.model.bike.BikeMotorData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoBikeConnectivityRepositoryImpl @Inject constructor() : BikeConnectivityRepository {

    override suspend fun getBleAddressFromNfc(): String {
        // Simulate a delay for reading NFC to retrieve the BLE address.
        delay(500L)
        return "DEMO_BLE_ADDRESS"
    }

    override fun connectBike(bleAddress: String): Flow<BikeMotorData> = flow {
        // Simulate connection delay.
        Log.d("BikeConnectivityRepository", "Connected to bike with BLE address: $bleAddress")

        delay(1000L)
        // Emit a demo BikeMotorData instance with fixed values.
        emit(BikeMotorData(batteryLevel = 95, motorPower = 250.0f))

        // Optionally, you could simulate battery drain or motor power fluctuations over time:
        // while (true) {
        //     delay(10000L) // Update every 10 seconds.
        //     // For demonstration, we simply emit the same data.
        //     emit(BikeMotorData(batteryLevel = 95, motorPower = 250))
        // }
    }
}

