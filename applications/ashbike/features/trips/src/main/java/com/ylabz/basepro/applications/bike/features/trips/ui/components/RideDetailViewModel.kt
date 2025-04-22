package com.ylabz.basepro.applications.bike.features.trips.ui.components

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ylabz.basepro.applications.bike.database.mapper.BikeRide
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
// features/trips/src/main/java/com/ylabz/basepro/applications/bike/features/trips/RideDetailViewModel.kt

@HiltViewModel
class RideDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: BikeRideRepo
) : ViewModel() {

    private val rideId: String = requireNotNull(savedStateHandle["rideId"])
    private val _ride = MutableStateFlow<BikeRide?>(null)
    val ride: StateFlow<BikeRide?> = _ride

    init {
        viewModelScope.launch {
            _ride.value = repo.getRideById(rideId)
        }
    }
}
