package com.ylabz.basepro.applications.bike.database

import com.ylabz.basepro.core.model.bike.BikeRide
import kotlinx.coroutines.flow.Flow

interface BikeRideRepo {
    /** Stream all rides (no locations). */
    fun getAllRides(): Flow<List<BikeRide>> // Assuming BikeRide is your domain model

    /** Now returns rides *plus* their child‐rows */
    fun getAllRidesWithLocations(): Flow<List<RideWithLocations>> // These seem to be DB-specific models

    /** Ditto, single ride + its locations */
    fun getRideWithLocations(rideId: String): Flow<RideWithLocations?>

    /** Insert a bikeRide summary; locations handled separately if needed. */
    suspend fun insert(bikeRide: BikeRide) // Uses domain model BikeRide

    suspend fun insertRideWithLocations(
        ride: BikeRideEntity, // Uses DB entity
        locations: List<RideLocationEntity>
    )

    /** Delete a bikeRide by domain object. */
    suspend fun delete(bikeRide: BikeRide) // Uses domain model BikeRide

    /** Delete a ride by its ID. */
    suspend fun deleteById(rideId: String)

    /** Load a single ride (ignores locations in this example). */
    suspend fun getRideById(rideId: String): BikeRide? // Returns domain model BikeRide

    /** Update a ride's notes. */
    suspend fun updateRideNotes(rideId: String, notes: String)

    /** Wipe entire rides table. */
    suspend fun deleteAll()

    /** Marks a ride as synced to Health Connect and stores the Health Connect ID. */
    suspend fun markRideAsSyncedToHealthConnect(rideId: String, healthConnectId: String?)

    /** Gets the count of rides that are not yet synced to Health Connect. */
    fun getUnsyncedRidesCount(): Flow<Int>

    // It seems your repository mixes usage of domain models (BikeRide) and DB entities (BikeRideEntity).
    // For consistency, it's often better if the repository interface exposes only domain models
    // and handles the mapping to/from entities internally within its implementation.
    // For example, getAllRides() returns Flow<List<BikeRide>>.
    // It would be good if methods like getAllRidesWithLocations also returned Flow<List<DomainRideWithLocations>>
    // and insertRideWithLocations took a domain model.
    // However, I'll stick to the existing pattern for now.
}