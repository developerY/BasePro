package com.ylabz.basepro.core.data.repository.location


import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    /**
     * Exposes the current location as a Flow<LatLng?>.
     * The repository is responsible for retrieving
     * the device's GPS location, checking permissions, etc.
     */
    val currentLocation: Flow<LatLng?>

    /**
     * Optional: a function to explicitly refresh the location
     * if needed.
     */
    suspend fun updateLocation()
}
