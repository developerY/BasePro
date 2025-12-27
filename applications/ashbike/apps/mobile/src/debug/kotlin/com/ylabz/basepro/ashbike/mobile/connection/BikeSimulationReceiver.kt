package com.ylabz.basepro.ashbike.mobile.connection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ylabz.basepro.core.data.repository.bike.BikeRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A Backdoor Receiver to drive the app via ADB.
 * Command:
 * adb shell am broadcast -a com.ylabz.ashbike.SIM_BIKE --ez connected true --ei battery 85
 */
@AndroidEntryPoint
class BikeSimulationReceiver_not_used : BroadcastReceiver() {

    @Inject lateinit var repository: BikeRepository

    override fun onReceive(context: Context, intent: Intent) {
        val scope = CoroutineScope(Dispatchers.IO)

        when (intent.action) {
            "com.ylabz.ashbike.SIM_BIKE" -> {
                val isConnected = intent.getBooleanExtra("connected", false)
                val battery = intent.getIntExtra("battery", 100)

                scope.launch {
                    // CRITICAL FIX:
                    // 1. Get the CURRENT state (Preserve GPS Speed, Distance, etc.)
                    val currentInfo = repository.rideInfo.value

                    // 2. Only modify the E-Bike hardware fields
                    repository.updateRideInfo(
                        currentInfo.copy(
                            isBikeConnected = isConnected,
                            batteryLevel = if (isConnected) battery else null,
                            motorPower = if (isConnected) 250f else 0f
                            // We do NOT touch currentSpeed, distance, or duration here.
                            // Those are kept from the running GPS service.
                        )
                    )
                }
            }
        }
    }
}