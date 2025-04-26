package com.ylabz.basepro.applications.bike.features.trips.ui

sealed class TripsEvent {
    object LoadData : TripsEvent()
    // object AddBikeRide : TripsEvent()
    // data class DeleteItem(val itemId: Int) : TripsEvent()
    object DeleteAll : TripsEvent()
    object OnRetry : TripsEvent()
    //data class OnItemClicked(val itemId: Int) : TripsEvent()
    object StopSaveRide : TripsEvent()
}
