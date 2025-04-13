package com.ylabz.basepro.core.data.repository.travel

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow


interface UnifiedLocationRepository {
    /** Emits the raw Location from the device's GPS. */
    val locationFlow: Flow<Location>
    /** Emits the distance (in km) based on accumulated traveled distance. */
    val traveledDistanceFlow: Flow<Float>
    /** Emits the speed in km/h, derived from the raw Location. */
    val speedFlow: Flow<Float>
    /** Emits the elevation (in meters), derived from the raw Location. */
    val elevationFlow: Flow<Float>

}