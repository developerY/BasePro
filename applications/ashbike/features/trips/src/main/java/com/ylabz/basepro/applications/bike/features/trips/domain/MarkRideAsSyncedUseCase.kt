package com.ylabz.basepro.applications.bike.features.trips.domain

import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import javax.inject.Inject

/**
 * Encapsulates the business logic for marking a bike ride as synced in the local database.
 */
class MarkRideAsSyncedUseCase @Inject constructor(
    private val bikeRideRepo: BikeRideRepo
) {
    suspend operator fun invoke(rideId: String, healthConnectId: String?) {
        bikeRideRepo.markRideAsSyncedToHealthConnect(rideId, healthConnectId)
    }
}
