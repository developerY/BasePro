package com.ylabz.basepro.ashbike.wear.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.ylabz.basepro.applications.bike.features.main.service.BikeForegroundService
import com.ylabz.basepro.applications.bike.features.main.service.BikeServiceManager
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BikeWearService : LifecycleService() {

    @Inject
    lateinit var bikeServiceManager: BikeServiceManager

    // Expose the real BikeRideInfo state
    private val _exerciseState = MutableStateFlow(BikeRideInfo.initial())
    val exerciseState = _exerciseState.asStateFlow()

    private val localBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): BikeWearService = this@BikeWearService
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return localBinder
    }

    override fun onCreate() {
        super.onCreate()
        // 1. Bind to the logic engine
        bikeServiceManager.bindService(this)

        // 2. Pipe data from engine to UI state
        lifecycleScope.launch {
            bikeServiceManager.rideInfo.collect { rideInfo ->
                _exerciseState.value = rideInfo
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Prevent leaks
        bikeServiceManager.unbindService(this)
    }

    // --- Added Methods for UI Actions ---

    fun startRide() {
        // Tells the underlying service to start recording a formal ride
        bikeServiceManager.sendCommand(BikeForegroundService.ACTION_START_RIDE)
    }

    fun stopRide() {
        // Tells the underlying service to stop and save the ride
        bikeServiceManager.sendCommand(BikeForegroundService.ACTION_STOP_RIDE)
    }
}