package com.ylabz.basepro.applications.bike.features.trips.ui

import com.ylabz.basepro.core.model.bike.BikeRide


sealed class TripsEvent {
    object LoadData : TripsEvent()

    // object AddBikeRide : TripsEvent()
    data class DeleteItem(val itemId: String) : TripsEvent()
    data class UpdateRideNotes(val itemId: String, val notes: String) : TripsEvent()
    object DeleteAll : TripsEvent()
    object OnRetry : TripsEvent()

    //data class OnItemClicked(val itemId: Int) : TripsEvent()
    object StopSaveRide : TripsEvent()
    //data class SyncHeathConnect(val ride : BikeRide) : TripsEvent()

    data class SyncRide(val rideId: String) : TripsEvent()

    data class BuildBikeRec(val ride: BikeRide) : TripsEvent()

}
