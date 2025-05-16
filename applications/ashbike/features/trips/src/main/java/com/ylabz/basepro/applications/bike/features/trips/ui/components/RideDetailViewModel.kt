package com.ylabz.basepro.applications.bike.features.trips.ui.components

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import com.ylabz.basepro.applications.bike.database.RideWithLocations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ylabz.basepro.applications.bike.database.mapper.BikeRide
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsEvent
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Named

// features/trips/src/main/java/com/ylabz/basepro/applications/bike/features/trips/RideDetailViewModel.kt

@HiltViewModel
class RideDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: BikeRideRepo
) : ViewModel() {

    private val rideId: String = checkNotNull(
        savedStateHandle.get<String>("rideId")
    ) { "rideId required in SavedStateHandle" }

    /** Live DB-backed flow of this ride + its locations */
    val rideWithLocations: StateFlow<RideWithLocations?> =
        repo.getRideWithLocations(rideId)
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun onEvent(event: TripsEvent) {
        when (event) {
            is TripsEvent.UpdateRideNotes -> updateNotes(event.notes)
            TripsEvent.DeleteAll -> TODO()
            is TripsEvent.DeleteItem -> TODO()
            TripsEvent.LoadData -> TODO()
            TripsEvent.OnRetry -> TODO()
            TripsEvent.StopSaveRide -> TODO()
        }
    }
    /** Called by the UI when the notes text changes */
    fun updateNotes(newNotes: String) {
        viewModelScope.launch {
            repo.updateRideNotes(rideId, newNotes)
            // no need to re-load—Flow will emit the updated row automatically
        }
    }
}
