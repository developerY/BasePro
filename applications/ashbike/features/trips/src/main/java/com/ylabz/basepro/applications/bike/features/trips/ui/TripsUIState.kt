package com.ylabz.basepro.applications.bike.features.trips.ui
import com.ylabz.basepro.applications.bike.database.mapper.BikeRide

sealed interface TripsUIState {
    object Loading : TripsUIState
    data class Error(val message: String) : TripsUIState
    data class Success(
        // val bikePro:  List<BikePro>        = emptyList(), // data
        val bikeRides: List<BikeRide> = emptyList()
    ) : TripsUIState
}

