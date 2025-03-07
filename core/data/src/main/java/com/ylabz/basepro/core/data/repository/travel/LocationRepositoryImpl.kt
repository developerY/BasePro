package com.ylabz.basepro.core.data.repository.travel

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val context: Context
) : LocationRepository {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    override val currentLocation = _currentLocation.asStateFlow()

    @SuppressLint("MissingPermission")
    override suspend fun updateLocation() {
        // Ensure the UI has requested location permissions before calling this
        try {
            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                _currentLocation.value = LatLng(location.latitude, location.longitude)
            }
        } catch (e: Exception) {
            // Handle exceptions (permissions missing, location off, etc.)
        }
    }
}
