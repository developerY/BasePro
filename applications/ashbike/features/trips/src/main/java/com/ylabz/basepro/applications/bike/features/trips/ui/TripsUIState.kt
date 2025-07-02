package com.ylabz.basepro.applications.bike.features.trips.ui

import com.ylabz.basepro.applications.bike.features.trips.ui.model.BikeRideUiModel

/**
 * Represents the different states for the Trips screen UI.
 */
sealed interface TripsUIState {
    object Loading : TripsUIState
    data class Error(val message: String) : TripsUIState
    data class Success(
        val rides: List<BikeRideUiModel> = emptyList()
    ) : TripsUIState
}
