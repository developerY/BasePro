package com.ylabz.basepro.applications.bike.features.main.ui

import android.app.Application // <-- Import Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.features.main.service.BikeForegroundService
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.jvm.java

@HiltViewModel
class BikeViewModelNew @Inject constructor(
    private val application: Application // <-- Inject Application here
) : ViewModel() {

    // --- State for the Service Connection ---
    private val _bound = MutableStateFlow(false)
    private var bikeService: BikeForegroundService? = null // Renamed property

    // --- State exposed to the UI ---
    private val _uiState = MutableStateFlow<BikeUiState>(BikeUiState.WaitingForGps)
    val uiState: StateFlow<BikeUiState> = _uiState.asStateFlow()

    // --- In-memory UI-only override for total distance ---
    private val _uiPathDistance = MutableStateFlow<Float?>(null)

    // --- Service Connection ---
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("BikeViewModel", "Service Connected. Name: $name")
            val binder = service as BikeForegroundService.LocalBinder
            bikeService = binder.getService() // Updated usage
            _bound.value = true
            Log.d("BikeViewModel", "BikeForegroundService instance obtained: $bikeService") // Updated usage
            observeServiceData()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("BikeViewModel", "Service Disconnected. Name: $name")
            _bound.value = false
            bikeService = null // Updated usage
        }
    }

    private fun observeServiceData() {
        Log.d("BikeViewModel", "observeServiceData called.")
        viewModelScope.launch {
            bikeService?.let { service -> // Updated usage
                Log.d("BikeViewModel", "Starting to collect from service.rideInfo.")
                // Combine the service's "true" data with our UI-only override
                combine(
                    service.rideInfo,
                    _uiPathDistance
                ) { serviceInfo, uiTotalKm ->
                    Log.d("BikeViewModel", "Service.rideInfo emitted: Location: ${serviceInfo.location}, RideState: ${serviceInfo.rideState}, uiTotalKm: $uiTotalKm")
                    serviceInfo.copy(
                        totalTripDistance = uiTotalKm
                    )
                }.map<BikeRideInfo, BikeUiState> { info ->
                    Log.d("BikeViewModel", "Mapping to BikeUiState.Success. Location: ${info.location}, RideState: ${info.rideState}")
                    BikeUiState.Success(info)
                }.catch { e ->
                    Log.e("BikeViewModel", "Error in service.rideInfo flow: ${e.message}", e)
                    emit(BikeUiState.Error(e.localizedMessage ?: "Service error"))
                }.collect { state ->
                    Log.d("BikeViewModel", "Collecting new UI state: $state")
                    _uiState.value = state
                }
            } ?: run {
                Log.w("BikeViewModel", "observeServiceData: bikeService is null, cannot collect.") // Updated usage
            }
        }
    }

    // The context parameter is now gone!
    fun onEvent(event: BikeEvent) {
        when (event) {
            is BikeEvent.SetTotalDistance -> _uiPathDistance.value = event.distanceKm
            BikeEvent.StartRide -> sendCommandToService(BikeForegroundService.ACTION_START_RIDE) // This will now correctly refer to the class
            BikeEvent.StopRide -> sendCommandToService(BikeForegroundService.ACTION_STOP_RIDE)  // This will now correctly refer to the class
        }
    }

    // This function now uses the injected 'application' context
    private fun sendCommandToService(action: String) {
        Log.d("BikeViewModel", "sendCommandToService: $action")
        val intent = Intent(application, BikeForegroundService::class.java).apply { this.action = action }
        application.startService(intent) // Ensures service is running if not already
    }

    // --- Service Lifecycle Management ---
    // Note: bind/unbind still need the Activity/Fragment context, which is fine
    // because these methods are called directly from the UI layer's lifecycle.
    fun bindToService(context: Context) {
        if (!_bound.value) {
            Log.d("BikeViewModel", "bindToService called. Current bound state: ${_bound.value}")
            Intent(context, BikeForegroundService::class.java).also { intent ->
                try {
                    Log.d("BikeViewModel", "Attempting to start service.")
                    context.startService(intent) // Good to ensure it's started, especially if it might not be running
                    Log.d("BikeViewModel", "Attempting to bind service.")
                    val didBind = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                    Log.d("BikeViewModel", "bindService call returned: $didBind")
                    if (!didBind) {
                        Log.e("BikeViewModel", "bindService returned false. Service may not be available or manifest declaration missing.")
                    }
                } catch (e: Exception) {
                    Log.e("BikeViewModel", "Exception during bindToService: ${e.message}", e)
                }
            }
        } else {
            Log.d("BikeViewModel", "bindToService called, but already bound.")
        }
    }

    fun unbindFromService(context: Context) {
        if (_bound.value) {
            Log.d("BikeViewModel", "unbindFromService called.")
            try {
                context.unbindService(serviceConnection)
                _bound.value = false
                bikeService = null // Clear reference immediately // Updated usage
                Log.d("BikeViewModel", "Service unbound successfully.")
            } catch (e: Exception) {
                Log.e("BikeViewModel", "Exception during unbindFromService: ${e.message}", e)
            }
        } else {
            Log.d("BikeViewModel", "unbindFromService called, but not bound.")
        }
    }
}
