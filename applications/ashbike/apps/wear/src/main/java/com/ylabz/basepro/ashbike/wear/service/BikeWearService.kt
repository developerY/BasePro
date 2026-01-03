package com.ylabz.basepro.ashbike.wear.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
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

    // State exposed to the UI (e.g. WearBikeScreen)
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

        // 1. Start/Bind the shared BikeForegroundService logic
        // This makes the watch run the exact same logic as the phone
        bikeServiceManager.bindService(this)

        // 2. Observe the data stream from that service
        lifecycleScope.launch {
            bikeServiceManager.rideInfo.collect { realRideInfo ->
                // 3. Pipe the real data to our local state
                _exerciseState.value = realRideInfo
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Prevent leaks
        bikeServiceManager.unbindService(this)
    }
}