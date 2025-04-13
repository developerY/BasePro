package com.ylabz.basepro.core.data.repository.bikeConnectivity

import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationRepository
import com.ylabz.basepro.core.data.repository.travel.compass.CompassRepository


import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BikeRepositoryImpl @Inject constructor(
    private val unifiedLocationRepository: UnifiedLocationRepository,
    private val compassRepository: CompassRepository // If needed for further data
) : BikeRepository {

    override val locationFlow: Flow<android.location.Location>
        get() = unifiedLocationRepository.locationFlow

    override val speedFlow: Flow<Float>
        get() = unifiedLocationRepository.speedFlow

    override val elevationFlow: Flow<Float>
        get() = unifiedLocationRepository.elevationFlow

    override val traveledDistanceFlow: Flow<Float>
        get() = unifiedLocationRepository.traveledDistanceFlow
}
