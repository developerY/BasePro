package com.ylabz.basepro.core.data.repository.bikeConnectivity

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface BikeRepository {
    val locationFlow: Flow<Location>
    val speedFlow: Flow<Float>
    val elevationFlow: Flow<Float>
    val traveledDistanceFlow: Flow<Float>
}
