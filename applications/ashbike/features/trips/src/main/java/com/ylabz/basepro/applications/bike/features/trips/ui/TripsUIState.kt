package com.ylabz.basepro.applications.bike.features.trips.ui

import com.ylabz.basepro.applications.bike.database.mapper.BikePro

sealed interface TripsUIState {
    object Loading : TripsUIState
    data class Error(val message: String) : TripsUIState
    data class Success(
        val data: List<BikePro> = emptyList(),
    ) : TripsUIState
}

