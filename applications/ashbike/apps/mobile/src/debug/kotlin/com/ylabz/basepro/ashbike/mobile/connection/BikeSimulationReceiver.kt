package com.ylabz.basepro.ashbike.mobile.connection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ylabz.basepro.core.data.repository.bike.BikeRepository
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A Backdoor Receiver to drive the app via ADB.
 * Commands:
 * 1. adb shell am broadcast -a com.ylabz.ashbike.SIM_BIKE --ez connected true --ef speed 25.0
 */
@AndroidEntryPoint
class BikeSimulationReceiver : BroadcastReceiver() {

    @Inject lateinit var repository: BikeRepository

    override fun onReceive(context: Context, intent: Intent) {
        val scope = CoroutineScope(Dispatchers.IO)

        when (intent.action) {
            "com.ylabz.ashbike.SIM_BIKE" -> {
                val isConnected = intent.getBooleanExtra("connected", false)
                val battery = intent.getIntExtra("battery", 100)

                // Inject the fake data directly into the Repo
                scope.launch {
                    repository.updateRideInfo(
                        BikeRideInfo.initial().copy(
                            isBikeConnected = isConnected,
                            batteryLevel = if (isConnected) battery else null,
                            motorPower = if (isConnected) 250f else 0f
                        )
                    )
                }
            }
        }
    }
}