package com.ylabz.basepro.ashbike.mobile.features.glass

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

@AndroidEntryPoint
class BikeSimulationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: BikeRepository

    override fun onReceive(context: Context, intent: Intent) {
        // Use IO scope for repository operations
        val scope = CoroutineScope(Dispatchers.IO)

        when (intent.action) {
            // COMMAND 1: SIMULATE BIKE DATA
            "com.ylabz.ashbike.SIM_BIKE" -> {
                val isConnected = intent.getBooleanExtra("connected", false)
                val speed = intent.getFloatExtra("speed", 0f).toDouble()
                val battery = intent.getIntExtra("battery", 100)

                // Construct the "Fake" Data Object
                val simInfo = BikeRideInfo.initial().copy(
                    isBikeConnected = isConnected,
                    currentSpeed = if (isConnected) speed else 0.0,
                    batteryLevel = if (isConnected) battery else null,
                    motorPower = if (isConnected) 250f else 0f
                )

                scope.launch {
                    repository.updateRideInfo(simInfo)
                }
            }

            // COMMAND 2: SIMULATE GLASS CONNECTION
            "com.ylabz.ashbike.SIM_GLASS" -> {
                val isConnected = intent.getBooleanExtra("connected", false)
                scope.launch {
                    repository.updateGlassConnectionState(isConnected)
                }
            }
        }
    }
}