package com.ylabz.basepro.core.data.repository.bikeConnectivity

import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationRepository
import com.ylabz.basepro.core.data.repository.travel.compass.CompassRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoBikeRepositoryImpl @Inject constructor(
    private val demoLocationRepository: UnifiedLocationRepository,
    private val demoCompassRepository: CompassRepository
) : BikeRepository {

    override val locationFlow: Flow<android.location.Location>
        get() = demoLocationRepository.locationFlow

    override val speedFlow: Flow<Float>
        get() = demoLocationRepository.speedFlow

    override val elevationFlow: Flow<Float>
        get() = demoLocationRepository.elevationFlow

    override val traveledDistanceFlow: Flow<Float>
        get() = demoLocationRepository.traveledDistanceFlow
}
