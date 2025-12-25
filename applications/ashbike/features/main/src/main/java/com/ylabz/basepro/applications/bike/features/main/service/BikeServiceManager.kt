package com.ylabz.basepro.applications.bike.features.main.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BikeServiceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _service = MutableStateFlow<BikeForegroundService?>(null)

    // The ViewModel observes this. It switches automatically:
    // If service is null -> emptyFlow
    // If service is bound -> service.rideInfo
    val rideInfo: Flow<BikeRideInfo> = _service.flatMapLatest { service ->
        service?.rideInfo ?: emptyFlow()
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            Log.d("BikeServiceManager", "Connected to Service")
            val localBinder = binder as BikeForegroundService.LocalBinder
            _service.value = localBinder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("BikeServiceManager", "Disconnected from Service")
            _service.value = null
        }
    }

    fun bindService(activityContext: Context) {
        val intent = Intent(activityContext, BikeForegroundService::class.java)
        // Start it (so it keeps running in foreground)
        activityContext.startService(intent)
        // Bind to it (so we can talk to it)
        activityContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService(activityContext: Context) {
        try {
            activityContext.unbindService(connection)
        } catch (e: Exception) {
            Log.w("BikeServiceManager", "Service already unbound")
        }
        _service.value = null
    }

    fun sendCommand(action: String) {
        val intent = Intent(context, BikeForegroundService::class.java).apply {
            this.action = action
        }
        context.startService(intent)
    }
}